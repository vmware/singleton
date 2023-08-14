/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import (
	v1 "sgtnserver/api/v1"
	v2Translation "sgtnserver/api/v2/translation"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
)

type translationRouter struct{}

func (r *translationRouter) Init(e *gin.RouterGroup) {
	logger.Log.Debug("Initialize translation router")

	tranGroup := e.Group("/translation", v2Translation.HandleVersionFallback)
	{
		// Component API
		tranGroup.GET("/component", GetBundle2)
		tranGroup.GET("/components", GetMultipleBundles2)

		// translation-key-api
		tranGroup.GET("/string", GetString2)

		// Translation Product API
		tranGroup.GET("", GetProduct)
		tranGroup.GET("/product/:productName/version/:version/supportedLocales", GetAvailableLocales)

		// Translation Product Component API
		tranGroup.GET("/product/:productName/component/:component", GetBundle)
		tranGroup.GET("/product/:productName/components/:components", GetMultipleBundles)

		// Translation Product Component Key API
		tranGroup.GET("/product/:productName/component/:component/key/:key", GetString)
		tranGroup.GET("/product/:productName/key/:key", GetString3)
	}

	// Translation Product Component API
	e.GET("/bundles/components", v2Translation.HandleVersionFallback, GetAvailableComponents)

	// Translation Sync API
	e.PUT("/translation/product/:productName/version/:version", PutBundles)

}
func init() {
	v1.Register(&translationRouter{})
}
