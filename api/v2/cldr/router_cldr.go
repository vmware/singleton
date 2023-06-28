/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldr

import (
	v2 "sgtnserver/api/v2"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
)

type cldrRouter struct{}

func (cldr *cldrRouter) Init(e *gin.RouterGroup) {
	logger.Log.Debug("Initialize CLDR router")
	formattingGroup := e.Group("/formatting")
	{
		formattingGroup.GET("/patterns", GetPatternDataByLangReg)
		formattingGroup.GET("/patterns/locales/:locale", GetPatternByLocale)
		formattingGroup.GET("/date/timezoneNameList", GetTimeZoneNames)
	}

	e.GET("/locale/regionList", GetRegionListOfLanguages)
}

func init() {
	v2.Register(&cldrRouter{})
}
