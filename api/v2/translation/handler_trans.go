/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import (
	"io/ioutil"
	"sgtnserver/api"
	"sgtnserver/internal/common"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/translation"
	"sgtnserver/modules/translation/translationservice"
	"strings"

	"github.com/emirpasic/gods/sets/linkedhashset"
	"github.com/gin-gonic/gin"
	jsoniter "github.com/json-iterator/go"
)

var l3Service translation.Service = translationservice.GetService()

// GetAvailableComponents godoc
// @Summary Get component names
// @Description Get available component names in the product
// @Tags translation-product-api
// @Produce json
// @Param productName path string true "product name"
// @Param version path string true "version"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/products/{productName}/versions/{version}/componentlist [get]
func GetAvailableComponents(c *gin.Context) {
	params := ReleaseID{}
	if err := api.ExtractParameters(c, &params, nil); err != nil {
		return
	}

	version := c.GetString(api.SgtnVersionKey)

	components, err := l3Service.GetAvailableComponents(logger.NewContext(c, c.MustGet(api.LoggerKey)), params.ProductName, version)
	data := gin.H{
		api.ProductNameAPIKey: params.ProductName,
		api.VersionAPIKey:     version,
		api.ComponentsAPIKey:  components}
	api.HandleResponse(c, data, err)
}

// GetAvailableLocales godoc
// @Summary Get locale names
// @Description Get available locale names in the product
// @Tags translation-product-api
// @Produce json
// @Param productName path string true "product name"
// @Param version path string true "version"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/products/{productName}/versions/{version}/localelist [get]
func GetAvailableLocales(c *gin.Context) {
	params := ReleaseID{}
	if err := api.ExtractParameters(c, &params, nil); err != nil {
		return
	}

	version := c.GetString(api.SgtnVersionKey)

	locales, err := l3Service.GetAvailableLocales(logger.NewContext(c, c.MustGet(api.LoggerKey)), params.ProductName, version)
	data := gin.H{
		api.ProductNameAPIKey: params.ProductName,
		api.VersionAPIKey:     version,
		api.LocalesAPIKey:     locales}
	api.HandleResponse(c, data, err)
}

// GetMultipleBundles godoc
// @Summary Get messages of the product
// @Description Get messages of all the product or parts of the product
// @Tags translation-product-api
// @Produce json
// @Param productName path string true "product name"
// @Param version path string true "version"
// @Param locales query string false "locales"
// @Param components query string false "components"
// @Success 200 {object} api.Response "OK"
// @Success 206 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/products/{productName}/versions/{version} [get]
func GetMultipleBundles(c *gin.Context) {
	params := ProductReq{}
	if err := api.ExtractParameters(c, &params, &params); err != nil {
		return
	}
	version := c.GetString(api.SgtnVersionKey)

	bundles, multiErr := l3Service.GetMultipleBundles(logger.NewContext(c, c.MustGet(api.LoggerKey)), params.ProductName, version, params.Locales, params.Components)
	data := ConvertReleaseToAPI(params.ProductName, version, bundles)
	api.HandleResponse(c, data, multiErr)
}

// GetBundle godoc
// @Summary Get messages of a single bundle
// @Description Get messages of a single bundle
// @Tags translation-product-component-api
// @Produce json
// @Param productName path string true "product name"
// @Param version path string true "version"
// @Param locale path string true "locale name"
// @Param component path string true "component name"
// @Param checkTranslationStatus query string false "checkTranslationStatus" default(false)
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/products/{productName}/versions/{version}/locales/{locale}/components/{component} [get]
func GetBundle(c *gin.Context) {
	params := GetBundleReq{}
	if err := api.ExtractParameters(c, &params, &params); err != nil {
		return
	}

	version := c.GetString(api.SgtnVersionKey)

	ctx := logger.NewContext(c, c.MustGet(api.LoggerKey))
	bundleID := &translation.BundleID{Name: params.ProductName, Version: version, Locale: params.Locale, Component: params.Component}
	data, err := l3Service.GetBundle(ctx, bundleID)
	bundleAPIData := ConvertBundleToAPI(data)

	mErr := sgtnerror.Append(err)
	if err == nil && params.CheckTranslationStatus {
		bundleAPIData.Status, err = l3Service.GetTranslationStatus(ctx, bundleID)
		mErr = sgtnerror.Append(err)
	}

	api.HandleResponse(c, bundleAPIData, mErr)
}

// GetStrings godoc
// @Summary Get translations of multiple strings
// @Description Get multiple translations together by their keys
// @Tags translation-product-component-keys-api
// @Produce json
// @Param productName path string true "product name"
// @Param version path string true "version"
// @Param locale path string true "locale name"
// @Param component path string true "component name"
// @Param keys query string true "keys separated by commas"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 401 {string} string "Unauthorized"
// @Failure 403 {string} string "Forbidden"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}/keys [get]
func GetStrings(c *gin.Context) {
	params := GetStringsReq{}
	if err := api.ExtractParameters(c, &params, &params); err != nil {
		return
	}

	version := c.GetString(api.SgtnVersionKey)

	bundleID := &translation.BundleID{Name: params.ProductName, Version: version, Locale: params.Locale, Component: params.Component}
	data, err := l3Service.GetStrings(logger.NewContext(c, c.MustGet(api.LoggerKey)), bundleID, strings.Split(params.Keys, common.KeySep))
	api.HandleResponse(c, ConvertBundleToAPI(data), err)
}

// GetString godoc
// @Summary Get a string's translation
// @Description Get a message by its key
// @Tags translation-product-component-key-api
// @Produce json
// @Param productName path string true "product name"
// @Param version path string true "version"
// @Param locale path string true "locale name"
// @Param component path string true "component name"
// @Param key path string true "key"
// @Param source query string false "a source string"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}/keys/{key} [get]
// @Deprecated
func GetString(c *gin.Context) {
	params := GetStringReq{}
	if err := api.ExtractParameters(c, &params, &params); err != nil {
		return
	}

	version := c.GetString(api.SgtnVersionKey)

	internalID := translation.MessageID{Name: params.ProductName, Version: version, Locale: params.Locale, Component: params.Component, Key: params.Key}
	result, err := l3Service.GetStringWithSource(logger.NewContext(c, c.MustGet(api.LoggerKey)), &internalID, params.Source)
	api.HandleResponse(c, result, err)
}

// GetStringByPost godoc
// @Summary Post a source
// @Description Post a source
// @Tags translation-product-component-key-api
// @Produce json
// @Param productName path string true "product name"
// @Param version path string true "version"
// @Param locale path string true "locale name"
// @Param component path string true "component name"
// @Param key path string true "key"
// @Param source body string false "a source string"
// @Param checkTranslationStatus query string false "checkTranslationStatus" default(false)
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}/keys/{key} [post]
// @Deprecated
func GetStringByPost(c *gin.Context) {
	params := GetStringByPostReq{}
	if err := api.ExtractParameters(c, &params, &params); err != nil {
		return
	}

	if c.Request.Body != nil {
		if bts, err := ioutil.ReadAll(c.Request.Body); err != nil {
			api.AbortWithError(c, sgtnerror.StatusBadRequest.WrapErrorWithMessage(err, "fail to read request body"))
			return
		} else {
			params.Source = string(bts)
		}
	}

	internalID := translation.MessageID{Name: params.ProductName, Version: params.Version, Locale: params.Locale, Component: params.Component, Key: params.Key}
	result, err := l3Service.GetStringWithSource(logger.NewContext(c, c.MustGet(api.LoggerKey)), &internalID, params.Source)
	if err == nil && result != nil && params.CheckTranslationStatus {
		if result["status"].(translation.TranslationStatus).IsReady() {
			err = sgtnerror.TranslationReady
		} else {
			err = sgtnerror.TranslationNotReady
		}
	}

	api.HandleResponse(c, result, err)
}

// PutBundles godoc
// @Summary Update bundles
// @Description Update bundles
// @Tags translation-sync-api
// @Accept  json
// @Produce json
// @Param productName path string true "product name"
// @Param version path string true "version"
// @Param translationData body UpdateTranslationDTO true "translationData"
// @Success 200 {object} api.Response "OK"
// @Success 206 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/products/{productName}/versions/{version} [put]
func PutBundles(c *gin.Context) {
	uriPart := ReleaseID{}
	if err := api.ExtractParameters(c, &uriPart, nil); err != nil {
		return
	}

	params := UpdateTranslationDTO{}
	if err := c.ShouldBindJSON(&params); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}

	if uriPart.ProductName != params.Data.ProductName || uriPart.Version != params.Data.Version {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage("Product name/version should be consistent between URL and post data"))
		return
	}

	err := l3Service.PutBundles(logger.NewContext(c, c.MustGet(api.LoggerKey)), ConvertBundleToInternal(params.Data))
	api.HandleResponse(c, nil, err)
}

func ConvertBundleToInternal(apiData *UpdateBundle) []*translation.Bundle {
	productName, version := apiData.ProductName, apiData.Version
	internalData := make([]*translation.Bundle, 0, len(apiData.Translation))
	for _, ad := range apiData.Translation {
		id := translation.BundleID{Name: productName, Version: version, Locale: ad.Locale, Component: ad.Component}
		internalData = append(internalData, &translation.Bundle{ID: id, Messages: ad.Messages})
	}

	return internalData
}

func ConvertReleaseToAPI(productName, version string, bundles []*translation.Bundle) *ReleaseData {
	if len(bundles) == 0 {
		return nil
	}

	pData := ReleaseData{ProductName: productName, Version: version}
	localeSet, componentSet := linkedhashset.New(), linkedhashset.New()
	for _, d := range bundles {
		pData.Bundles = append(pData.Bundles, BundleData{Component: d.ID.Component, Locale: d.ID.Locale, Messages: d.Messages.(jsoniter.Any)})
		localeSet.Add(d.ID.Locale)
		componentSet.Add(d.ID.Component)
	}
	pData.Locales = localeSet.Values()
	pData.Components = componentSet.Values()
	return &pData
}

func ConvertBundleToAPI(bundle *translation.Bundle) *SingleBundleData {
	if bundle == nil {
		return nil
	}

	id := bundle.ID
	data := SingleBundleData{
		ProductName: id.Name,
		Version:     id.Version,
		Locale:      id.Locale,
		Component:   id.Component,
		Messages:    bundle.Messages.(jsoniter.Any)}

	return &data
}
