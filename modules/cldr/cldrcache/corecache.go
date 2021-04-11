/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cldrcache

import (
	"context"
	"reflect"

	"sgtnserver/internal/cache"
	"sgtnserver/internal/logger"
	"sgtnserver/modules/cldr"
)

var coreDataCache cache.Cache

func (cldrCache) GetCoreData(ctx context.Context, dataType cldr.CoreDataType, data interface{}) (err error) {
	cacheKey := int(dataType)
	if dataInCache, err := coreDataCache.Get(cacheKey); err == nil {
		reflect.ValueOf(data).Elem().Set(reflect.ValueOf(dataInCache).Elem())
		return nil
	}

	if err = cldrDao.GetCoreData(ctx, dataType, data); err == nil {
		if cacheErr := coreDataCache.Set(cacheKey, data); cacheErr != nil {
			logger.FromContext(ctx).Error(cacheErr.Error())
		}
	}

	return err
}

func init() {
	coreDataCache = cache.NewCache("cldrcore", map[string]interface{}{
		"MaxCost":     20,
		"BufferItems": 64,
	})
}
