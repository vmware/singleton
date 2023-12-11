/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"fmt"
	"net/http"
	"testing"

	"sgtnserver/api"
	"sgtnserver/api/authentication"
	"sgtnserver/internal/config"

	"github.com/gavv/httpexpect/v2"
	"github.com/go-ldap/ldap/v3"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/suite"
)

const (
	authLoginURL = "/auth/login"
	authTokenURL = "/auth/token"
)

type MockedLDAPClient struct {
	mock.Mock
	ldap.Client
}

func (m *MockedLDAPClient) Close() error {
	args := m.Called()
	return args.Error(0)
}

func (m *MockedLDAPClient) Bind(username, password string) error {
	args := m.Called(username, password)
	return args.Error(0)
}

func (m *MockedLDAPClient) Search(sr *ldap.SearchRequest) (*ldap.SearchResult, error) {
	args := m.Called(sr)
	return args.Get(0).(*ldap.SearchResult), args.Error(1)
}

type AuthTestSuite struct {
	suite.Suite
	mockLDAPClient                                *MockedLDAPClient
	username, password, appId, jwtToken, appToken string
	e                                             *httpexpect.Expect
}

func (suite *AuthTestSuite) SetupSuite() {
	suite.mockLDAPClient = &MockedLDAPClient{}
	suite.username = "username"
	suite.password = "password"
	suite.appId = "ABC"

	config.Settings.Authentication.Enable = true
	defer func() {
		config.Settings.Authentication.Enable = false
	}()
	authentication.Init()
	suite.e = CreateHTTPExpect(suite.T(), api.InitServer())

	authentication.GetLDAPConn = func() (ldap.Client, error) {
		return suite.mockLDAPClient, nil
	}

	suite.Login()
	suite.CreateAppToken()
}

func TestAuthTestSuite(t *testing.T) {
	suite.Run(t, new(AuthTestSuite))
}

func (suite *AuthTestSuite) Login() {
	suite.mockLDAPClient.On("Close").Once().Return(nil)
	suite.mockLDAPClient.On("Bind", fmt.Sprintf(config.Settings.Authentication.LDAPUserDN, suite.username), suite.password).Once().Return(nil)
	suite.mockLDAPClient.On("Search", mock.AnythingOfType("*ldap.SearchRequest")).Once().Return(&ldap.SearchResult{Entries: []*ldap.Entry{{}}}, nil)

	resp := suite.e.POST(authLoginURL).WithQuery("username", suite.username).WithQuery("password", suite.password).WithQuery("expireDays", 365).Expect()

	bError, data := GetErrorAndData(resp.Body().Raw())
	suite.Equal(http.StatusOK, bError.Code)
	suite.NotNil(data)
	suite.Equal(suite.username, data.(map[string]interface{})["username"])
	jwtToken := data.(map[string]interface{})[authentication.AuthJWTHeaderKey]
	suite.NotNil(jwtToken)
	suite.jwtToken = jwtToken.(string)

	suite.mockLDAPClient.AssertExpectations(suite.T())
}

func (suite *AuthTestSuite) CreateAppToken() {
	resp := suite.e.POST(authTokenURL).WithQuery(authentication.AuthAppIdKey, suite.appId).WithHeader(authentication.AuthJWTHeaderKey, suite.jwtToken).Expect()
	bError, data := GetErrorAndData(resp.Body().Raw())
	suite.Equal(http.StatusOK, bError.Code)
	appToken := data.(map[string]interface{})[authentication.AuthTokenKey]
	suite.NotNil(appToken)
	suite.appToken = appToken.(string)
}

func (suite *AuthTestSuite) TestWrongPassword() {
	resp := suite.e.POST(authLoginURL).WithQuery("username", suite.username).WithQuery("password", "wrong_password").WithQuery("expireDays", 365).Expect()

	bError, data := GetErrorAndData(resp.Body().Raw())
	suite.Equal(http.StatusUnauthorized, bError.Code)
	suite.Nil(data)

	suite.mockLDAPClient.AssertExpectations(suite.T())
}

func (suite *AuthTestSuite) TestWrongUser() {
	username := "wrong_username"
	suite.mockLDAPClient.On("Close").Once().Return(nil)
	suite.mockLDAPClient.On("Bind", fmt.Sprintf(config.Settings.Authentication.LDAPUserDN, username), suite.password).Once().Return(assert.AnError)

	resp := suite.e.POST(authLoginURL).WithQuery("username", username).WithQuery("password", suite.password).WithQuery("expireDays", 365).Expect()

	bError, data := GetErrorAndData(resp.Body().Raw())
	suite.Equal(http.StatusUnauthorized, bError.Code)
	suite.Nil(data)

	suite.mockLDAPClient.AssertExpectations(suite.T())
}

func (suite *AuthTestSuite) TestInvalidJwtToken() {
	resp := suite.e.POST(authTokenURL).WithQuery(authentication.AuthAppIdKey, suite.appId).WithHeader(authentication.AuthJWTHeaderKey, suite.jwtToken+"invalid_token").Expect()
	bError, data := GetErrorAndData(resp.Body().Raw())
	suite.Equal(http.StatusUnauthorized, bError.Code)
	suite.Nil(data)
}

func (suite *AuthTestSuite) TestAuthAppToken() {
	resp := suite.e.GET(GetPatternByLocaleURL, Locale).WithHeader(authentication.AuthTokenKey, suite.appToken).WithHeader(authentication.AuthAppIdKey, suite.appId).WithQuery(api.ScopeAPIKey, "plurals").Expect()
	bError, data := GetErrorAndData(resp.Body().Raw())
	suite.Equal(http.StatusOK, bError.Code)
	suite.NotNil(data)
}

func (suite *AuthTestSuite) TestWrongAppId() {
	wrongAppId := "wrong_app_id"
	resp := suite.e.GET(GetPatternByLocaleURL, Locale).WithHeader(authentication.AuthTokenKey, suite.appToken).WithHeader(authentication.AuthAppIdKey, wrongAppId).WithQuery(api.ScopeAPIKey, "plurals").Expect()
	bError, data := GetErrorAndData(resp.Body().Raw())
	suite.Equal(http.StatusUnauthorized, bError.Code)
	suite.Nil(data)
}

func (suite *AuthTestSuite) TestInvalidAppToken() {
	appToken := suite.appToken + "1"
	resp := suite.e.GET(GetPatternByLocaleURL, Locale).WithHeader(authentication.AuthTokenKey, appToken).WithHeader(authentication.AuthAppIdKey, suite.appId).WithQuery(api.ScopeAPIKey, "plurals").Expect()
	bError, data := GetErrorAndData(resp.Body().Raw())
	suite.Equal(http.StatusUnauthorized, bError.Code)
	suite.Nil(data)
}
