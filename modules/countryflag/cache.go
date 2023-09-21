/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package countryflag

import (
	"context"
	"fmt"

	"sgtnserver/internal/cache"
)

type flagCache struct {
	cache cache.Cache
	dao
}

func newFlagCache(d dao) *flagCache {
	return &flagCache{
		cache: cache.NewCache("countryflag", map[string]interface{}{"MaxEntities": int64(100)}),
		dao:   d}
}

func (fc *flagCache) GetFlag(ctx context.Context, region string, scale FlagScale) (data string, err error) {
	key := getCacheKey(region, scale)
	if v, err := fc.cache.Get(key); err == nil {
		return v.(string), nil
	}

	data, err = fc.dao.GetFlag(ctx, region, scale)
	if err == nil {
		fc.cache.Set(key, data)
	}
	return
}

func getCacheKey(region string, scale FlagScale) string {
	return fmt.Sprintf("%v:%v", scale, region)
}
