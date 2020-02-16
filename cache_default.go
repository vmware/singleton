/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"sync"
	"sync/atomic"
)

type (
	defaultCache struct {
		tMessages  transMsgs
		locales    atomic.Value
		components atomic.Value
	}
)

func newCache() Cache {
	c := &defaultCache{
		&defaultTransMsgs{new(sync.Map)},
		atomic.Value{},
		atomic.Value{},
	}

	c.locales.Store([]string{})
	c.components.Store([]string{})

	return c
}
func (c *defaultCache) GetLocales() []string {
	data := c.locales.Load()
	if nil != data {
		return data.([]string)
	}
	return nil
}
func (c *defaultCache) GetComponents() []string {
	data := c.components.Load()
	if nil != data {
		return data.([]string)
	}
	return nil
}
func (c *defaultCache) SetLocales(locales []string) {
	c.locales.Store(locales)
}
func (c *defaultCache) SetComponents(components []string) {
	c.components.Store(components)
}
func (c *defaultCache) GetComponentMessages(locale, comp string) (data ComponentMsgs, found bool) {
	compData, ok := c.tMessages.Get(compAsKey{locale, comp})
	if !ok {
		return nil, ok
	}

	return compData, true
}
func (c *defaultCache) SetComponentMessages(locale, comp string, data ComponentMsgs) {
	c.tMessages.Put(compAsKey{locale, comp}, data)
}
