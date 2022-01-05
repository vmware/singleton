/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package combine

import (
	v2 "sgtnserver/api/v2"
	"sgtnserver/api/v2/translation"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
)

type combineRouter struct{}

func (r *combineRouter) Init(e *gin.RouterGroup) {
	logger.Log.Debug("Initialize combination router")

	e.GET("/locale/supportedLanguageList", translation.HandleAllowList, translation.HandleVersionFallback, getLanguageListOfDispLang)

	// translations and pattern combined interface
	e.GET("/combination/translationsAndPattern", translation.HandleAllowList, translation.HandleVersionFallback, getCombinedData)

	// translations and pattern combined interface by POST (deprecated)
	e.POST("/combination/translationsAndPattern", translation.HandleAllowList, getCombinedDataByPost)

}
func init() {
	v2.Register(&combineRouter{})
}
