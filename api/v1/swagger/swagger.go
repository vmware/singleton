/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package swagger

import (
	v1 "sgtnserver/api/v1"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"

	"github.com/gin-gonic/gin"
	ginSwagger "github.com/swaggo/gin-swagger"
	"github.com/swaggo/gin-swagger/swaggerFiles"
)

// @title Singleton Service API
// @version 1.0
// @description This is a Singleton server
// @termsOfService

// @tag.name formatting-api
// @tag.name formatting-pattern-api
// @tag.name translation-component-api
// @tag.name translation-key-api
// @tag.name translation-product-api
// @tag.name translation-product-component-api
// @tag.name translation-product-component-key-api
// @tag.name translation-sync-api
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
	SwaggerInfo.BasePath = v1.APIRoot
	ginEngine.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
}

func init() {
	v1.Register(&swaggerUI{})
}
