/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldrservice

import (
	"context"
	"regexp"
	"strings"

	"sgtnserver/internal/common"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/coreutil"
	"sgtnserver/modules/cldr/localeutil"

	"github.com/emirpasic/gods/sets/hashset"
	jsoniter "github.com/json-iterator/go"
	"github.com/stretchr/objx"
	"go.uber.org/zap"
)

var (
	ArgSplitter = regexp.MustCompile(`\s*` + common.ParamSep + `\s*`)

	// categoriesFromLanguage these are always form language
	categoriesFromLanguage = hashset.New(cldr.PatternDateFields, cldr.PatternPlurals, cldr.PatternMeasurements, cldr.PatternCurrencies)

	// categoriesWithSupplement Need to get some extra data for these categories for front end requirements
	categoriesWithSupplement = map[string]cldr.CoreDataType{
		cldr.PatternCurrencies: cldr.CoreSplmtCurrencyData,
		cldr.PatternNumbers:    cldr.CoreSplmtNumberingSystems,
		cldr.PatternDates:      cldr.CoreSplmtDates,
	}

	// categoriesNeedOtherCategories these categories need other categories for front end requirements
	categoriesNeedOtherCategories = map[string][]string{
		cldr.PatternCurrencies:   {cldr.PatternNumbers},
		cldr.PatternPlurals:      {cldr.PatternNumbers},
		cldr.PatternDateFields:   {cldr.PatternPlurals, cldr.PatternNumbers},
		cldr.PatternMeasurements: {cldr.PatternPlurals, cldr.PatternNumbers},
	}
)

const (
	scopeFilterSep = "_"
	objxMapPathSep = "."
)

func GetPatternByLangReg(ctx context.Context, language, region, catgs, filter string) (resultMap map[string]interface{}, cldrLocale string, err error) {
	log := logger.FromContext(ctx)
	log.Debug("Get pattern by language and region", zap.String("language", language), zap.String("region", region), zap.String("scope", catgs), zap.String("scopeFilter", filter))

	combinedLocale := coreutil.GetLocaleByLangReg(language, region)
	normalizedLanguage := coreutil.GetCLDRLocale(language)
	var returnErr *sgtnerror.MultiError

	catgFromLanguageSize := 0
	resultMap = map[string]interface{}{}
	categories := ArgSplitter.Split(catgs, -1)
	for i := 0; i < len(categories); i++ {
		catg := categories[i]
		var catgData jsoniter.Any
		var err error
		if categoriesFromLanguage.Contains(catg) { // dateFields and Plural always follow language
			if normalizedLanguage == "" {
				err = sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidLocale, language)
				log.Error(err.Error())
			} else if catgData, err = localeutil.GetPatternData(ctx, normalizedLanguage, catg); err == nil {
				catgFromLanguageSize++
			}
		} else {
			if combinedLocale == "" {
				err = sgtnerror.StatusBadRequest.WithUserMessage("Can't get a locale ID with '%s' and '%s'", language, region)
				log.Error(err.Error())
			} else {
				catgData, err = localeutil.GetPatternData(ctx, combinedLocale, catg)
			}
		}
		returnErr = sgtnerror.Append(returnErr, err)
		if err == nil {
			resultMap[catg] = catgData
			categories = addOtherCategories(categories, catg)
		}
	}

	resultMap = processFilters(resultMap, filter)
	getSupplementalData(ctx, resultMap, returnErr)

	localeToSet := combinedLocale
	switch {
	case len(resultMap) == 0:
		localeToSet = ""
	case catgFromLanguageSize == len(resultMap):
		localeToSet = normalizedLanguage
	case catgFromLanguageSize != 0 && combinedLocale != normalizedLanguage:
		localeToSet = ""
	}

	return resultMap, localeToSet, returnErr.ErrorOrNil()
}

func GetPatternByLocale(ctx context.Context, locale, catgs, filter string) (newLocale string, resultMap map[string]interface{}, err error) {
	log := logger.FromContext(ctx)
	log.Debug("Get pattern by locale", zap.String("locale", locale), zap.String("scope", catgs), zap.String("scopeFilter", filter))
	newLocale = coreutil.GetCLDRLocale(locale)
	if newLocale == "" {
		err := sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidLocale, locale)
		log.Error(err.Error())
		return "", nil, err
	}

	var returnErr *sgtnerror.MultiError

	resultMap = map[string]interface{}{}
	categories := ArgSplitter.Split(catgs, -1)
	for i := 0; i < len(categories); i++ {
		catg := categories[i]
		catgData, err := localeutil.GetPatternData(ctx, newLocale, catg)
		returnErr = sgtnerror.Append(returnErr, err)
		if err == nil {
			resultMap[catg] = catgData
			categories = addOtherCategories(categories, catg)
		}
	}

	resultMap = processFilters(resultMap, filter)
	getSupplementalData(ctx, resultMap, returnErr)

	return newLocale, resultMap, returnErr.ErrorOrNil()
}

func getSupplementalData(ctx context.Context, resultMap map[string]interface{}, returnErr *sgtnerror.MultiError) {
	suppleMap := map[string]jsoniter.Any{}
	for catg, v := range resultMap {
		if v == nil {
			continue
		}
		if m, ok := v.(map[string]interface{}); ok && len(m) == 0 {
			continue
		}

		if coreType, ok := categoriesWithSupplement[catg]; ok {
			coreData, err := coreutil.GetCoreData(ctx, coreType)
			returnErr = sgtnerror.Append(returnErr, err)
			if err == nil {
				suppleMap[catg] = coreData.(jsoniter.Any)
			}
		}
	}
	if len(suppleMap) > 0 {
		resultMap["supplemental"] = suppleMap
	}
}

func processFilters(data map[string]interface{}, scopeFilter string) map[string]interface{} {
	if scopeFilter == "" || len(data) == 0 {
		return data
	}

	if strings.HasPrefix(scopeFilter, "^") {
		tempFilters := strings.TrimSuffix(strings.TrimPrefix(scopeFilter[1:], "("), ")")
		return excludeNodes(data, tempFilters)
	} else {
		return includeNodes(data, scopeFilter)
	}
}

func excludeNodes(data map[string]interface{}, filters string) map[string]interface{} {
	objxMap := objx.Map(data)
	for _, filter := range strings.Split(filters, common.ParamSep) {
		if filter == "" {
			continue
		}

		filterParts := strings.Split(filter, scopeFilterSep)
		if partData := data[filterParts[0]]; partData != nil {
			if anyValue, ok := partData.(jsoniter.Any); ok {
				mapData := make(map[string]interface{})
				anyValue.ToVal(&mapData)
				data[filterParts[0]] = mapData
			}
			parentPath := strings.Join(filterParts[:len(filterParts)-1], objxMapPathSep)
			parentMap := objxMap.Get(parentPath).ObjxMap()
			delete(parentMap, filterParts[len(filterParts)-1])
		}
	}
	return data
}
func includeNodes(data map[string]interface{}, filters string) map[string]interface{} {
	oldData, newData := objx.Map(data), objx.Map{}
	for _, filter := range strings.Split(filters, common.ParamSep) {
		if filter == "" {
			continue
		}

		filterParts := strings.Split(filter, scopeFilterSep)
		if catgData := oldData[filterParts[0]]; catgData != nil {
			if anyValue, ok := catgData.(jsoniter.Any); ok && len(filterParts) > 1 {
				oldData[filterParts[0]] = anyValue.GetInterface()
			}
		}

		objxPath := strings.Join(filterParts, objxMapPathSep)
		newData.Set(objxPath, oldData.Get(objxPath).Data())
	}

	for k, v := range oldData {
		if _, ok := newData[k]; !ok {
			newData[k] = v
		}
	}

	return newData
}

func addOtherCategories(categories []string, category string) []string {
	if otherCatgs, ok := categoriesNeedOtherCategories[category]; ok {
		for _, otherCatg := range otherCatgs {
			if common.Contains(categories, otherCatg) < 0 {
				categories = append(categories, otherCatg)
			}
		}
	}
	return categories
}
