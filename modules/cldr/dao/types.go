/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package dao

import "sgtnserver/modules/cldr"

const cldrBaseFolder = "cldr/"

// Core Data
const (
	LocaleAliasesPath        = cldrBaseFolder + "aliases/aliases.json"
	SupplementalCurrencyPath = cldrBaseFolder + "supplement/currencies.json"
	LanguageDataPath         = cldrBaseFolder + "supplement/languageData.json"
	ParseDataPath            = cldrBaseFolder + "core/parse.json"
	NumberingSystemsPath     = cldrBaseFolder + "supplement/numbers.json"
	SupplementalDatesPath    = cldrBaseFolder + "supplement/dates.json"
	RegionLanguagesMapPath   = cldrBaseFolder + "regionLanguage/regionLanguageMapping.json"
	DefaultContentPath       = cldrBaseFolder + "defaultContent/defaultContent.json"
)

var (
	coreDataInfo = [...]cldrItemInfo{
		cldr.CoreSplmtAlias:            {LocaleAliasesPath, []interface{}{"languageAlias"}},
		cldr.CoreSplmtCurrencyData:     {SupplementalCurrencyPath, nil},
		cldr.CoreSplmtLanguageData:     {LanguageDataPath, []interface{}{"languageData"}},
		cldr.CoreSplmtLikelySubTags:    {ParseDataPath, []interface{}{"likelySubtag"}},
		cldr.CoreSplmtNumberingSystems: {NumberingSystemsPath, []interface{}{}},
		cldr.CoreSplmtDates:            {SupplementalDatesPath, []interface{}{}},
		cldr.CoreAvaLocales:            {ParseDataPath, []interface{}{"localePath"}},

		cldr.RegionToLanguage: {RegionLanguagesMapPath, []interface{}{"regionInfo"}},
		cldr.DefaultContent:   {DefaultContentPath, []interface{}{"defaultContent"}},
	}

	coreDataTypeStrings = [...]string{
		cldr.CoreSplmtAlias:            "SplmtAlias",
		cldr.CoreSplmtCurrencyData:     "SplmtCurrency",
		cldr.CoreSplmtLanguageData:     "SplmtLanguage",
		cldr.CoreSplmtLikelySubTags:    "LikelySubTags",
		cldr.CoreSplmtNumberingSystems: "NumberingSystems",
		cldr.CoreSplmtDates:            "SplmtDates",
		cldr.CoreAvaLocales:            "AvaLocales",

		cldr.RegionToLanguage: "RegionToLanguage",
		cldr.DefaultContent:   "DefaultContent",
	}
)

// Locale Data
const (
	PatternJSONPath       = cldrBaseFolder + "pattern/common/%s/pattern.json"
	LocaleTerritoriesPath = cldrBaseFolder + "localedata/%s/territories.json"
	LocaleLanguagesPath   = cldrBaseFolder + "localedata/%s/languages.json"
	ContextTransformPath  = cldrBaseFolder + "misc/%s/contextTransforms.json"
	DateFieldsJSONPath    = cldrBaseFolder + "pattern/common/%s/dateFields.json"
)

var localeDataInfo = map[string]cldrItemInfo{
	cldr.PatternDates:        {PatternJSONPath, []interface{}{"categories", "dates"}},
	cldr.PatternNumbers:      {PatternJSONPath, []interface{}{"categories", "numbers"}},
	cldr.PatternPlurals:      {PatternJSONPath, []interface{}{"categories", "plurals"}},
	cldr.PatternMeasurements: {PatternJSONPath, []interface{}{"categories", "measurements"}},
	cldr.PatternCurrencies:   {PatternJSONPath, []interface{}{"categories", "currencies"}},
	cldr.PatternDateFields:   {DateFieldsJSONPath, []interface{}{"dateFields"}},

	cldr.ContextTransform:  {ContextTransformPath, []interface{}{"contextTransforms"}},
	cldr.LocaleLanguages:   {LocaleLanguagesPath, []interface{}{"languages"}},
	cldr.LocaleTerritories: {LocaleTerritoriesPath, []interface{}{}},
}

type cldrItemInfo struct {
	filePath string
	jsonPath []interface{}
}

func getItemInfoOfCoreGroup(t cldr.CoreDataType) cldrItemInfo {
	if int(t) < len(coreDataInfo) {
		return coreDataInfo[t]
	}
	return cldrItemInfo{}
}

func getItemInfoOfLocaleGroup(t string) cldrItemInfo {
	return localeDataInfo[t]
}
