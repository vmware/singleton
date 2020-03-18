/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import "sync"

type (
	defaultTranslationMsgs struct {
		dataMap *sync.Map
	}
	defaultComponentMsgs struct {
		messages map[string]string
	}

	translationID struct {
		Name, Version string
	}
	componentID struct {
		Name, Version, Locale, Component string
	}

	translationMsgs interface {
		Put(key componentID, value ComponentMsgs)
		Get(key componentID) (value ComponentMsgs, found bool)
		Size() int
		Clear()
	}
)

// Get Get messages of a component
func (d *defaultTranslationMsgs) Get(key componentID) (value ComponentMsgs, found bool) {
	v, ok := d.dataMap.Load(key)
	if ok {
		return v.(ComponentMsgs), ok
	}

	return nil, ok
}

// Put Set messages of a component
func (d *defaultTranslationMsgs) Put(key componentID, value ComponentMsgs) {
	d.dataMap.Store(key, value)
}

func (d *defaultTranslationMsgs) Size() int {
	s := 0
	d.dataMap.Range(func(key, value interface{}) bool { s++; return true })
	return s
}

func (d *defaultTranslationMsgs) Clear() {
	d.dataMap.Range(func(key, value interface{}) bool { d.dataMap.Delete(key); return true })
}

func (d *defaultComponentMsgs) Get(key string) (value string, found bool) {
	value, found = d.messages[key]
	return
}
func (d *defaultComponentMsgs) Size() int {
	return len(d.messages)
}
