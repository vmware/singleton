/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package authentication

import (
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
)

type authRouter struct{}

func (cldr *authRouter) Init(e *gin.RouterGroup) {
	logger.Log.Debug("Initialize authentication router")

	group := e.Group("/auth")
	group.POST("/login", LDAPAuthenticate, CreateJWTToken)
	group.POST("/token", JWTAuthenticate, CreateAppToken)
}
