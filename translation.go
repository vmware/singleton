/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"errors"
	"fmt"
	"strings"
)

// Translation interface of translation
type Translation interface {
	// GetLocaleList Get locale list
	GetLocaleList(name, version string) ([]string, error)

	// GetComponentList Get component list
	GetComponentList(name, version string) ([]string, error)

	// GetStringMessage Get a message with optional arguments
	GetStringMessage(name, version, locale, component, key string, args ...string) (string, error)

	// GetComponentMessages Get component messages
	GetComponentMessages(name, version, locale, component string) (ComponentMsgs, error)
}

//!+ defaultTrans
type defaultTrans struct {
	*dataService
	defaultLocale string
}

func (t *defaultTrans) GetStringMessage(name, version, locale, component, key string, args ...string) (string, error) {

	compData, err := t.dataService.GetComponentMessages(name, version, locale, component)
	if err != nil {
		if strings.Compare(strings.ToLower(locale), strings.ToLower(t.defaultLocale)) != 0 {
			logger.Error("Fallback to default locale because of error: " + err.Error())
			locale = t.defaultLocale
			compData, err = t.dataService.GetComponentMessages(name, version, locale, component)
		}
	}
	if err != nil {
		return key, err
	}

	message, ok := compData.Get(key)
	if !ok {
		errMsg := "No key in locale: " + locale + ", component: " + component
		return key, errors.New(errMsg)
	}

	for i, arg := range args {
		placeholder := fmt.Sprintf("{%d}", i)
		message = strings.Replace(message, placeholder, arg, 1)
	}

	return message, nil
}

//!- defaultTrans
