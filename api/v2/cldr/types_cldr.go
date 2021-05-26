/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldr

// Request
type (
	PatternScope struct {
		Scope       string `form:"scope" binding:"required,scope"`
		ScopeFilter string `form:"scopeFilter"`
	}

	PatternByLangRegReq struct {
		Language string `form:"language" binding:"required,language"`
		Region   string `form:"region" binding:"required,region"`
		PatternScope
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
