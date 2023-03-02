/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cache

import (
	"time"

	"sgtnserver/internal/logger"

	"github.com/dgraph-io/ristretto"
	"github.com/pkg/errors"
	"go.uber.org/zap"
)

func NewRistrettoCache(config map[string]interface{}) *RistrettoCache {
	maxEntities := config["MaxEntities"].(int64)
	cache, err := ristretto.NewCache(&ristretto.Config{
		NumCounters:        maxEntities * 10,
		MaxCost:            maxEntities,
		BufferItems:        64,
		IgnoreInternalCost: true,
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
		return nil, errors.Errorf("Not found key: '%s'", key)
	}
}

func (c *RistrettoCache) Set(key, value interface{}) error {
	if c.Cache.SetWithTTL(key, value, 1, c.expirationTime) {
		return nil
	} else {
		return errors.Errorf("Fail to store data to cache. key: '%s'", key)
	}
}
