/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package countryflag

import (
	"sgtnserver/api"
	"sgtnserver/internal/logger"
	"sgtnserver/modules/countryflag"

	"github.com/gin-gonic/gin"
)

const (
	flagTypeJSON = "json"
	flagTypeSvg  = "svg"
)

// GetCountryFlag godoc
// @Summary Get the svg image of a country flag
// @Description Get a country flag with region and scale
// @tags countryflag-api
// @Produce json
// @Param region query string true "a string which represents a region, e.g. US"
// @Param scale query int false "scale of the flag. 1 represents 1x1, 2 represents 3x2, default is 1"
// @Param type query string false "response types. Available types are 'svg' and 'json'. Default type is 'json'"
// @Success 200 {object} api.Response "OK"
// @Success 207 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 500 {string} string "Internal Server Error"
// @Router /image/countryFlag [get]
func GetCountryFlag(c *gin.Context) {
	params := struct {
		Region string                `form:"region" binding:"required,region"`
		Scale  countryflag.FlagScale `form:"scale" binding:"oneof=1 2"`
		Type   string                `form:"type" binding:"omitempty,oneof=json svg"`
	}{Scale: countryflag.Flag1x1, Type: flagTypeJSON}

	if err := api.ExtractParameters(c, nil, &params); err != nil {
		return
	}

	flag, err := countryflag.GetFlag(logger.NewContext(c, c.MustGet(api.LoggerKey)), params.Region, params.Scale)
	switch params.Type {
	case flagTypeJSON:
		data := gin.H{"image": flag, "type": "svg", "region": params.Region}
		api.HandleResponse(c, data, err)
	case flagTypeSvg:
		c.Data(api.ToBusinessError(err).HTTPCode, "image/svg+xml", []byte(flag))
	}
}
