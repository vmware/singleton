/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package combine

import (
	"errors"
	"strconv"
	"strings"

	"sgtnserver/api"
	cldrApi "sgtnserver/api/v2/cldr"
	transApi "sgtnserver/api/v2/translation"
	"sgtnserver/internal/common"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/cldrservice"
	"sgtnserver/modules/cldr/coreutil"
	"sgtnserver/modules/cldr/localeutil"
	"sgtnserver/modules/translation"
	"sgtnserver/modules/translation/translationservice"

	"github.com/gin-gonic/gin"
)

var l3Service translation.Service = translationservice.GetService()

// getCombinedData godoc
// @Summary Get translation and pattern data
// @Description Get translation and pattern data by customized type
// @Tags translation-with-pattern-api
// @Produce json
// @Param combine query int true "an integer which represents combine type number 1 or 2"
// @Param productName query string true "product name"
// @Param version query string true "version"
// @Param components query string true "a string contains multiple components, separated by commas. e.g. 'cim,common,cpa,cpu'"
// @Param language query string true "a string which represents language, e.g. en,en-US,pt,pt-BR,zh-Hans"
// @Param region query string false "a string which represents region, e.g. US,PT,CN"
// @Param scope query string true "pattern category string, separated by commas. e.g. 'dates,numbers,currencies,plurals,measurements,dateFields'"
// @Param scopeFilter query string false "a string for filtering the pattern data, separated by comma and underline. e.g. 'dates_eras,dates_dateTimeFormats'"
// @Param pseudo query boolean false "a flag for returnning pseudo translation" default(false)
// @Success 200 {object} api.Response "OK"
// @Success 206 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /combination/translationsAndPattern [get]
func getCombinedData(c *gin.Context) {
	params := translationWithPatternReq{}
	if err := api.ExtractParameters(c, nil, &params); err != nil {
		return
	}

	params.Version = c.GetString(api.SgtnVersionKey)
	doGetCombinedData(c, &params)
}

// getLanguageListOfDispLang godoc
// @Summary Get language display names
// @Description Get language display names in a specified locale
// @Tags locale-api
// @Produce json
// @Param productName query string true "product name"
// @Param version query string true "version"
// @Param displayLanguage query string false "displayLanguage"
// @Success 200 {object} api.Response "OK"
// @Success 206 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /locale/supportedLanguageList [get]
func getLanguageListOfDispLang(c *gin.Context) {
	params := languageListReq{}
	if err := api.ExtractParameters(c, nil, &params); err != nil {
		return
	}
	version := c.GetString(api.SgtnVersionKey)
	ctx := logger.NewContext(c, c.MustGet(api.LoggerKey))

	productLocales, err := l3Service.GetAvailableLocales(ctx, params.ProductName, version)
	if err != nil {
		api.AbortWithError(c, err)
		return
	}

	var infos []supportedLanguageInfo
	var languagesDataOfLocale map[string]string
	var contextData map[string]interface{}

	// Get display Names when displayLanguage is provided
	if params.DisplayLanguage != "" {
		cldrLocale := coreutil.GetCLDRLocale(params.DisplayLanguage)
		if cldrLocale == "" {
			api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidLocale, params.DisplayLanguage))
			return
		}
		languagesDataOfLocale, err = localeutil.GetLocaleLanguages(ctx, cldrLocale)
		if err != nil {
			api.AbortWithError(c, err)
			return
		}
		contextData, _ = localeutil.GetContextTransforms(ctx, cldrLocale)
	}

	var multiErr *sgtnerror.MultiError
	for _, pLocale := range productLocales {
		newLocale := pLocale
		if cldrLocale := coreutil.GetCLDRLocale(newLocale); cldrLocale != "" {
			newLocale = cldrLocale
		}
		if params.DisplayLanguage == "" {
			// Get display name when displayLanguage isn't specified. Need to display language in itself.
			languagesDataOfLocale, err = localeutil.GetLocaleLanguages(ctx, newLocale)
			multiErr = sgtnerror.Append(multiErr, err)
			// Because some locales don't have this data, so ignore the error
			contextData, _ = localeutil.GetContextTransforms(ctx, newLocale)
		}

		dispName := languagesDataOfLocale[newLocale]
		resultDataOfCurLang := supportedLanguageInfo{
			LanguageTag:                  pLocale,
			DisplayName:                  dispName,
			DisplayNameSentenceBeginning: common.TitleCase(dispName),
			DisplayNameUIListOrMenu:      dispName,
			DisplayNameStandalone:        dispName,
		}
		if cd, ok := contextData[cldr.LanguageStr]; ok {
			if cdMap, ok := cd.(map[string]interface{}); ok {
				if v, ok := cdMap[cldr.UIListOrMenu]; ok {
					resultDataOfCurLang.DisplayNameUIListOrMenu = v.(string)
				}
				if v, ok := cdMap[cldr.StandAlone]; ok {
					resultDataOfCurLang.DisplayNameStandalone = v.(string)
				}
			}
		}
		infos = append(infos, resultDataOfCurLang)
	}

	data := map[string]interface{}{
		api.ProductNameAPIKey: params.ProductName,
		api.VersionAPIKey:     version,
		"displayLanguage":     params.DisplayLanguage,
		"languages":           infos}

	api.HandleResponse(c, data, multiErr)
}

// getCombinedDataByPost godoc
// @Summary Get translation and pattern data (Deprecated because GET method is ready)
// @Description Get translation and pattern data by customized type
// @Tags translation-with-pattern-api
// @Produce json
// @Param data body translationWithPatternPostReq true "translationWithPatternPostReq"
// @Success 200 {object} api.Response "OK"
// @Success 206 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /combination/translationsAndPattern [post]
// @Deprecated
func getCombinedDataByPost(c *gin.Context) {
	params := translationWithPatternPostReq{}
	if err := c.ShouldBindJSON(&params); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}

	if config.Settings.AllowList && !translationservice.IsReleaseAllowed(params.ProductName, params.Version) {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(translation.ReleaseNonexistent, params.ProductName, params.Version))
		return
	}

	var pseudo bool
	var err error
	switch v := params.Pseudo.(type) {
	case nil:
		pseudo = false
	case string:
		if v == "" {
			pseudo = false
		} else {
			pseudo, err = strconv.ParseBool(v)
		}
	case bool:
		pseudo = v
	default:
		err = errors.New("wrong type")
	}
	if err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WrapErrorWithMessage(err, "%v is an invalid boolean value", params.Pseudo))
		return
	}

	params.Version = transApi.DoVersionFallback(c, params.ProductName, params.Version)

	req := translationWithPatternReq{
		Combine:    params.Combine,
		ReleaseID:  params.ReleaseID,
		Language:   params.Language,
		Region:     params.Region,
		Components: strings.Join(params.Components, common.ParamSep),
		PatternScope: cldrApi.PatternScope{
			Scope:       params.Scope,
			ScopeFilter: params.ScopeFilter},
		Pseudo: pseudo}
	doGetCombinedData(c, &req)
}

func doGetCombinedData(c *gin.Context, params *translationWithPatternReq) {
	ctx := logger.NewContext(c, c.MustGet(api.LoggerKey))

	var allErrors, translationError, patternError error
	var transData *translation.Release
	var patternDataMap map[string]interface{}
	var localeToSet, language, region = "", params.Language, params.Region
	data := new(translationWithPatternData)

	switch params.Combine {
	// get pattern use parameter: language, scope, region, get the translation use parameters language, productName, version, component
	case 1:
		if len(params.Region) == 0 {
			api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage("Region can't be empty when combine type is %d", params.Combine))
			return
		}
		patternDataMap, localeToSet, patternError = cldrservice.GetPatternByLangReg(ctx, params.Language, params.Region, params.Scope, params.ScopeFilter)
		transData, translationError = transApi.GetService(params.Pseudo).GetMultipleBundles(ctx, params.ProductName, params.Version, params.Language, params.Components)
	// get pattern use parameter: language, scope, get the translation use parameters language, productName, version, component
	case 2:
		localeToSet, patternDataMap, patternError = cldrservice.GetPatternByLocale(ctx, params.Language, params.Scope, params.ScopeFilter)
		if localeToSet != "" && len(patternDataMap) > 0 {
			parts := strings.Split(localeToSet, cldr.LocalePartSep)
			language = parts[0]
			if region = coreutil.ParseRegion(parts); region == "" {
				region, _ = localeutil.GetLocaleDefaultRegion(ctx, localeToSet)
			}
		}
		transData, translationError = transApi.GetService(params.Pseudo).GetMultipleBundles(ctx, params.ProductName, params.Version, params.Language, params.Components)
	default:
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage("Unsupported combination type: %d", params.Combine))
		return
	}
	allErrors = sgtnerror.Append(patternError, translationError)

	for _, t := range transData.Bundles {
		data.Bundles = append(data.Bundles, transApi.ConvertBundleToAPI(t))
	}
	if len(patternDataMap) > 0 && isExistPattern(patternDataMap) {
		data.Pattern = &patternData{
			PatternData: cldrApi.PatternData{
				LocaleID:   localeToSet,
				Language:   language,
				Region:     region,
				Categories: patternDataMap,
			},
			IsExistPattern: true,
		}
	}

	//!+ This is for JS client which can't handle 207
	// In development env, when translation isn't ready,
	// server will return 207 because only pattern data is available.
	if bError := api.ToBusinessError(allErrors); bError.Code == 207 {
		api.HandleResponse(c, data, nil)
		return
	}
	//!- This is for JS client which can't handle 207

	api.HandleResponse(c, data, allErrors)
}

func isExistPattern(patternDataMap map[string]interface{}) bool {
	for _, v := range patternDataMap {
		if v != nil && !common.IsZeroOfUnderlyingType(v) {
			return true
		}
	}
	return false
}
