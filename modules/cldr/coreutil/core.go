/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package coreutil

import (
	"context"
	"strings"

	"sgtnserver/internal/logger"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/cldrcache"
	"sgtnserver/modules/cldr/dao"

	jsoniter "github.com/json-iterator/go"
)

var (
	LikelySubtagMap           map[string]string
	AvailableLocalesMap       map[string]string
	RegionToLangMap           map[string]string
	LocaleAliasesMap          = map[string]cldr.LocaleAlias{}
	SupplementLanguageDataMap map[string]*cldr.LanguageData
	DefaultContentMap         map[string]string
)

var (
	cachedTypes = map[cldr.CoreDataType]interface{}{}

	EnableCache = true

	dataOrigin coreDataOrigin
)

type coreDataOrigin interface {
	GetCoreData(ctx context.Context, t cldr.CoreDataType, data interface{}) error
}

func GetCoreData(ctx context.Context, t cldr.CoreDataType) (interface{}, error) {
	if dataInPermCache, ok := cachedTypes[t]; ok {
		return dataInPermCache, nil
	}

	var data jsoniter.Any
	err := dataOrigin.GetCoreData(ctx, t, &data)
	return data, err
}

func init() {
	if EnableCache {
		dataOrigin = cldrcache.GetCache()
	} else {
		dataOrigin = dao.GetDAO()
	}

	err := dataOrigin.GetCoreData(context.TODO(), cldr.CoreSplmtLikelySubTags, &LikelySubtagMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		cachedTypes[cldr.CoreSplmtLikelySubTags] = LikelySubtagMap
	}
	err = dataOrigin.GetCoreData(context.TODO(), cldr.CoreAvaLocales, &AvailableLocalesMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		cachedTypes[cldr.CoreAvaLocales] = AvailableLocalesMap
	}

	err = dataOrigin.GetCoreData(context.TODO(), cldr.RegionToLanguage, &RegionToLangMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		cachedTypes[cldr.RegionToLanguage] = RegionToLangMap
	}

	var tempAliasMap map[string]cldr.LocaleAlias
	err = dataOrigin.GetCoreData(context.TODO(), cldr.CoreSplmtAlias, &tempAliasMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		for k, v := range tempAliasMap {
			LocaleAliasesMap[strings.ToLower(k)] = v
		}
		cachedTypes[cldr.CoreSplmtAlias] = LocaleAliasesMap
	}

	err = dataOrigin.GetCoreData(context.TODO(), cldr.CoreSplmtLanguageData, &SupplementLanguageDataMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		cachedTypes[cldr.CoreSplmtLanguageData] = SupplementLanguageDataMap
	}

	err = dataOrigin.GetCoreData(context.TODO(), cldr.DefaultContent, &DefaultContentMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		cachedTypes[cldr.DefaultContent] = DefaultContentMap
	}
}
