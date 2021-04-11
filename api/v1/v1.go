/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package v1

import (
	"sgtnserver/api"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
)

const APIRoot = "/i18n/api/v1"

const DefaultLocale = "en"

var routers []api.Router

func init() {
	api.Register(&router{})
}

type router struct{}

func (r *router) Init(g *gin.RouterGroup) {
	logger.Log.Debug("Initialize V1 router")

	group := g.Group(APIRoot)

	for _, r := range routers {
		r.Init(group)
	}
}

func Register(r api.Router) {
	routers = append(routers, r)
}
