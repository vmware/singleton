/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package coreutil

import (
	"context"
	"strings"

	jsoniter "github.com/json-iterator/go"

	"sgtnserver/internal/logger"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/cldrcache"
	"sgtnserver/modules/cldr/dao"
)

var (
	LikelySubtagMap           map[string]string
	AvailableLocalesMap       map[string]string
	RegionToLangMap           map[string]string
	LocaleAliasesMap          = map[string]cldr.LocaleAlias{}
	SupplementLanguageDataMap map[string]*cldr.LanguageData
	DefaultContentMap         map[string]string
)

var permanentCachedTypes = map[cldr.CoreDataType]interface{}{}

var EnableCache = false

var coreDao cldrCoreDAO

type cldrCoreDAO interface {
	GetCoreData(ctx context.Context, t cldr.CoreDataType, data interface{}) error
}

func GetCoreData(ctx context.Context, t cldr.CoreDataType) (interface{}, error) {
	if dataInPermCache, ok := permanentCachedTypes[t]; ok {
		return dataInPermCache, nil
	}

	var data jsoniter.Any
	err := coreDao.GetCoreData(ctx, t, &data)
	return data, err
}

func init() {
	if EnableCache {
		coreDao = cldrcache.GetCache()
	} else {
		coreDao = dao.GetDAO()
	}

	err := coreDao.GetCoreData(context.TODO(), cldr.CoreSplmtLikelySubTags, &LikelySubtagMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		permanentCachedTypes[cldr.CoreSplmtLikelySubTags] = LikelySubtagMap
	}
	err = coreDao.GetCoreData(context.TODO(), cldr.CoreAvaLocales, &AvailableLocalesMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		permanentCachedTypes[cldr.CoreAvaLocales] = AvailableLocalesMap
	}

	err = coreDao.GetCoreData(context.TODO(), cldr.RegionToLanguage, &RegionToLangMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		permanentCachedTypes[cldr.RegionToLanguage] = RegionToLangMap
	}

	var tempAliasMap map[string]cldr.LocaleAlias
	err = coreDao.GetCoreData(context.TODO(), cldr.CoreSplmtAlias, &tempAliasMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		for k, v := range tempAliasMap {
			LocaleAliasesMap[strings.ToLower(k)] = v
		}
		permanentCachedTypes[cldr.CoreSplmtAlias] = LocaleAliasesMap
	}

	err = coreDao.GetCoreData(context.TODO(), cldr.CoreSplmtLanguageData, &SupplementLanguageDataMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		permanentCachedTypes[cldr.CoreSplmtLanguageData] = SupplementLanguageDataMap
	}

	err = coreDao.GetCoreData(context.TODO(), cldr.DefaultContent, &DefaultContentMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		permanentCachedTypes[cldr.DefaultContent] = DefaultContentMap
	}
}
