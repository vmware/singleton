/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package combine

import (
	"sgtnserver/api/v2/cldr"
	"sgtnserver/api/v2/translation"
)

// Request
type (
	translationWithPatternReq struct {
		Combine int `form:"combine" binding:"required"`
		translation.ReleaseID
		Language    string `form:"language" binding:"required,language"`
		Region      string `form:"region" binding:"omitempty,region"`
		Components  string `form:"components" binding:"required,components"`
		Scope       string `form:"scope" binding:"required,scope"`
		ScopeFilter string `form:"scopeFilter"`
	}

	languageListReq struct {
		translation.ReleaseID
		DisplayLanguage string `form:"displayLanguage" binding:"omitempty,language"`
	}

	// translationWithPatternPostReq Deprecated because GET method is ready
	translationWithPatternPostReq struct {
		Combine int `form:"combine" binding:"required"`
		translation.ReleaseID
		Language    string   `form:"language" binding:"required,language"`
		Region      string   `form:"region" binding:"omitempty,region"`
		Components  []string `form:"components" binding:"required,dive,component"`
		Scope       string   `form:"scope" binding:"required,scope"`
		ScopeFilter string   `form:"scopeFilter"`
	}
)

// Response
type (
	translationWithPatternData struct {
		Bundles []*translation.SingleBundleData `json:"components,omitempty"`
		Pattern patternData                     `json:"pattern,omitempty"`
	}

	patternData struct {
		cldr.PatternData
		IsExistPattern bool `json:"isExistPattern"`
	}

	supportedLanguageInfo struct {
		LanguageTag                  string `json:"languageTag"`
		DisplayName                  string `json:"displayName"`
		DisplayNameSentenceBeginning string `json:"displayName_sentenceBeginning"`
		DisplayNameUIListOrMenu      string `json:"displayName_uiListOrMenu"`
		DisplayNameStandalone        string `json:"displayName_standalone"`
	}
)
