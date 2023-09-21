/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package countryflag

import (
	v2 "sgtnserver/api/v2"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
)

type router struct{}

func (cldr *router) Init(e *gin.RouterGroup) {
	logger.Log.Debug("Initialize countryflag router")
	e.GET("/image/countryFlag", GetCountryFlag)
}

func init() {
	v2.Register(&router{})
}
