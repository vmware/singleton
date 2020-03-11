/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"strings"
)

//!+ defaultTrans
type defaultTrans struct {
	cfg      *Config
	dService *dataService
}

func (t *defaultTrans) GetLocaleList() ([]string, error) {
	return t.dService.GetLocaleList()
}

func (t *defaultTrans) GetComponentList() ([]string, error) {
	return t.dService.GetComponentList()
}

func (t *defaultTrans) GetStringMessage(locale, component, key string, args ...string) (string, error) {
	message, err := t.dService.getStringMessage(locale, component, key)
	if err != nil {
		return message, err
	}

	for i, arg := range args {
		placeholder := fmt.Sprintf("{%d}", i)
		message = strings.Replace(message, placeholder, arg, 1)
	}
	return message, nil
}

func (t *defaultTrans) GetComponentMessages(locale, component string) (ComponentMsgs, error) {
	return t.dService.getComponentMessages(locale, component)
}

//!- defaultTrans
