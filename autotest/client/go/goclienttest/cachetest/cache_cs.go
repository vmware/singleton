/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cachetest

import (
	"sync"

	sgtn "github.com/vmware/singleton"
)

//!+defaultCache

type (
	productMsgs struct {
		*sync.Map
	}
	componentMsgs struct {
		msgs map[string]string
	}

	defaultCache struct {
		pMessages  productMsgs
		locales    *atomicData
		components *atomicData
	}

	atomicData struct {
		*sync.RWMutex
		data interface{}
	}

	// compAsKey .
	compAsKey struct {
		locale    string
		component string
	}
)

//new cache .
func NewCache() *defaultCache {
	cs := &defaultCache{
		productMsgs{new(sync.Map)},
		newAtomicData(),
		newAtomicData(),
	}

	cs.locales.data = []string{}
	cs.components.data = []string{}

	return cs
}
func (cs *defaultCache) GetLocales() []string {
	data := cs.locales.Get()
	if nil != data {
		return data.([]string)
	}
	return nil
}
func (cs *defaultCache) GetComponents() []string {
	data := cs.components.Get()
	if nil != data {
		return data.([]string)
	}
	return nil
}
func (cs *defaultCache) SetLocales(locales []string) {
	cs.locales.Set(locales)
}
func (cs *defaultCache) SetComponents(components []string) {
	cs.components.Set(components)
}
func (cs *defaultCache) GetComponentMessages(locale, component string) (data sgtn.ComponentMsgs, found bool) {
	compData, ok := cs.pMessages.Load(compAsKey{locale, component})
	if !ok {
		return nil, ok
	}

	return compData.(sgtn.ComponentMsgs), true
}
func (cs *defaultCache) SetComponentMessages(locale, comp string, data sgtn.ComponentMsgs) {
	cs.pMessages.Store(compAsKey{locale, comp}, data)
}

func newAtomicData() *atomicData {
	return &atomicData{
		new(sync.RWMutex),
		nil,
	}
}
func (ad *atomicData) Set(v interface{}) {
	ad.Lock()
	defer ad.Unlock()
	ad.data = v
}
func (ad *atomicData) Get() interface{} {
	ad.RLock()
	defer ad.RUnlock()
	return ad.data
}

//Get ...
func (d *componentMsgs) Get(key string) (value string, found bool) {
	value, found = d.msgs[key]
	return
}

//Size ...
func (d *componentMsgs) Size() int {
	return len(d.msgs)
}

//!-defaultCache
