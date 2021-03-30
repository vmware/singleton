/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package v2

import (
	"github.com/gin-gonic/gin"

	"sgtnserver/api"
	"sgtnserver/internal/logger"
)

var routers []api.Router

func init() {
	api.Register(&router{})
}

type router struct{}

func (v2r *router) Init(g *gin.RouterGroup) {
	logger.Log.Debug("Initialize V2 router")

	v2Group := g.Group(APIRoot)

	for _, r := range routers {
		r.Init(v2Group)
	}
}

func Register(r api.Router) {
	routers = append(routers, r)
}
