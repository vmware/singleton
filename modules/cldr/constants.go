/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldr

const (
	LanguageStr  = "languages"
	StandAlone   = "stand-alone"
	UIListOrMenu = "uiListOrMenu"
)

const LocalePartSep = "-"

type CoreDataType int

// Core data type
const (
	CoreSplmtAlias CoreDataType = iota
	CoreSplmtCurrencyData
	CoreSplmtLanguageData
	CoreSplmtLikelySubTags
	CoreSplmtNumberingSystems
	CoreAvaLocales

	RegionToLanguage
	DefaultContent
)

// Locale data type
const (
	PatternDates        = "dates"
	PatternNumbers      = "numbers"
	PatternPlurals      = "plurals"
	PatternMeasurements = "measurements"
	PatternCurrencies   = "currencies"
	PatternDateFields   = "dateFields"

	ContextTransform  = "ContextTransform"
	LocaleLanguages   = "LocaleLanguages"
	LocaleTerritories = "LocaleTerritories"
)
