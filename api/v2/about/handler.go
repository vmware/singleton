/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package about

import (
	"sgtnserver/api"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/serverinfo"
	"sgtnserver/modules/translation/translationservice"

	"github.com/gin-gonic/gin"
)

// GetAboutInfo godoc
// @Summary Get service build information and translation bundle information
// @Description Get service build and translation bundle information
// @Tags about-version-api
// @Produce json
// @Param productName query string false "product name"
// @Param version query string false "version"
// @Success 200 {object} api.Response "OK"
// @Success 207 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 500 {string} string "Internal Server Error"
// @Router /about/version [get]
func GetAboutInfo(c *gin.Context) {
	params := struct {
		ProductName string `form:"productName" binding:"omitempty,alphanum"`
		Version     string `form:"version" binding:"omitempty,version"`
	}{}
	if err := api.ExtractParameters(c, nil, &params); err != nil {
		return
	}

	version := c.GetString(api.SgtnVersionKey)

	var returnErr *sgtnerror.MultiError
	ctx := logger.NewContext(c, c.MustGet(api.LoggerKey))
	serviceInfo := serverinfo.GetServerInfo(ctx)
	data := gin.H{"service": serviceInfo}
	if params.ProductName != "" && version != "" {
		if bundleInfo, err := translationservice.GetService().GetVersionInfo(ctx, params.ProductName, version); err == nil {
			data["bundle"] = gin.H{api.ProductNameAPIKey: params.ProductName, api.VersionAPIKey: version, "changeId": bundleInfo["drop_id"]}
		} else {
			returnErr = sgtnerror.Append(nil, err) // Add nil because service is always successful.
		}
	}

	api.HandleResponse(c, data, returnErr)
}
