/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package v2

import (
	"sgtnserver/api"
	"sgtnserver/api/authentication"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
)

var routers []api.Router

func init() {
	api.Register(&router{})
}

type router struct{}

func (v2r *router) Init(g *gin.RouterGroup) {
	logger.Log.Debug("Initialize V2 router")

	group := g.Group(APIRoot)
	if config.Settings.Authentication.Enable {
		group.Use(authentication.AppTokenAuthenticate)
	}

	for _, r := range routers {
		r.Init(group)
	}
}

func Register(r api.Router) {
	routers = append(routers, r)
}
