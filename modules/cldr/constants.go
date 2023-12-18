/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldr

const (
	LanguageStr          = "languages"
	CTStandAlone         = "stand-alone"
	CTUIListOrMenu       = "uiListOrMenu"
	CTNoChange           = "no-change"
	CTTitlecaseFirstword = "titlecase-firstword"
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
	CoreSplmtDates
	CoreAvaLocales

	RegionToLanguage
	DefaultContent

	MaxCoreDataSize
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
	TimeZoneName      = "TimeZoneName"
	LocaleCities      = "LocaleCities"
)
