/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

type (
	defaultComponentMsgs struct {
		messages map[string]string
	}
)

func (d *defaultComponentMsgs) Get(key string) (value string, found bool) {
	value, found = d.messages[key]
	return
}
func (d *defaultComponentMsgs) Size() int {
	return len(d.messages)
}
