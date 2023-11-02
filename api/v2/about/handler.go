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

	data := gin.H{}
	var returnErr *sgtnerror.MultiError
	ctx := logger.NewContext(c, c.MustGet(api.LoggerKey))
	serviceInfo, serviceErr := serverinfo.GetServerInfo(ctx)
	returnErr = sgtnerror.Append(returnErr, serviceErr)
	if serviceErr == nil {
		data["service"] = serviceInfo
	}

	switch {
	case params.ProductName != "" && version != "":
		bundleInfo, err := translationservice.GetService().GetVersionInfo(ctx, params.ProductName, version)
		returnErr = sgtnerror.Append(returnErr, err)
		if err == nil {
			data["bundle"] = gin.H{api.ProductNameAPIKey: params.ProductName, api.VersionAPIKey: version, "changeId": bundleInfo["drop_id"]}
		}
	case params.ProductName == "" && version == "":
	case params.ProductName == "" || version == "":
		returnErr = sgtnerror.Append(returnErr, sgtnerror.StatusBadRequest.WithUserMessage("'productName' and 'version' must be provided together"))
	}

	if len(data) == 0 {
		data = nil
	}

	api.HandleResponse(c, data, returnErr)
}
