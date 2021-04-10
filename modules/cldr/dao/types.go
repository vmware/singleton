/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package dao

import "sgtnserver/modules/cldr"

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
	cldr.CoreSplmtAlias:            "CoreSplmtAlias",
	cldr.CoreSplmtCurrencyData:     "CoreSplmtCurrencyData",
	cldr.CoreSplmtLanguageData:     "CoreSplmtLanguageData",
	cldr.CoreSplmtLikelySubTags:    "CoreSplmtLikelySubTags",
	cldr.CoreSplmtNumberingSystems: "CoreSplmtNumberingSystems",
	cldr.CoreAvaLocales:            "CoreAvaLocales",

	cldr.RegionToLanguage: "RegionToLanguage",
	cldr.DefaultContent:   "DefaultContent",
}

var coreDataInfo = [...]cldrItemInfo{
	cldr.CoreSplmtAlias:            {LocaleAliasesPath, []interface{}{"languageAlias"}},
	cldr.CoreSplmtCurrencyData:     {SupplementalCurrencyPath, nil},
	cldr.CoreSplmtLanguageData:     {LanguageDataPath, []interface{}{"languageData"}},
	cldr.CoreSplmtLikelySubTags:    {ParseDataPath, []interface{}{"likelySubtag"}},
	cldr.CoreSplmtNumberingSystems: {NumberingSystemsPath, []interface{}{}},
	cldr.CoreAvaLocales:            {ParseDataPath, []interface{}{"localePath"}},

	cldr.RegionToLanguage: {RegionLanguagesMapPath, []interface{}{"regionInfo"}},
	cldr.DefaultContent:   {DefaultContentPath, []interface{}{"defaultContent"}},
}

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

var coreDataTypeStrings = [...]string{
	cldr.CoreSplmtAlias:            "SplmtAlias",
	cldr.CoreSplmtCurrencyData:     "SplmtCurrency",
	cldr.CoreSplmtLanguageData:     "SplmtLanguage",
	cldr.CoreSplmtLikelySubTags:    "LikelySubTags",
	cldr.CoreSplmtNumberingSystems: "NumberingSystems",
	cldr.CoreAvaLocales:            "AvaLocales",
	cldr.RegionToLanguage:          "RegionToLanguage",
	cldr.DefaultContent:            "DefaultContent",
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
