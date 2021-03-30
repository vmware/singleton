/*
 * Copyright 2021 VMware, Inc.
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
	if bundleData, err := c.Cache.Get(cacheKey); err == nil {
		return bundleData.(*translation.Bundle), nil
	}

	populateCache := func() error {
		data, err = c.DAO.GetBundle(ctx, id)
		if err == nil {
			if setCacheError := c.Cache.Set(cacheKey, data); setCacheError != nil {
				logger.FromContext(ctx).DPanic(setCacheError.Error())
				return setCacheError
			}
		}
		return err
	}
	var getCacheError error // For log error message before return
	getFromCache := func() error {
		if bundleData, e := c.Cache.Get(cacheKey); e == nil {
			data = bundleData.(*translation.Bundle)
			err = nil
		} else {
			err = sgtnerror.StatusInternalServerError.WrapErrorWithMessage(e, "Fail to read from cache: '%s'", cacheKey)
			// because this function is used to verify cache is ready, my run many times. So can't log error here, log it below
		}
		getCacheError = err
		return err
	}
	actual, loaded := c.locks.LoadOrStore(cacheKey, make(chan struct{}))
	if !loaded {
		defer c.locks.Delete(cacheKey)
	}
	common.DoOrWait(ctx, actual.(chan struct{}), populateCache, getFromCache, !loaded)

	if loaded && getCacheError != nil { // For the routine waiting for cache population, should log the error message.
		logger.FromContext(ctx).DPanic(getCacheError.Error())
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
