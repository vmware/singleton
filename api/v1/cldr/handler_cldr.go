/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldr

import (
	"strings"

	"sgtnserver/api"
	"sgtnserver/internal/logger"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/cldrservice"
	"sgtnserver/modules/cldr/coreutil"
	"sgtnserver/modules/cldr/localeutil"

	"github.com/gin-gonic/gin"
)

// GetPatternData godoc
// @Summary Get pattern from CLDR
// @Description Get pattern from CLDR by locale and scope
// @tags formatting-pattern-api
// @Produce json
// @Param locale query string true "locale String. e.g. 'en-US'"
// @Param scope query string true "pattern category string, separated by commas. e.g. 'dates,numbers,currencies,plurals,measurements,dateFields'"
// @Param scopeFilter query string false "a string for filtering the pattern data, separated by comma and underline. e.g. 'dates_eras,dates_dateTimeFormats'"
// @Success 200 {object} api.Response "OK"
// @Success 207 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /i18nPattern [get]
// @Deprecated
func GetPatternData(c *gin.Context) {
	params := PatternByLocaleReq{}
	if err := api.ExtractParameters(c, nil, &params); err != nil {
		return
	}

	ctx := logger.NewContext(c, c.MustGet(api.LoggerKey))
	cldrLocale, dataMap, err := cldrservice.GetPatternByLocale(ctx, params.Locale, params.Scope, params.ScopeFilter)
	var data interface{}
	if len(dataMap) > 0 {
		parts := strings.Split(cldrLocale, cldr.LocalePartSep)
		region := coreutil.ParseRegion(parts)
		if region == "" {
			region, _ = localeutil.GetLocaleDefaultRegion(ctx, cldrLocale)
		}
		data = PatternData{
			LocaleID:   cldrLocale,
			Language:   parts[0],
			Region:     region,
			Categories: dataMap,
		}
	}

	api.HandleResponse(c, data, err)
}
