/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package authentication

import (
	"context"
	"crypto/rsa"
	"crypto/tls"
	"fmt"
	"net/http"
	"strconv"
	"strings"
	"time"

	"sgtnserver/api"
	"sgtnserver/internal/common"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
	"github.com/gin-gonic/gin/binding"
	"github.com/go-ldap/ldap/v3"
	"github.com/pkg/errors"
	"github.com/shaj13/go-guardian/v2/auth"
	"github.com/shaj13/go-guardian/v2/auth/strategies/basic"
	"github.com/shaj13/go-guardian/v2/auth/strategies/jwt"
	"github.com/shaj13/go-guardian/v2/auth/strategies/token"
	"github.com/shaj13/go-guardian/v2/auth/strategies/union"
	"github.com/shaj13/libcache"
	_ "github.com/shaj13/libcache/fifo"
)

var (
	unionStrategy                               union.Union
	ldapStrategy, jwtStrategy, appTokenStrategy auth.Strategy
	jwtSecretKeeper                             jwt.SecretsKeeper

	rsaPrivateKey *rsa.PrivateKey
	rsaPublicKey  *rsa.PublicKey
)

const jwtSecretID = "secret-id"

type credentialsFn func(r *http.Request) (string, string, error)

func (fn credentialsFn) Credentials(r *http.Request) (string, string, error) {
	return fn(r)
}

func init() {
	Init()
}

func Init() {
	if !config.Settings.Authentication.Enable {
		return
	}

	var err error
	rsaPrivateKey, err = common.GetPrivateKeyFromFile(config.Settings.Crypto.RSAPrivateKeyFile)
	if err != nil {
		logger.Log.Fatal(err.Error())
	}
	rsaPublicKey, err = common.GetPublicKeyFromFile(config.Settings.Crypto.RSAPublicKeyFile)
	if err != nil {
		logger.Log.Fatal(err.Error())
	}

	setupGuardian()

	api.Register(&authRouter{})
}

func setupGuardian() {
	jwtSecret, err := common.Decrypt([]byte(config.Settings.Authentication.JWTSecret), rsaPublicKey)
	if err != nil {
		logger.Log.Fatal(err.Error())
	}
	jwtSecretKeeper = jwt.StaticSecret{
		ID:        jwtSecretID,
		Secret:    []byte(jwtSecret),
		Algorithm: jwt.HS256,
	}

	cacheObj := libcache.FIFO.New(0)
	cacheObj.SetTTL(time.Minute * 5)
	ldapStrategy = basic.NewCached(ldapAuthFunc, cacheObj, basic.SetParser(credentialsFn(func(r *http.Request) (string, string, error) {
		params := struct {
			UserName string `form:"username" binding:"required"`
			Password string `form:"password" binding:"required"`
		}{}
		err := binding.Query.Bind(r, &params)
		return params.UserName, params.Password, err
	})))
	jwtStrategy = jwt.New(cacheObj, jwtSecretKeeper, token.SetParser(token.XHeaderParser(AuthJWTHeaderKey)))
	unionStrategy = union.New(jwtStrategy, ldapStrategy)
	appTokenStrategy = token.New(func(ctx context.Context, r *http.Request, tk string) (auth.Info, time.Time, error) {
		params := AppIdHeader{}
		if err := ctx.(*gin.Context).ShouldBindHeader(&params); err != nil {
			return nil, time.Time{}, err
		}

		tkBytes, err := common.Decrypt([]byte(tk), rsaPublicKey)
		if err != nil {
			return nil, time.Time{}, err
		}
		parts := strings.Split(string(tkBytes), common.CharColon)
		appId, username := parts[0], parts[2]
		if appId != params.AppId {
			return nil, time.Time{}, errors.Errorf("invalid appId '%s' is provided", params.AppId)
		}
		expTime, err := strconv.ParseInt(parts[1], 10, 64)
		if err != nil {
			return nil, time.Time{}, errors.Errorf("invalid expTime '%s' in token", parts[1])
		}
		if expTime <= time.Now().UnixMilli() {
			return nil, time.Time{}, errors.New("token is expired")
		}

		return auth.NewDefaultUser(username, "", nil, auth.Extensions{AuthAppIdKey: []string{appId}}), time.Time{}, nil
	}, cacheObj, token.SetParser(token.XHeaderParser(AuthTokenKey)))
}

func ldapAuthFunc(ctx context.Context, r *http.Request, userName, password string) (auth.Info, error) {
	l, err := GetLDAPConn()
	if err != nil {
		return nil, err
	}
	defer l.Close()

	err = l.Bind(fmt.Sprintf(config.Settings.Authentication.LDAPUserDN, userName), password)
	if err != nil {
		return nil, err
	}

	searchRequest := ldap.NewSearchRequest(
		config.Settings.Authentication.LDAPBaseDN,
		ldap.ScopeWholeSubtree, ldap.NeverDerefAliases, 0, 0, false,
		fmt.Sprintf(config.Settings.Authentication.LDAPFilter, ldap.EscapeFilter(userName)),
		[]string{"dn"},
		nil,
	)

	sr, err := l.Search(searchRequest)
	if err != nil {
		return nil, err
	}

	if len(sr.Entries) != 1 {
		return nil, errors.New("User does not exist or too many entries returned")
	}

	return auth.NewDefaultUser(userName, userName, nil, nil), nil
}

var GetLDAPConn = func() (ldap.Client, error) {
	tlsConfig := tls.Config{MinVersion: tls.VersionTLS13, InsecureSkipVerify: true}
	return ldap.DialURL(config.Settings.Authentication.LDAPServerURL, ldap.DialWithTLSConfig(&tlsConfig))
}
