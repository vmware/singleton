/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldr

import (
	"sgtnserver/modules/cldr/localeutil"

	jsoniter "github.com/json-iterator/go"
)

// Request
type (
	PatternScope struct {
		Scope       string `form:"scope" binding:"scope"`
		ScopeFilter string `form:"scopeFilter" binding:"omitempty,scopeFilter"`
	}

	PatternByLangRegReq struct {
		Language string `form:"language" binding:"language"`
		Region   string `form:"region" binding:"region"`
		PatternScope
	}
)

// Response
type (
	PatternData struct {
		LocaleID   string                 `json:"localeID"`
		Language   string                 `json:"language"`
		Region     string                 `json:"region"`
		Categories map[string]interface{} `json:"categories"`
	}

	LocaleTerritoriesWithCities struct {
		*localeutil.LocaleTerritories
		Cities map[string]jsoniter.Any `json:"cities,omitempty"`
	}
)
