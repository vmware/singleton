/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package CustomMethod

//COMMENT
import (
	"sync"
)

//!+defaultCache

type defaultCache struct {
	m *sync.Map
}

//NewCache
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

// func (c *defaultCache) getcomponent() *sgtn.dataItem{
// 	var component *sgtn.dataItem
// 	c.m.Range(func(key, value interface{}) bool {
// 		component = value.(*sgtn.dataItem)
// 		return true
// 	})
// 	return component
// }

//!-defaultCache
