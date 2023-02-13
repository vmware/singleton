/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

var cache Cache

// !+cacheService
type cacheService struct {
	messageOrigin
}

func (s *cacheService) Get(item *dataItem) (err error) {
	dataInCache, ok := cache.Get(item.id)
	if ok {
		cachedItem := dataInCache.(*dataItem)
		item.data = cachedItem.data
		if s.messageOrigin.IsExpired(cachedItem) {
			go s.messageOrigin.Get(&dataItem{id: item.id, attrs: cachedItem.attrs})
		}

		return nil
	}

	return s.messageOrigin.Get(item)
}

// !-cacheService

type saveToCache struct {
	messageOrigin
}

func (s *saveToCache) Get(item *dataItem) (err error) {
	err = s.messageOrigin.Get(item)
	if err == nil {
		cache.Set(item.id, item)
	}

	return
}
