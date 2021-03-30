/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldr

import (
	v1 "sgtnserver/api/v1"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
)

type cldrRouter struct{}

func (cldr *cldrRouter) Init(e *gin.RouterGroup) {
	logger.Log.Debug("Initialize CLDR router")

	e.GET("/i18nPattern", GetPatternData)
}

func init() {
	v1.Register(&cldrRouter{})
}
