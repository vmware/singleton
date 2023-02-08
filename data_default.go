/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

type MapComponentMsgs struct {
	messages          map[string]string
	locale, component string
}

func NewMapComponentMsgs(messages map[string]string, locale, component string) *MapComponentMsgs {
	return &MapComponentMsgs{messages: messages, locale: locale, component: component}
}

func (d *MapComponentMsgs) Get(key string) (value string, found bool) {
	value, found = d.messages[key]
	return
}

func (d *MapComponentMsgs) Set(key, value string) {
	d.messages[key] = value
}

func (d *MapComponentMsgs) Size() int {
	return len(d.messages)
}

func (d *MapComponentMsgs) Locale() string {
	return d.locale
}

func (d *MapComponentMsgs) Component() string {
	return d.component
}

func (d *MapComponentMsgs) Range(f func(key, value string) bool) {
	for key, value := range d.messages {
		if !f(key, value) {
			break
		}
	}
}

func (d *MapComponentMsgs) Clone() ComponentMsgs {
	messages := make(map[string]string, len(d.messages))
	for key, value := range d.messages {
		messages[key] = value
	}
	return NewMapComponentMsgs(messages, d.locale, d.component)
}
