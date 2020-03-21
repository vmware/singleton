/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"sync"
)

var cache Cache

type (
	defaultCache struct {
		m *sync.Map
	}
)

func newCache() Cache {
	c := &defaultCache{
		new(sync.Map),
	}

	return c
}
func (c *defaultCache) Get(key interface{}) (value interface{}, found bool) {
	return c.m.Load(key)
}

func (c *defaultCache) Set(key interface{}, value interface{}) {
	c.m.Store(key, value)
}
