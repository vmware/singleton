/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cache

import (
	"context"
	"reflect"

	"sgtnserver/internal/cache"
	"sgtnserver/internal/logger"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/dao"
)

var (
	cldrDao = dao.GetDAO()

	coreDataCache cache.Cache
)

type coreCache struct{}

func (coreCache) GetCoreData(ctx context.Context, dataType cldr.CoreDataType, data interface{}) (err error) {
	cacheKey := int(dataType)
	if dataInCache, err := coreDataCache.Get(cacheKey); err == nil {
		reflect.ValueOf(data).Elem().Set(reflect.ValueOf(dataInCache))
		return nil
	}

	if err = cldrDao.GetCoreData(ctx, dataType, data); err == nil {
		if cacheErr := coreDataCache.Set(cacheKey, reflect.ValueOf(data).Elem().Interface()); cacheErr != nil {
			logger.FromContext(ctx).Error(cacheErr.Error())
		}
	}

	return err
}

func GetCache() coreCache {
	return coreCache{}
}

func init() {
	coreDataCache = cache.NewCache("cldrcore", map[string]interface{}{
		"MaxEntities": int64(cldr.MaxCoreDataSize),
	})
}
