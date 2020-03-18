/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"sync"
)

type (
	defaultCache struct {
		tMessages  transMsgs
		locales    *sync.Map
		components *sync.Map
	}
)

func newCache() Cache {
	c := &defaultCache{
		&defaultTransMsgs{new(sync.Map)},
		new(sync.Map),
		new(sync.Map),
	}

	return c
}
func (c *defaultCache) GetLocales(name, version string) []string {
	data, ok := c.locales.Load(translationID{name, version})
	if ok {
		return data.([]string)
	}
	return nil
}
func (c *defaultCache) GetComponents(name, version string) []string {
	data, ok := c.components.Load(translationID{name, version})
	if ok {
		return data.([]string)
	}
	return nil
}
func (c *defaultCache) SetLocales(name, version string, locales []string) {
	c.locales.Store(translationID{name, version}, locales)
}
func (c *defaultCache) SetComponents(name, version string, components []string) {
	c.components.Store(translationID{name, version}, components)
}
func (c *defaultCache) GetComponentMessages(name, version, locale, comp string) (data ComponentMsgs, found bool) {
	compData, ok := c.tMessages.Get(componentID{name, version, locale, comp})
	if !ok {
		return nil, ok
	}

	return compData, true
}
func (c *defaultCache) SetComponentMessages(name, version, locale, comp string, data ComponentMsgs) {
	c.tMessages.Put(componentID{name, version, locale, comp}, data)
}
