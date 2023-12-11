/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package authentication

import (
	"bytes"
	"fmt"
	"net/http"
	"time"

	"sgtnserver/api"
	"sgtnserver/internal/common"
	"sgtnserver/internal/config"
	"sgtnserver/internal/sgtnerror"

	"github.com/gin-gonic/gin"
	"github.com/pkg/errors"
	"github.com/shaj13/go-guardian/v2/auth"
	"github.com/shaj13/go-guardian/v2/auth/strategies/jwt"
)

func LDAPAuthenticate(c *gin.Context) {
	log := api.GetLogger(c)
	log.Debug("Executing LDAPAuthenticate")
	user, err := ldapStrategy.Authenticate(c, c.Request)
	if err != nil {
		log.Error(fmt.Sprintf("%+v", err))
		c.AbortWithStatusJSON(http.StatusUnauthorized, api.Response{Error: api.ToBusinessError(sgtnerror.StatusUnauthorized)})
		return
	}
	log.Info(fmt.Sprintf("User %s Authenticated", user.GetUserName()))
	c.Request = auth.RequestWithUser(user, c.Request)
}

func JWTAuthenticate(c *gin.Context) {
	log := api.GetLogger(c)
	log.Debug("Executing JWTAuthenticate")
	user, err := jwtStrategy.Authenticate(c, c.Request)
	if err != nil {
		log.Error(fmt.Sprintf("%+v", err))
		c.AbortWithStatusJSON(http.StatusUnauthorized, api.Response{Error: api.ToBusinessError(sgtnerror.StatusUnauthorized)})
		return
	}
	log.Info(fmt.Sprintf("User %s Authenticated", user.GetUserName()))
	c.Request = auth.RequestWithUser(user, c.Request)
}

// func UnionAuthenticate(c *gin.Context) {
// 	log := api.GetLogger(c)
// 	log.Debug("Executing UnionAuthenticate")
// 	_, user, err := unionStrategy.AuthenticateRequest(c.Request)
// 	if err != nil {
// 		log.Error(fmt.Sprintf("%+v", err))
// 		c.AbortWithStatusJSON(http.StatusUnauthorized, api.Response{Error: api.ToBusinessError(sgtnerror.StatusUnauthorized)})
// 		return
// 	}
// 	log.Info(fmt.Sprintf("User %s Authenticated", user.GetUserName()))
// 	c.Request = auth.RequestWithUser(user, c.Request)
// }

func AppTokenAuthenticate(c *gin.Context) {
	log := api.GetLogger(c)
	log.Debug("Executing AppTokenAuthenticate")

	user, err := appTokenStrategy.Authenticate(c, c.Request)
	if err == nil {
		params := AppIdHeader{}
		if err = c.ShouldBindHeader(&params); err == nil {
			if appId := user.GetExtensions().Get(AuthAppIdKey); appId != params.AppId {
				err = errors.Errorf("invalid appId '%s' is provided", params.AppId)
			}
		}
	}
	if err != nil {
		log.Error(err.Error())
		c.AbortWithStatusJSON(http.StatusUnauthorized, api.Response{Error: api.ToBusinessError(sgtnerror.StatusUnauthorized)})
		return
	}

	c.Request = auth.RequestWithUser(user, c.Request)
}

func CreateJWTToken(c *gin.Context) {
	params := struct {
		ExpDuration int64 `form:"expireDays" binding:"required"`
	}{}
	if err := api.ExtractParameters(c, nil, &params); err != nil {
		return
	}

	u := auth.User(c.Request)
	token, err := jwt.IssueAccessToken(u, jwtSecretKeeper, jwt.SetExpDuration(time.Hour*24*time.Duration(params.ExpDuration)))
	data := gin.H{UserNameKey: u.GetUserName(), AuthJWTHeaderKey: token}
	api.HandleResponse(c, data, err)
}

func CreateAppToken(c *gin.Context) {
	params := struct {
		AppId string `form:"appId" binding:"required"`
	}{}
	if err := api.ExtractParameters(c, nil, &params); err != nil {
		return
	}

	user := auth.User(c.Request)

	now := time.Now()
	issueTime := now.UnixMilli()
	expTime := now.Add(config.Settings.Authentication.AppTokenExpDuration).UnixMilli()

	var b bytes.Buffer
	fmt.Fprintf(&b, "%s:%d:%s:%d", params.AppId, expTime, user.GetUserName(), issueTime)
	token, err := common.Encrypt(b.Bytes(), rsaPrivateKey)
	if err == nil {
		auth.Append(appTokenStrategy, token, user)
	}

	data := gin.H{AuthTokenKey: string(token)}
	api.HandleResponse(c, data, err)
}
