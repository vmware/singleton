/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"sync"
)

type defaultCache struct {
	m *sync.Map
}

func newCache() Cache {
	return &defaultCache{new(sync.Map)}
}
func (c *defaultCache) Get(key interface{}) (value interface{}, found bool) {
	return c.m.Load(key)
}
func (c *defaultCache) Set(key interface{}, value interface{}) {
	c.m.Store(key, value)
}
