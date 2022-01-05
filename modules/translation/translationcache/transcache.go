/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translationcache

import (
	"context"
	"fmt"
	"sync"

	"sgtnserver/internal/cache"
	"sgtnserver/internal/common"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/translation"
)

type TransCacheMgr struct {
	DAO   translation.MessageOrigin
	Cache cache.Cache
	locks sync.Map
}

func NewCacheManager(d translation.MessageOrigin, c cache.Cache) *TransCacheMgr {
	return &TransCacheMgr{DAO: d, Cache: c, locks: sync.Map{}}
}

func (c *TransCacheMgr) GetBundleInfo(ctx context.Context) (*translation.BundleInfo, error) {
	return c.DAO.GetBundleInfo(ctx)
}

func (c *TransCacheMgr) GetBundle(ctx context.Context, id *translation.BundleID) (data *translation.Bundle, err error) {
	cacheKey := getCacheKey(id)

	// Read from cache
	if dataInCache, err := c.Cache.Get(cacheKey); err == nil {
		return dataInCache.(*translation.Bundle), nil
	}

	// (Read from storage and populate cache) or (wait and read from cache)
	populateCache := func() (err error) {
		data, err = c.DAO.GetBundle(ctx, id)
		if err == nil {
			if cacheErr := c.Cache.Set(cacheKey, data); cacheErr != nil {
				logger.FromContext(ctx).DPanic(cacheErr.Error())
				return cacheErr
			}
		}
		return
	}
	getFromCache := func() error {
		_, err := c.Cache.Get(cacheKey)
		return err
	}

	actual, loaded := c.locks.LoadOrStore(cacheKey, make(chan struct{}))
	if !loaded {
		defer c.locks.Delete(cacheKey)
		err = common.DoAndCheck(ctx, actual.(chan struct{}), populateCache, getFromCache)
	} else { // For the routine waiting for cache population, get from cache
		<-actual.(chan struct{})
		if dataInCache, e := c.Cache.Get(cacheKey); e == nil {
			data = dataInCache.(*translation.Bundle)
		} else {
			err = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(e, common.FailToReadCache, cacheKey)
			logger.FromContext(ctx).DPanic(err.Error())
		}
	}

	return
}

func (c *TransCacheMgr) PutBundle(ctx context.Context, bundleData *translation.Bundle) error {
	err := c.DAO.PutBundle(ctx, bundleData)
	if err == nil {
		cacheKey := getCacheKey(&bundleData.ID)
		if _, err := c.Cache.Get(cacheKey); err == nil { // Update cache only when messages have been cached before.
			if cacheErr := c.Cache.Set(cacheKey, bundleData); cacheErr != nil {
				logger.FromContext(ctx).Error(cacheErr.Error())
			}
		}
	}

	return err
}

// func (c *TransCacheMgr) DeleteBundle(ctx context.Context, bundleID *translation.BundleID) error {
// 	return c.DAO.DeleteBundle(ctx, bundleID)
// }

func getCacheKey(id *translation.BundleID) string {
	return fmt.Sprintf("%s:%s:%s:%s", id.Name, id.Version, id.Locale, id.Component)
}

func (c *TransCacheMgr) ClearCache(ctx context.Context) (err error) {
	log := logger.FromContext(ctx)
	log.Debug("Clear translation cache")
	if err = c.Cache.Clear(); err != nil {
		err = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(err, "Fail to clear translation cache")
		log.Error(err.Error())
	}

	return
}
