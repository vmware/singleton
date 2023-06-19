/*
 * Copyright 2022-2023 VMware, Inc.
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
var pseudoService translation.Service = translationservice.GetPseudoService(l3Service)

func GetService(pseudo bool) translation.Service {
	if pseudo {
		return pseudoService
	} else {
		return l3Service
	}
}

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
// @Param pseudo query boolean false "a flag for returnning pseudo translation" default(false)
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

	transService := GetService(params.Pseudo)
	ctx := logger.NewContext(c, c.MustGet(api.LoggerKey))
	releaseData, multiErr := transService.GetMultipleBundles(ctx, params.ProductName, version, params.Locales, params.Components)
	if multiErr != nil && releaseData != nil && len(releaseData.Bundles) > 0 {
		// make up failed bundles because JAVA client needs them when successful partially
		var locales, components []string
		if params.Locales == "" {
			locales, _ = transService.GetAvailableLocales(ctx, params.ProductName, version)
		} else {
			locales = strings.Split(params.Locales, common.ParamSep)
		}
		if params.Components == "" {
			components, _ = transService.GetAvailableComponents(ctx, params.ProductName, version)
		} else {
			components = strings.Split(params.Components, common.ParamSep)
		}

		allBundles := make([]*translation.Bundle, 0, len(locales)*len(components))
		for _, locale := range locales {
			locale = translationservice.PickupLocales(params.ProductName, version, []string{locale})[0]
			for _, component := range components {
				i := 0
				for ; i < len(releaseData.Bundles); i++ {
					bundle := releaseData.Bundles[i]
					if bundle.ID.Component == component && bundle.ID.Locale == locale {
						allBundles = append(allBundles, bundle)
						break
					}
				}
				if i == len(releaseData.Bundles) {
					id := translation.BundleID{Name: params.ProductName, Version: version, Component: component, Locale: locale}
					allBundles = append(allBundles, &translation.Bundle{ID: id, Pseudo: params.Pseudo})
				}
			}
		}
		releaseData.Bundles = allBundles
	}

	api.HandleResponse(c, ConvertReleaseToAPI(releaseData), multiErr)
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
// @Param pseudo query boolean false "a flag for returnning pseudo translation" default(false)
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
	data, err := GetService(params.Pseudo).GetBundle(ctx, bundleID)
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
// @Tags translation-product-component-key-api
// @Produce json
// @Param productName path string true "product name"
// @Param version path string true "version"
// @Param locale path string true "locale name"
// @Param component path string true "component name"
// @Param keys query string true "keys separated by commas"
// @Param pseudo query boolean false "a flag for returning pseudo translation" default(false)
// @Success 200 {object} api.Response "OK"
// @Success 207 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/products/{productName}/versions/{version}/locales/{locale}/components/{component}/keys [get]
func GetStrings(c *gin.Context) {
	uriPart := BundleID{}
	formPart := struct {
		Keys   string `form:"keys" binding:"required"`
		Pseudo bool   `form:"pseudo"`
	}{}
	if err := api.ExtractParameters(c, &uriPart, &formPart); err != nil {
		return
	}

	version := c.GetString(api.SgtnVersionKey)

	bundleID := &translation.BundleID{Name: uriPart.ProductName, Version: version, Locale: uriPart.Locale, Component: uriPart.Component}
	data, err := GetService(formPart.Pseudo).GetStrings(logger.NewContext(c, c.MustGet(api.LoggerKey)), bundleID, strings.Split(formPart.Keys, common.ParamSep))
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
// @Param pseudo query boolean false "a flag for returnning pseudo translation" default(false)
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
	result, err := GetService(params.Pseudo).GetStringWithSource(logger.NewContext(c, c.MustGet(api.LoggerKey)), &internalID, params.Source)
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
// @Param pseudo query boolean false "a flag for returnning pseudo translation" default(false)
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
	result, err := GetService(params.Pseudo).GetStringWithSource(logger.NewContext(c, c.MustGet(api.LoggerKey)), &internalID, params.Source)
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
		marshaled, _ := jsoniter.Marshal(ad.Messages)
		internalData = append(internalData, &translation.Bundle{ID: id, Messages: jsoniter.Get(marshaled)})
	}

	return internalData
}

func ConvertReleaseToAPI(release *translation.Release) *ReleaseData {
	if release == nil || len(release.Bundles) == 0 {
		return nil
	}

	pData := ReleaseData{ProductName: release.Name, Version: release.Version, Pseudo: release.Pseudo}
	localeSet, componentSet := linkedhashset.New(), linkedhashset.New()
	for _, d := range release.Bundles {
		pData.Bundles = append(pData.Bundles, BundleData{Component: d.ID.Component, Locale: d.ID.Locale, Messages: d.Messages})
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
	return &SingleBundleData{
		ProductName: id.Name,
		Version:     id.Version,
		Locale:      id.Locale,
		Component:   id.Component,
		Messages:    bundle.Messages,
		Pseudo:      bundle.Pseudo}
}
