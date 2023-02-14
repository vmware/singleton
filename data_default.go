/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

type MapComponentMsgs struct {
	messages          map[string]string
	locale, component string
}

// Create a new Component
func NewMapComponentMsgs(messages map[string]string, locale, component string) *MapComponentMsgs {
	return &MapComponentMsgs{messages: messages, locale: locale, component: component}
}

// Get a message by key
func (d *MapComponentMsgs) Get(key string) (value string, found bool) {
	value, found = d.messages[key]
	return
}

// Set a message by key
func (d *MapComponentMsgs) Set(key, value string) {
	d.messages[key] = value
}

// Return number of the messages
func (d *MapComponentMsgs) Size() int {
	return len(d.messages)
}

// Return locale of the component
func (d *MapComponentMsgs) Locale() string {
	return d.locale
}

// Return the component name
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
