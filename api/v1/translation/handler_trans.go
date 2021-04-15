/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import (
	"sgtnserver/api"
	v1 "sgtnserver/api/v1"
	v2Translation "sgtnserver/api/v2/translation"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/translation"
	"sgtnserver/modules/translation/translationservice"

	"github.com/gin-gonic/gin"
)

var l3Service translation.Service = translationservice.GetService()

// @Summary Get a component's translation
// @Description Get a component's translation by the specific version
// @Tags translation-component-api
// @Produce json
// @Param productName query string true "product name"
// @Param version query string true "version"
// @Param locale query string false "locale String. e.g. 'en-US'"
// @Param component query string true "component name"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/component [get]
// @Deprecated
func GetBundle2(c *gin.Context) {
	id := struct {
		ReleaseID
		Locale    string `form:"locale" binding:"locale"`
		Component string `form:"component" binding:"required,component"`
	}{Locale: v1.DefaultLocale}
	if err := c.ShouldBindQuery(&id); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	version := c.GetString(api.SgtnVersionKey)

	data, err := l3Service.GetBundle(logger.NewContext(c, c.MustGet(api.LoggerKey)),
		&translation.BundleID{Name: id.ProductName, Version: version, Locale: id.Locale, Component: id.Component})

	api.HandleResponse(c, v2Translation.ConvertBundleToAPI(data), err)
}

// @Summary Get multiple components' translation
// @Description Get multiple components' by the specific version
// @Tags translation-component-api
// @Produce json
// @Param productName query string true "product name"
// @Param version query string true "version"
// @Param locales query string true "locales"
// @Param components query string true "components"
// @Success 200 {object} api.Response "OK"
// @Success 206 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/components [get]
// @Deprecated
func GetMultipleBundles2(c *gin.Context) {
	req := struct {
		ReleaseID
		Locales    string `form:"locales" binding:"required,locales"`
		Components string `form:"components" binding:"required,components"`
	}{}
	if err := c.ShouldBindQuery(&req); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	version := c.GetString(api.SgtnVersionKey)

	bundles, multiErr := l3Service.GetMultipleBundles(logger.NewContext(c, c.MustGet(api.LoggerKey)), req.ProductName, version, req.Locales, req.Components)
	data := v2Translation.ConvertReleaseToAPI(req.ProductName, version, bundles)
	api.HandleResponse(c, data, multiErr)
}

// @Summary Get a key's translation
// @Description Get a key's translation in specific component
// @Tags translation-key-api
// @Produce json
// @Param productName query string true "product name"
// @Param version query string true "version"
// @Param locale query string false "locale String. e.g. 'en-US'"
// @Param component query string true "component name"
// @Param key query string true "key"
// @Param source query string false "a source string"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/string [get]
// @Deprecated
func GetString2(c *gin.Context) {
	id := GetStringReq{Locale: v1.DefaultLocale}
	if err := c.ShouldBindQuery(&id); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	version := c.GetString(api.SgtnVersionKey)

	internalID := translation.MessageID{Name: id.ProductName, Version: version, Locale: id.Locale, Component: id.Component, Key: id.Key}
	result, err := l3Service.GetStringWithSource(logger.NewContext(c, c.MustGet(api.LoggerKey)), &internalID, id.Source)
	api.HandleResponse(c, result, err)
}

// @Summary Get the product's translation
// @Description Get the product's translations by the specific version.
// @Tags translation-product-api
// @Produce json
// @Param productName query string true "product name"
// @Param version query string true "version"
// @Param locale query string false "locale String. e.g. 'en-US'"
// @Success 200 {object} api.Response "OK"
// @Success 206 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation [get]
// @Deprecated
func GetProduct(c *gin.Context) {
	req := struct {
		ReleaseID
		Locale string `form:"locale" binding:"locale"`
	}{Locale: v1.DefaultLocale}
	if err := c.ShouldBindQuery(&req); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	version := c.GetString(api.SgtnVersionKey)

	bundles, multiErr := l3Service.GetMultipleBundles(logger.NewContext(c, c.MustGet(api.LoggerKey)), req.ProductName, version, req.Locale, "")
	data := v2Translation.ConvertReleaseToAPI(req.ProductName, version, bundles)
	api.HandleResponse(c, data, multiErr)
}

// @Summary Get the component list
// @Description Get the component list by the specific version.
// @Tags translation-product-api
// @Produce json
// @Param productName query string true "product name"
// @Param version query string true "version"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /bundles/components [get]
// @Deprecated
func GetAvailableComponents(c *gin.Context) {
	id := ReleaseID{}
	if err := c.ShouldBindQuery(&id); err != nil {
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

// @Summary Get the supported locale list
// @Description Get the supported locale list which contains all supported locale by the specific version.
// @Tags translation-product-api
// @Produce json
// @Param productName path string true "product name"
// @Param version path string true "version"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/product/{productName}/version/{version}/supportedLocales [get]
// @Deprecated
func GetAvailableLocales(c *gin.Context) {
	v2Translation.GetAvailableLocales(c)
}

// @Summary Get a component's translation
// @Description Get a component's translation by the specific version
// @Tags translation-product-component-api
// @Produce json
// @Param productName path string true "product name"
// @Param version query string true "version"
// @Param locale query string false "locale String. e.g. 'en-US'"
// @Param component path string true "component name"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/product/{productName}/component/{component} [get]
// @Deprecated
func GetBundle(c *gin.Context) {
	uriPart := struct {
		ProductName string `uri:"productName" binding:"required,alphanum"`
		Component   string `uri:"component" binding:"required,component"`
	}{}
	if err := c.ShouldBindUri(&uriPart); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	formPart := struct {
		Version string `form:"version" binding:"required,version"`
		Locale  string `form:"locale" binding:"locale"`
	}{Locale: v1.DefaultLocale}
	if err := c.ShouldBindQuery(&formPart); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}

	version := c.GetString(api.SgtnVersionKey)

	data, err := l3Service.GetBundle(logger.NewContext(c, c.MustGet(api.LoggerKey)),
		&translation.BundleID{Name: uriPart.ProductName, Version: version, Locale: formPart.Locale, Component: uriPart.Component})

	api.HandleResponse(c, v2Translation.ConvertBundleToAPI(data), err)
}

// @Summary Get multiple components' by the specific version
// @Description Get multiple components' by the specific version
// @Tags translation-product-component-api
// @Produce json
// @Param productName path string true "product name"
// @Param version query string true "version"
// @Param locales query string true "locales"
// @Param components path string true "components"
// @Success 200 {object} api.Response "OK"
// @Success 206 {object} api.Response "Successful Partially"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/product/{productName}/components/{components} [get]
// @Deprecated
func GetMultipleBundles(c *gin.Context) {
	uriPart := struct {
		ProductName string `uri:"productName" binding:"required,alphanum"`
		Components  string `uri:"components" binding:"required,components"`
	}{}
	if err := c.ShouldBindUri(&uriPart); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	formPart := struct {
		Version string `form:"version" binding:"required,version"`
		Locales string `form:"locales" binding:"required,locales"`
	}{}
	if err := c.ShouldBindQuery(&formPart); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	version := c.GetString(api.SgtnVersionKey)

	componentsData, multiErr := l3Service.GetMultipleBundles(logger.NewContext(c, c.MustGet(api.LoggerKey)), uriPart.ProductName, version, formPart.Locales, uriPart.Components)
	data := v2Translation.ConvertReleaseToAPI(uriPart.ProductName, version, componentsData)
	api.HandleResponse(c, data, multiErr)
}

// @Summary Get a key's translation
// @Description Get a key's translation in specific component
// @Tags translation-product-component-key-api
// @Produce json
// @Param productName path string true "product name"
// @Param version query string true "version"
// @Param locale query string false "locale String. e.g. 'en-US'"
// @Param component path string true "component name"
// @Param key path string true "key"
// @Param source query string false "a source string"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/product/{productName}/component/{component}/key/{key} [get]
// @Deprecated
func GetString(c *gin.Context) {
	uriPart := struct {
		ProductName string `uri:"productName" binding:"required,alphanum"`
		Component   string `uri:"component" binding:"required,component"`
		Key         string `uri:"key" binding:"required,key"`
	}{}
	if err := c.ShouldBindUri(&uriPart); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	formPart := struct {
		Version string `form:"version" binding:"required,version"`
		Locale  string `form:"locale" binding:"locale"`
		Source  string `form:"source"`
	}{Locale: v1.DefaultLocale}
	if err := c.ShouldBindQuery(&formPart); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	version := c.GetString(api.SgtnVersionKey)

	internalID := translation.MessageID{Name: uriPart.ProductName, Version: version, Locale: formPart.Locale, Component: uriPart.Component, Key: uriPart.Key}
	result, err := l3Service.GetStringWithSource(logger.NewContext(c, c.MustGet(api.LoggerKey)), &internalID, formPart.Source)
	api.HandleResponse(c, result, err)
}

// @Summary Get a key's translation
// @Description Get a key's translation in specific component
// @Tags translation-product-component-key-api
// @Produce json
// @Param productName path string true "product name"
// @Param version query string true "version"
// @Param locale query string false "locale String. e.g. 'en-US'"
// @Param key path string true "key"
// @Param source query string false "a source string"
// @Success 200 {object} api.Response "OK"
// @Failure 400 {string} string "Bad Request"
// @Failure 404 {string} string "Not Found"
// @Failure 500 {string} string "Internal Server Error"
// @Router /translation/product/{productName}/key/{key} [get]
// @Deprecated
func GetString3(c *gin.Context) {
	uriPart := struct {
		ProductName string `uri:"productName" binding:"required,alphanum"`
		Component   string
		Key         string `uri:"key" binding:"required,key"`
	}{Component: "default"}
	if err := c.ShouldBindUri(&uriPart); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	formPart := struct {
		Version string `form:"version" binding:"required,version"`
		Locale  string `form:"locale" binding:"locale"`
		Source  string `form:"source"`
	}{Locale: v1.DefaultLocale}
	if err := c.ShouldBindQuery(&formPart); err != nil {
		api.AbortWithError(c, sgtnerror.StatusBadRequest.WithUserMessage(api.ExtractErrorMsg(err)))
		return
	}
	version := c.GetString(api.SgtnVersionKey)

	internalID := translation.MessageID{Name: uriPart.ProductName, Version: version, Locale: formPart.Locale, Component: uriPart.Component, Key: uriPart.Key}
	result, err := l3Service.GetStringWithSource(logger.NewContext(c, c.MustGet(api.LoggerKey)), &internalID, formPart.Source)
	api.HandleResponse(c, result, err)
}

// @Summary Update translation
// @Description Update translation
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
// @Router /translation/product/{productName}/version/{version} [put]
// @Deprecated
func PutBundles(c *gin.Context) {
	v2Translation.PutBundles(c)
}
