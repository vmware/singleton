/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

type (
	defaultComponentMsgs struct {
		messages          map[string]string
		locale, component string
	}
)

func (d *defaultComponentMsgs) Get(key string) (value string, found bool) {
	value, found = d.messages[key]
	return
}

func (d *defaultComponentMsgs) Size() int {
	return len(d.messages)
}

func (d *defaultComponentMsgs) Locale() string {
	return d.locale
}

func (d *defaultComponentMsgs) Component() string {
	return d.component
}
