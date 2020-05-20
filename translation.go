/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"strings"

	"github.com/pkg/errors"
)

//!+ defaultTrans
type defaultTrans struct {
	ds            *dataService
	fallbackChain []string
}

func (t *defaultTrans) GetStringMessage(name, version, locale, component, key string, args ...string) (string, error) {
	if name == "" || version == "" || locale == "" || component == "" || key == "" {
		return key, errors.New(wrongPara)
	}

	var message string

	// localeSlice := make([]string, 0, len(t.fallbackChain)+1)
	// localeSlice = append(localeSlice, locale)
	// index := contains(t.fallbackChain, locale)
	// if index < 0 {
	// 	localeSlice = append(localeSlice, t.fallbackChain...)
	// } else {
	// 	localeSlice = append(localeSlice, t.fallbackChain[0:index]...)
	// 	localeSlice = append(localeSlice, t.fallbackChain[index+1:]...)
	// }

	compData, err := t.GetComponentMessages(name, version, locale, component)
	if err == nil {
		message, _ = compData.Get(key)
	}
	if len(message) == 0 {
		for _, fbLocle := range t.fallbackChain {
			if strings.ToLower(locale) != strings.ToLower(fbLocle) {
				logger.Warn(fmt.Sprintf("Fallback to %s because of error: %v", fbLocle, err))
				compData, err = t.GetComponentMessages(name, version, fbLocle, component)
				if err == nil {
					if message, _ = compData.Get(key); len(message) != 0 {
						break
					}
				}
			}
		}
	}

	if err != nil {
		return key, err
	}

	if len(message) == 0 {
		errMsg := fmt.Sprintf("Fail to get message for locale: %s, component: %s, key: %s", locale, component, key)
		return key, errors.New(errMsg)
	}

	for i, arg := range args {
		placeholder := fmt.Sprintf("{%d}", i)
		message = strings.Replace(message, placeholder, arg, 1)
	}

	return message, nil
}

func (t *defaultTrans) GetLocaleList(name, version string) (data []string, err error) {
	if name == "" || version == "" {
		return nil, errors.New(wrongPara)
	}

	item := &dataItem{dataItemID{itemLocales, name, version, "", ""}, nil, nil}
	err = t.ds.get(item)
	data, _ = item.data.([]string)
	return
}

func (t *defaultTrans) GetComponentList(name, version string) (data []string, err error) {
	if name == "" || version == "" {
		return nil, errors.New(wrongPara)
	}

	item := &dataItem{dataItemID{itemComponents, name, version, "", ""}, nil, nil}
	err = t.ds.get(item)
	data, _ = item.data.([]string)
	return
}

func (t *defaultTrans) GetComponentMessages(name, version, locale, component string) (data ComponentMsgs, err error) {
	if name == "" || version == "" || locale == "" || component == "" {
		return nil, errors.New(wrongPara)
	}

	item := &dataItem{dataItemID{itemComponent, name, version, locale, component}, nil, nil}
	err = t.ds.get(item)
	data, _ = item.data.(ComponentMsgs)
	return
}

//!- defaultTrans
