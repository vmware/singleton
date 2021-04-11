/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import (
	"sgtnserver/api"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/translation"
	"sgtnserver/modules/translation/translationservice"

	"github.com/emirpasic/gods/sets/linkedhashset"
	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
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
	id := ReleaseID{}
	if err := c.ShouldBindUri(&id); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	version := c.GetString(api.SgtnVersionKey)

	components, err := l3Service.GetAvailableComponents(logger.NewContext(c, c.MustGet(api.LoggerKey)), id.ProductName, version)
	data := gin.H{
		api.ProductNameAPIKey: id.ProductName,
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
	id := ReleaseID{}
	if err := c.ShouldBindUri(&id); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	version := c.GetString(api.SgtnVersionKey)

	locales, err := l3Service.GetAvailableLocales(logger.NewContext(c, c.MustGet(api.LoggerKey)), id.ProductName, version)
	data := gin.H{
		api.ProductNameAPIKey: id.ProductName,
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
	req := ProductReq{}
	if err := c.ShouldBindUri(&req); err != nil {
		vErrors, _ := err.(validator.ValidationErrors)
		for _, e := range vErrors {
			if !(e.Field() == api.LocalesAPIKey || e.Field() == api.ComponentsAPIKey) {
				api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(e.Translate(api.ValidatorTranslator)))
				return
			}
		}
	}
	if err := c.ShouldBindQuery(&req); err != nil {
		vErrors, _ := err.(validator.ValidationErrors)
		for _, e := range vErrors {
			if e.Field() == api.LocalesAPIKey || e.Field() == api.ComponentsAPIKey {
				api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(e.Translate(api.ValidatorTranslator)))
				return
			}
		}
	}
	version := c.GetString(api.SgtnVersionKey)

	componentsData, multiErr := l3Service.GetMultipleBundles(logger.NewContext(c, c.MustGet(api.LoggerKey)), req.ProductName, version, req.Locales, req.Components)
	data := ConvertReleaseToAPI(req.ProductName, version, componentsData)
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
	id := GetBundleReq{}
	if err := c.ShouldBindUri(&id); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	if err := c.ShouldBindQuery(&id.CheckTranslationStatus); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}

	version := c.GetString(api.SgtnVersionKey)
	ctx := logger.NewContext(c, c.MustGet(api.LoggerKey))
	bundleID := &translation.BundleID{Name: id.ProductName, Version: version, Locale: id.Locale, Component: id.Component}
	data, err := l3Service.GetBundle(ctx, bundleID)
	bundleAPIData := ConvertBundleToAPI(data)

	if err == nil && id.CheckTranslationStatus.Check {
		bundleAPIData.Status, err = l3Service.GetTranslationStatus(ctx, bundleID)
	}

	api.HandleResponse(c, bundleAPIData, err)
}

// GetString godoc
// @Summary Get a message
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
	req := GetStringReq{}
	if err := c.ShouldBindUri(&req); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	if err := c.ShouldBindQuery(&req); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}

	version := c.GetString(api.SgtnVersionKey)

	internalID := translation.MessageID{Name: req.ProductName, Version: version, Locale: req.Locale, Component: req.Component, Key: req.Key}
	result, err := l3Service.GetStringWithSource(logger.NewContext(c, c.MustGet(api.LoggerKey)), &internalID, req.Source)
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
	id := ReleaseID{}
	if err := c.ShouldBindUri(&id); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}

	req := UpdateTranslationDTO{}
	if err := c.ShouldBindJSON(&req); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}

	if id.ProductName != req.Data.ProductName || id.Version != req.Data.Version {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage("Product name/version should be consistent between URL and post data"))
		return
	}

	err := l3Service.PutBundles(logger.NewContext(c, c.MustGet(api.LoggerKey)), ConvertBundleToInternal(req.Data))
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

func ConvertReleaseToAPI(productName, version string, bundleSlice []*translation.Bundle) *ReleaseData {
	pData := ReleaseData{ProductName: productName, Version: version}
	localeSet, componentSet := linkedhashset.New(), linkedhashset.New()
	for _, d := range bundleSlice {
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
	data := SingleBundleData{
		ProductName: id.Name,
		Version:     id.Version,
		Locale:      id.Locale,
		Component:   id.Component,
		Messages:    bundle.Messages}

	return &data
}
