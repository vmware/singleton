/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package coreutil

import (
	"regexp"
	"strings"

	"sgtnserver/internal/common"
	"sgtnserver/modules/cldr"
)

var localeSplitter = regexp.MustCompile("[-_]")

type Locale struct {
	Language, Scripts, Region string
}

// GetCLDRLocale Query whether locale exists, if not, query defaultContent.json
func GetCLDRLocale(locale string) string {
	localeToProcess := strings.ToLower(strings.ReplaceAll(locale, "_", cldr.LocalePartSep))
	matchedLocale := GetLocaleNameByAliasData(localeToProcess)
	if matchedLocale != "" {
		localeToProcess = strings.ToLower(matchedLocale)
	}
	cldrLocale := AvailableLocalesMap[localeToProcess]
	if cldrLocale == "" {
		cldrLocale = GetLocaleByDefaultContent(localeToProcess)
	}

	return cldrLocale
}

// GetLocaleByDefaultContent Query defaultContent.json to determine whether
// there is a matching locale, and if so, get the processed result and run
// e.g. fr-FR ==> fr
func GetLocaleByDefaultContent(locale string) string {
	if gotten, ok := DefaultContentMap[locale]; ok {
		locale = gotten[0:strings.LastIndex(gotten, cldr.LocalePartSep)]
		return locale
	}
	return ""
}

// GetLocaleByLangReg Get locale by specific Language and Region
func GetLocaleByLangReg(language, region string) string {
	newLang := strings.ReplaceAll(language, "_", cldr.LocalePartSep)
	locale := newLang + cldr.LocalePartSep + region
	cldrLocale := GetCLDRLocale(locale)
	if cldrLocale != "" {
		return cldrLocale
	}

	languageParts := strings.Split(newLang, cldr.LocalePartSep)
	size := len(languageParts)
	if size > 1 {
		for i := 1; i < size; i++ {
			if strings.EqualFold(region, languageParts[i]) {
				cldrLocale = GetCLDRLocale(newLang)
				if cldrLocale != "" {
					return cldrLocale
				}
				break
			}
		}

		grandLanguage := languageParts[0]
		languageScript := languageParts[1]
		if size > 2 {
			locale = grandLanguage + cldr.LocalePartSep + languageScript + cldr.LocalePartSep + region
			cldrLocale = GetCLDRLocale(locale)
			if cldrLocale != "" {
				return cldrLocale
			}
		}

		supplementLanguageData := SupplementLanguageDataMap[grandLanguage]
		if supplementLanguageData != nil {
			scripts := supplementLanguageData.Scripts
			if len(scripts) != 0 {
				existenceOfScript := false
				for _, script := range scripts {
					if strings.EqualFold(languageScript, script) {
						existenceOfScript = true
						break
					}
				}

				if !existenceOfScript {
					locale = grandLanguage + cldr.LocalePartSep + region
					cldrLocale = GetCLDRLocale(locale)
					if cldrLocale != "" {
						return cldrLocale
					}
				}
			}
		}
	}

	langFromRegion := RegionToLangMap[strings.ToUpper(region)]
	if langFromRegion != "" {
		locale = langFromRegion + cldr.LocalePartSep + region
		return GetCLDRLocale(locale)
	}

	return ""
}

// GetLocaleNameByAliasData Convert a deprecated name to its replacement by aliases.json
func GetLocaleNameByAliasData(oldLocale string) string {
	return LocaleAliasesMap[oldLocale].Replacement
}

// GetPathLocale Parse locale and match cldr locale path: e.g. zh-Hans-CN = > zh-Hans,
// zh-CN = > zh-Hans-CN = > zh-Hans, if no matching item, return zh
func GetPathLocale(locale string) string {
	normalizedLocale := strings.ReplaceAll(strings.ToLower(locale), "_", cldr.LocalePartSep)
	cldrLocale := AvailableLocalesMap[normalizedLocale]
	if cldrLocale != "" {
		return cldrLocale
	}

	parts := strings.Split(normalizedLocale, cldr.LocalePartSep)
	switch len(parts) {
	case 3:
		cldrLocale = AvailableLocalesMap[parts[0]+cldr.LocalePartSep+parts[1]] // e.g. zh-Hans
		if cldrLocale == "" {
			cldrLocale = AvailableLocalesMap[parts[0]+cldr.LocalePartSep+parts[2]] // zh-CN
		}
	case 2: // e.g. zh-Hans or zh-CN => zh-Hans-CN
		likelySubStr := LikelySubtagMap["und-"+parts[1]] // Get locale ID from Region
		if likelySubStr == "" || strings.Compare(parts[0], strings.Split(likelySubStr, cldr.LocalePartSep)[0]) != 0 {
			break
		}
		if likelySubStr != "" {
			cldrLocale = AvailableLocalesMap[strings.ToLower(likelySubStr)]
			if cldrLocale == "" {
				likelySubStrArr := strings.Split(likelySubStr, cldr.LocalePartSep)
				cldrLocale = AvailableLocalesMap[strings.ToLower(likelySubStrArr[0]+cldr.LocalePartSep+likelySubStrArr[1])]
			}
		}
		if cldrLocale == "" {
			cldrLocale = AvailableLocalesMap[parts[0]]
		}
	}

	return cldrLocale
}

func ParseRegion(parts []string) string {
	region := ""
	switch len(parts) {
	case 2:
		if len(parts[1]) == 2 {
			region = strings.ToUpper(parts[1])
		}
	case 3:
		region = strings.ToUpper(parts[2])
	}

	return region
}

// SplitLocale ...
func SplitLocale(s string) []string {
	if s == "" {
		return nil
	}
	return localeSplitter.Split(s, -1)
}

func ParseLocale(originalLocale string) *Locale {
	parts := SplitLocale(originalLocale)
	if len(parts) > 3 {
		return nil
	}

	locale := Locale{}
	var langData cldr.LanguageData
	for n, p := range parts {
		switch n {
		case 0:
			locale.Language = strings.ToLower(p)
			if langDataFirst := SupplementLanguageDataMap[locale.Language]; langDataFirst != nil {
				langData.Territories = langDataFirst.Territories
				langData.Scripts = langDataFirst.Scripts
			}
			if langDataSecond := SupplementLanguageDataMap[locale.Language+"-alt-secondary"]; langDataSecond != nil {
				langData.Territories = append(langData.Territories, langDataSecond.Territories...)
				langData.Scripts = append(langData.Scripts, langDataSecond.Scripts...)
			}
			if len(langData.Territories) == 0 && len(langData.Scripts) == 0 {
				return nil
			}
		case 1:
			if i := common.ContainsIgnoreCase(langData.Scripts, p); i >= 0 {
				locale.Scripts = langData.Scripts[i]
			} else if j := common.ContainsIgnoreCase(langData.Territories, p); j >= 0 {
				locale.Region = langData.Territories[j]
				return &locale
			} else if matched, _ := regexp.MatchString(`^\d{3}$`, p); matched { // to process number regions
				locale.Region = p
				return &locale
			} else {
				return nil
			}
		case 2:
			if j := common.ContainsIgnoreCase(langData.Territories, p); j >= 0 {
				locale.Region = langData.Territories[j]
				return &locale
			} else {
				return nil
			}
		}
	}

	return &locale
}
