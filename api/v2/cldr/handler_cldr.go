/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldr

import (
	"strings"

	"sgtnserver/api"
	"sgtnserver/internal/common"
	"sgtnserver/internal/logger"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/cldrservice"
	"sgtnserver/modules/cldr/coreutil"
	"sgtnserver/modules/cldr/localeutil"

	"github.com/gin-gonic/gin"
)

// GetPatternByLocale godoc
// @Summary Get pattern data
// @Description Get pattern data in a specified locale
// @tags formatting-pattern-api
// @Produce json
// @Param locale path string true "locale String. e.g. 'en-US'"
// @Param scope query string true "pattern category string, separated by commas. e.g. 'dates,numbers,currencies,plurals,measurements,dateFields'"
// @Param scopeFilter query string false "a string for filtering the pattern data, separated by comma and underline. e.g. 'dates_eras,dates_dateTimeFormats'"
// @Success 200 {object} api.Response "OK"
// @Success 206 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /formatting/patterns/locales/{locale} [get]
func GetPatternByLocale(c *gin.Context) {
	locale := struct {
		Locale string `uri:"locale" binding:"locale"`
	}{}
	scope := PatternScope{}
	if err := api.ExtractParameters(c, &locale, &scope); err != nil {
		return
	}

	ctx := logger.NewContext(c, c.MustGet(api.LoggerKey))
	cldrLocale, dataMap, err := cldrservice.GetPatternByLocale(ctx, locale.Locale, scope.Scope, scope.ScopeFilter)
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

// GetPatternDataByLangReg godoc
// @Summary Get pattern data
// @Description Get pattern data with language, region
// @tags formatting-pattern-api
// @Produce json
// @Param language query string true "a string which represents language, e.g. en,en-US,pt,pt-BR,zh-Hans"
// @Param region query string true "a string which represents region, e.g. US,PT,CN"
// @Param scope query string true "pattern category string, separated by commas. e.g. 'dates,numbers,currencies,plurals,measurements,dateFields'"
// @Param scopeFilter query string false "a string for filtering the pattern data, separated by comma and underline. e.g. 'dates_eras,dates_dateTimeFormats'"
// @Success 200 {object} api.Response "OK"
// @Success 206 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /formatting/patterns [get]
func GetPatternDataByLangReg(c *gin.Context) {
	params := PatternByLangRegReq{}
	if err := api.ExtractParameters(c, nil, &params); err != nil {
		return
	}

	dataMap, localeToSet, mError := cldrservice.GetPatternByLangReg(logger.NewContext(c, c.MustGet(api.LoggerKey)), params.Language, params.Region, params.Scope, params.ScopeFilter)
	var data interface{}
	if len(dataMap) > 0 {
		data = PatternData{
			LocaleID:   localeToSet,
			Language:   params.Language,
			Region:     params.Region,
			Categories: dataMap,
		}
	}

	api.HandleResponse(c, data, mError)
}

// GetRegionListOfLanguages godoc
// @Summary Get region names
// @Description Get region names in a specified locale
// @Tags locale-api
// @Produce json
// @Param supportedLanguageList query string true "the supported language list, separated by commas. e.g. 'en,zh,ja'"
// @Param displayCity query boolean false "a flag for returning cities" default(false)
// @Param regions query string false "a string which represents regions, separated by commas. e.g. US, PT, CN""
// @Success 200 {object} api.Response "OK"
// @Success 206 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /locale/regionList [get]
func GetRegionListOfLanguages(c *gin.Context) {
	params := struct {
		Locales     string `form:"supportedLanguageList" binding:"locales"`
		DisplayCity bool   `form:"displayCity"`
		Regions     string `form:"regions"`
	}{}
	if err := api.ExtractParameters(c, nil, &params); err != nil {
		return
	}

	ctx := logger.NewContext(c, c.MustGet(api.LoggerKey))
	localesData, multiErr := localeutil.GetTerritoriesOfMultipleLocales(ctx, common.SplitParameter(params.Locales, common.ParamSepSplitRegexp))
	if !params.DisplayCity {
		api.HandleResponse(c, localesData, multiErr)
	} else {
		result := make([]interface{}, 0, len(localesData))
		regions := common.SplitParameter(params.Regions, common.ParamSepSplitRegexp)
		for _, d := range localesData {
			cities, _ := localeutil.GetLocaleCities(ctx, d.Language, regions)
			result = append(result, LocaleTerritoriesWithCities{LocaleTerritories: d, Cities: cities})
		}
		api.HandleResponse(c, result, multiErr)
	}
}
