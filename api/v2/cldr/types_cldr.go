/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldr

// Request
type (
	PatternByLocaleReq struct {
		Locale      string `uri:"locale" form:"locale" binding:"required,locale"`
		Scope       string `form:"scope" binding:"required,scope"`
		ScopeFilter string `form:"scopeFilter"`
	}
	PatternByLangRegReq struct {
		Language    string `form:"language" binding:"required,language"`
		Region      string `form:"region" binding:"required,region"`
		Scope       string `form:"scope" binding:"required,scope"`
		ScopeFilter string `form:"scopeFilter"`
	}
	LocaleRegionsReq struct {
		Locales string `form:"supportedLanguageList" binding:"required,locales"`
	}
)

// Response
type PatternData struct {
	LocaleID   string                 `json:"localeID"`
	Language   string                 `json:"language"`
	Region     string                 `json:"region"`
	Categories map[string]interface{} `json:"categories"`
}
