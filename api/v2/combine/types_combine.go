/*
 * Copyright 2022-2023 VMware, Inc.
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
		Combine int `form:"combine" binding:"oneof=1 2"`
		translation.ReleaseID
		Language   string `form:"language" binding:"language"`
		Region     string `form:"region" binding:"omitempty,region"`
		Components string `form:"components" binding:"components"`
		Pseudo     bool   `form:"pseudo" binding:"omitempty"`
		cldr.PatternScope
	}

	languageListReq struct {
		translation.ReleaseID
		DisplayLanguage string `form:"displayLanguage" binding:"omitempty,language"`
	}

	// translationWithPatternPostReq Deprecated because GET method is ready
	translationWithPatternPostReq struct {
		Combine int `form:"combine" binding:"oneof=1 2"`
		translation.ReleaseID
		Language   string      `form:"language" binding:"language"`
		Region     string      `form:"region" binding:"omitempty,region"`
		Components []string    `form:"components" binding:"dive,component"`
		Pseudo     interface{} `form:"pseudo" binding:"omitempty"`
		cldr.PatternScope
	}
)

// Response
type (
	translationWithPatternData struct {
		Bundles []*translation.SingleBundleData `json:"components"`
		Pattern *patternData                    `json:"pattern"`
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
