/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import "sync"

type (
	defaultTransMsgs struct {
		dataMap *sync.Map
	}
	defaultComponentMsgs struct {
		messages map[string]string
	}
)

// Get Get messages of a component
func (d *defaultTransMsgs) Get(key compAsKey) (value ComponentMsgs, found bool) {
	v, ok := d.dataMap.Load(key)
	if ok {
		return v.(ComponentMsgs), ok
	}

	return nil, ok
}

// Put Set messages of a component
func (d *defaultTransMsgs) Put(key compAsKey, value ComponentMsgs) {
	d.dataMap.Store(key, value)
}

func (d *defaultTransMsgs) Size() int {
	s := 0
	d.dataMap.Range(func(key, value interface{}) bool { s++; return true })
	return s
}

func (d *defaultTransMsgs) Clear() {
	d.dataMap.Range(func(key, value interface{}) bool { d.dataMap.Delete(key); return true })
}

func (d *defaultComponentMsgs) Get(key string) (value string, found bool) {
	value, found = d.messages[key]
	return
}
func (d *defaultComponentMsgs) Size() int {
	return len(d.messages)
}
