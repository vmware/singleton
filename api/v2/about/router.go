/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package about

import (
	v2 "sgtnserver/api/v2"
	"sgtnserver/api/v2/translation"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
)

type aboutRouter struct{}

func (r *aboutRouter) Init(e *gin.RouterGroup) {
	logger.Log.Debug("Initialize about router")

	e.GET("/about/version", translation.HandleAllowList, translation.HandleVersionFallback, GetAboutInfo)
}

func init() {
	v2.Register(&aboutRouter{})
}
