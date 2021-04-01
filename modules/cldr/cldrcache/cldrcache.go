/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldrcache

import (
	"context"
	"net/http"
	"reflect"
	"strings"
	"sync"

	"sgtnserver/internal/cache"
	"sgtnserver/internal/common"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/dao"
)

var (
	coreDataCache   cache.Cache
	localeDataCache cache.Cache
	nonexistentMap  sync.Map
)
var localeDataLocks = sync.Map{}

var (
	LikelySubtagMap           map[string]string
	AvailableLocalesMap       map[string]string
	RegionToLangMap           map[string]string
	LocaleAliasesMap          = map[string]cldr.LocaleAlias{}
	SupplementLanguageDataMap map[string]*cldr.LanguageData
	DefaultContentMap         map[string]string
)

var cachePermanentTypes map[dao.CoreDataType]interface{}

func GetCoreData(ctx context.Context, dataType dao.CoreDataType, data interface{}) (err error) {
	if dataInPermCache, ok := cachePermanentTypes[dataType]; ok {
		reflect.ValueOf(data).Elem().Set(reflect.ValueOf(dataInPermCache))
		return nil
	}

	cacheKey := int(dataType)
	if dataInCache, err := coreDataCache.Get(cacheKey); err == nil {
		reflect.ValueOf(data).Elem().Set(reflect.ValueOf(dataInCache).Elem())
		return nil
	}

	if err = dao.GetCoreData(ctx, dataType, data); err == nil {
		if cacheErr := coreDataCache.Set(cacheKey, data); cacheErr != nil {
			logger.FromContext(ctx).Error(cacheErr.Error())
		}
	}

	return err
}

func GetLocaleData(ctx context.Context, locale, dataType string, data interface{}) (err error) {
	cldrLocale, ok := AvailableLocalesMap[strings.ToLower(locale)]
	if !ok {
		err := sgtnerror.StatusNotFound.WithUserMessage(cldr.InvalidLocale, locale)
		logger.FromContext(ctx).Error(err.Error())
		return err
	}

	cacheKey := dataType + ":" + cldrLocale

	// Read from cache
	if dataInCache, e := localeDataCache.Get(cacheKey); e == nil {
		reflect.ValueOf(data).Elem().Set(reflect.ValueOf(dataInCache))
		return nil
	}
	if _, ok := nonexistentMap.Load(cacheKey); ok {
		return sgtnerror.StatusNotFound.WithUserMessage("Locale is '%s', type is %v", locale, dataType)
	}

	// (Read from storage and populate cache) or (wait and read from cache)
	populateCache := func() (err error) {
		err = dao.GetLocaleData(ctx, dataType, cldrLocale, data)
		if err == nil {
			if cacheErr := localeDataCache.Set(cacheKey, reflect.ValueOf(data).Elem().Interface()); cacheErr != nil {
				logger.FromContext(ctx).DPanic(cacheErr.Error())
				return cacheErr
			}
		} else if sgtnerror.GetCode(err) == http.StatusNotFound {
			nonexistentMap.Store(cacheKey, nil) // this is for contextTransforms, only part of locales have this data. Save result to avoid querying from storage repeatedly
		}
		return err
	}
	getFromCache := func() error {
		_, err := localeDataCache.Get(cacheKey)
		return err
	}

	actual, loaded := localeDataLocks.LoadOrStore(cacheKey, make(chan struct{}))
	if !loaded {
		defer localeDataLocks.Delete(cacheKey)
		err = common.DoAndCheck(ctx, actual.(chan struct{}), populateCache, getFromCache)
	} else { // For the routine waiting for cache population, get from cache
		<-actual.(chan struct{})
		if dataInCache, e := localeDataCache.Get(cacheKey); e == nil {
			reflect.ValueOf(data).Elem().Set(reflect.ValueOf(dataInCache))
		} else {
			err = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(e, common.FailToReadCache, cacheKey)
			logger.FromContext(ctx).DPanic(err.Error())
		}
	}

	return
}

// InitCLDRCache .
func InitCLDRCache() {
	localeDataCache = cache.NewCache("cldr", map[string]interface{}{
		"MaxCost":     200,
		"BufferItems": 64,
	})
	coreDataCache = cache.NewCache("cldrcore", map[string]interface{}{
		"MaxCost":     20,
		"BufferItems": 64,
	})
	cachePermanentTypes = map[dao.CoreDataType]interface{}{}

	err := dao.GetCoreData(nil, dao.CoreSplmtLikelySubTags, &LikelySubtagMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		cachePermanentTypes[dao.CoreSplmtLikelySubTags] = LikelySubtagMap
	}
	err = dao.GetCoreData(nil, dao.CoreAvaLocales, &AvailableLocalesMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		cachePermanentTypes[dao.CoreAvaLocales] = AvailableLocalesMap
	}

	err = dao.GetCoreData(nil, dao.RegionToLanguage, &RegionToLangMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		cachePermanentTypes[dao.RegionToLanguage] = RegionToLangMap
	}

	var tempAliasMap map[string]cldr.LocaleAlias
	err = dao.GetCoreData(nil, dao.CoreSplmtAlias, &tempAliasMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		for k, v := range tempAliasMap {
			LocaleAliasesMap[strings.ToLower(k)] = v
		}
		cachePermanentTypes[dao.CoreSplmtAlias] = LocaleAliasesMap
	}

	err = dao.GetCoreData(nil, dao.CoreSplmtLanguageData, &SupplementLanguageDataMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		cachePermanentTypes[dao.CoreSplmtLanguageData] = SupplementLanguageDataMap
	}

	err = dao.GetCoreData(nil, dao.DefaultContent, &DefaultContentMap)
	if err != nil {
		logger.Log.Fatal(err.Error())
	} else {
		cachePermanentTypes[dao.DefaultContent] = DefaultContentMap
	}
}

func init() {
	InitCLDRCache()
}
