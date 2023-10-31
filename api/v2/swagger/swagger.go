/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package swagger

import (
	v2 "sgtnserver/api/v2"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

// @title Singleton Service API
// @version 2.0
// @description This is a Singleton server
// @termsOfService

// @tag.name about-version-api
// @tag.name formatting-api
// @tag.name formatting-pattern-api
// @tag.name locale-api
// @tag.name countryflag-api
// @tag.name translation-product-api
// @tag.name translation-product-component-api
// @tag.name translation-product-component-key-api
// @tag.name translation-sync-api
// @tag.name translation-with-pattern-api
// @tag.description

// @contact.name Singleton Team
// @contact.url https://github.com/vmware/singleton
// @contact.email

// @license.name EPL-2.0
// @license.url https://www.eclipse.org/legal/epl-2.0/

type swaggerUI struct{}

func (ui *swaggerUI) Init(ginEngine *gin.RouterGroup) {
	if !config.Settings.SwaggerUI {
		return
	}

	logger.Log.Debug("Initialize SwaggerUI")
	SwaggerInfo.BasePath = v2.APIRoot
	ginEngine.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
}

func init() {
	v2.Register(&swaggerUI{})
}
