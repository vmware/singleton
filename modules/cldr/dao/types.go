/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package dao

const cldrBaseFolder = "cldr/"
const (
	ParseDataPath            = cldrBaseFolder + "pattern/common/parse.json"
	PatternJSONPath          = cldrBaseFolder + "pattern/common/%s/pattern.json"
	SupplementalCurrencyPath = cldrBaseFolder + "supplement/currencies.json"
	NumberingSystemsPath     = cldrBaseFolder + "supplement/numbers.json"

	LocaleTerritoriesPath  = cldrBaseFolder + "localedata/%s/territories.json"
	LocaleLanguagesPath    = cldrBaseFolder + "localedata/%s/languages.json"
	ContextTransformPath   = cldrBaseFolder + "misc/%s/contextTransforms.json"
	DefaultContentPath     = cldrBaseFolder + "defaultContent/defaultContent.json"
	RegionLanguagesMapPath = cldrBaseFolder + "regionLanguage/regionLanguageMapping.json"
	LocaleAliasesPath      = cldrBaseFolder + "aliases/aliases.json"
	LanguageDataPath       = cldrBaseFolder + "supplement/languageData.json"
	DateFieldsJSONPath     = cldrBaseFolder + "pattern/common/%s/dateFields.json"
)

var CoreDataTypeNames = [...]string{
	CoreSplmtAlias:            "CoreSplmtAlias",
	CoreSplmtCurrencyData:     "CoreSplmtCurrencyData",
	CoreSplmtLanguageData:     "CoreSplmtLanguageData",
	CoreSplmtLikelySubTags:    "CoreSplmtLikelySubTags",
	CoreSplmtNumberingSystems: "CoreSplmtNumberingSystems",
	CoreAvaLocales:            "CoreAvaLocales",

	RegionToLanguage: "RegionToLanguage",
	DefaultContent:   "DefaultContent",
}

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

var coreDataInfo = [...]cldrItemInfo{
	CoreSplmtAlias:            {LocaleAliasesPath, []interface{}{"languageAlias"}},
	CoreSplmtCurrencyData:     {SupplementalCurrencyPath, nil},
	CoreSplmtLanguageData:     {LanguageDataPath, []interface{}{"languageData"}},
	CoreSplmtLikelySubTags:    {ParseDataPath, []interface{}{"likelySubtag"}},
	CoreSplmtNumberingSystems: {NumberingSystemsPath, []interface{}{}},
	CoreAvaLocales:            {ParseDataPath, []interface{}{"localePath"}},

	RegionToLanguage: {RegionLanguagesMapPath, []interface{}{"regionInfo"}},
	DefaultContent:   {DefaultContentPath, []interface{}{"defaultContent"}},
}

var localeDataInfo = map[string]cldrItemInfo{
	PatternDates:        {PatternJSONPath, []interface{}{"categories", "dates"}},
	PatternNumbers:      {PatternJSONPath, []interface{}{"categories", "numbers"}},
	PatternPlurals:      {PatternJSONPath, []interface{}{"categories", "plurals"}},
	PatternMeasurements: {PatternJSONPath, []interface{}{"categories", "measurements"}},
	PatternCurrencies:   {PatternJSONPath, []interface{}{"categories", "currencies"}},
	PatternDateFields:   {DateFieldsJSONPath, []interface{}{"dateFields"}},

	ContextTransform:  {ContextTransformPath, []interface{}{"contextTransforms"}},
	LocaleLanguages:   {LocaleLanguagesPath, []interface{}{"languages"}},
	LocaleTerritories: {LocaleTerritoriesPath, []interface{}{}},
}

type CoreDataType int

// Core data
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

var coreDataTypeStrings = [...]string{
	CoreSplmtAlias:            "SplmtAlias",
	CoreSplmtCurrencyData:     "SplmtCurrency",
	CoreSplmtLanguageData:     "SplmtLanguage",
	CoreSplmtLikelySubTags:    "LikelySubTags",
	CoreSplmtNumberingSystems: "NumberingSystems",
	CoreAvaLocales:            "AvaLocales",
	RegionToLanguage:          "RegionToLanguage",
	DefaultContent:            "DefaultContent",
}

type cldrItemInfo struct {
	filePath string
	jsonPath []interface{}
}

func getItemInfoOfCoreGroup(t CoreDataType) cldrItemInfo {
	if int(t) < len(coreDataInfo) {
		return coreDataInfo[t]
	}
	return cldrItemInfo{}
}
func getItemInfoOfLocaleGroup(t string) cldrItemInfo {
	return localeDataInfo[t]
}
