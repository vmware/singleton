/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

// MapComponentMsgs is for a ComponentMsgs containing map
type MapComponentMsgs struct {
	messages          map[string]string
	locale, component string
}

// NewMapComponentMsgs is to create a new ComponentMsgs easily
func NewMapComponentMsgs(messages map[string]string, locale, component string) *MapComponentMsgs {
	return &MapComponentMsgs{messages: messages, locale: locale, component: component}
}

// Get returns a message by key
func (d *MapComponentMsgs) Get(key string) (value string, found bool) {
	value, found = d.messages[key]
	return
}

// Set is to save a key-message pair
func (d *MapComponentMsgs) Set(key, value string) {
	d.messages[key] = value
}

// Size returns the number of messages
func (d *MapComponentMsgs) Size() int {
	return len(d.messages)
}

// Locale returns the locale of component
func (d *MapComponentMsgs) Locale() string {
	return d.locale
}

// Component returns the component name
func (d *MapComponentMsgs) Component() string {
	return d.component
}

// Traverse the messages
func (d *MapComponentMsgs) Range(f func(key, value string) bool) {
	for key, value := range d.messages {
		if !f(key, value) {
			break
		}
	}
}

// Clone the component
func (d *MapComponentMsgs) Clone() ComponentMsgs {
	messages := make(map[string]string, len(d.messages))
	for key, value := range d.messages {
		messages[key] = value
	}
	return NewMapComponentMsgs(messages, d.locale, d.component)
}
