/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import (
	v2 "sgtnserver/api/v2"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
)

type translationRouter struct{}

func (r *translationRouter) Init(e *gin.RouterGroup) {
	logger.Log.Debug("Initialize translation router")

	tranGroup := e.Group("/translation", HandleAllowList, HandleVersionFallback)
	{
		productPart := "/products/:productName/versions/:version"

		// Product APIs
		tranGroup.GET(productPart, GetMultipleBundles)

		tranGroup.GET(productPart+"/localelist", GetAvailableLocales)
		tranGroup.GET(productPart+"/componentlist", GetAvailableComponents)

		// Component API
		tranGroup.GET(productPart+"/locales/:locale/components/:component", GetBundle)

		// Key APIs
		tranGroup.GET(productPart+"/locales/:locale/components/:component/keys/:key", GetString)
	}

	e.PUT("/translation/products/:productName/versions/:version", PutBundles)
}

func init() {
	v2.Register(&translationRouter{})
}
