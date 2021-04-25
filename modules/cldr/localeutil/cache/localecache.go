/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cache

import (
	"context"
	"reflect"
	"sync"

	"sgtnserver/internal/cache"
	"sgtnserver/internal/common"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr/dao"
)

var (
	cldrDao = dao.GetDAO()

	localeDataCache cache.Cache

	localeDataLocks = sync.Map{}
)

type localeCache struct{}

func (localeCache) GetLocaleData(ctx context.Context, cldrLocale, dataType string, data interface{}) (err error) {
	cacheKey := dataType + ":" + cldrLocale

	// Read from cache
	if dataInCache, e := localeDataCache.Get(cacheKey); e == nil {
		reflect.ValueOf(data).Elem().Set(reflect.ValueOf(dataInCache))
		return nil
	}

	// (Read from storage and populate cache) or (wait and read from cache)
	populateCache := func() (err error) {
		err = cldrDao.GetLocaleData(ctx, cldrLocale, dataType, data)
		if err == nil {
			if cacheErr := localeDataCache.Set(cacheKey, reflect.ValueOf(data).Elem().Interface()); cacheErr != nil {
				logger.FromContext(ctx).DPanic(cacheErr.Error())
				return cacheErr
			}
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

func GetCache() localeCache {
	return localeCache{}
}

// InitCLDRCache .
func InitCLDRCache() {
	localeDataCache = cache.NewCache("cldr", map[string]interface{}{
		"MaxCost":     100,
		"BufferItems": 64,
	})
}

func init() {
	InitCLDRCache()
}
