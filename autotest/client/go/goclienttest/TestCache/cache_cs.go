/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cachetest

import (
	"sync"
)

//!+defaultCache

type defaultCache struct {
	m *sync.Map
}

// new cache.
func NewCache() *defaultCache {
	return &defaultCache{new(sync.Map)}
}

func (c *defaultCache) Get(key interface{}) (value interface{}, found bool) {
	return c.m.Load(key)
}
func (c *defaultCache) Set(key interface{}, value interface{}) {
	c.m.Store(key, value)
}

func (c *defaultCache) Clear() {
	c.m.Range(func(key, value interface{}) bool {
		c.m.Delete(key)
		return true
	})
}

//!-defaultCache
