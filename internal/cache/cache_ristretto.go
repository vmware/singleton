/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cache

import (
	"fmt"
	"time"

	"sgtnserver/internal/logger"

	"github.com/dgraph-io/ristretto"
	"go.uber.org/zap"
)

func NewRistrettoCache(config map[string]interface{}) *RistrettoCache {
	maxCost := int64(config["MaxCost"].(int))
	bufferItems := int64(config["BufferItems"].(int))
	cache, err := ristretto.NewCache(&ristretto.Config{
		NumCounters: maxCost * 10, // number of keys to track frequency of (10M).
		MaxCost:     maxCost,      // maximum cost of cache (1GB).
		BufferItems: bufferItems,  // number of keys per Get buffer.
	})
	if err != nil {
		logger.Log.Fatal("Fail to create cache", zap.Error(err))
	}

	t, _ := config["Expiration"].(time.Duration)
	return &RistrettoCache{expirationTime: t, Cache: cache}
}

type RistrettoCache struct {
	expirationTime time.Duration
	*ristretto.Cache
}

func (c *RistrettoCache) Clear() error {
	c.Cache.Clear()
	return nil
}

// func (c *RistrettoCache) Delete(key interface{}) error {
// 	c.Cache.Del(key)
// 	return nil
// }

func (c *RistrettoCache) Get(key interface{}) (interface{}, error) {
	v, found := c.Cache.Get(key)
	if found {
		return v, nil
	} else {
		return nil, fmt.Errorf("Not found key: '%s'", key)
	}
}

func (c *RistrettoCache) Set(key, value interface{}) error {
	if c.Cache.SetWithTTL(key, value, 1, c.expirationTime) {
		return nil
	} else {
		return fmt.Errorf("Fail to store data to cache. key: '%s'", key)
	}
}
