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
	defaultLocale string
}

func (t *defaultTrans) GetStringMessage(name, version, locale, component, key string, args ...string) (string, error) {
	if name == "" || version == "" || locale == "" || component == "" || key == "" {
		return key, errors.New(wrongPara)
	}

	compData, err := t.GetComponentMessages(name, version, locale, component)
	if err != nil {
		if strings.Compare(strings.ToLower(locale), strings.ToLower(t.defaultLocale)) != 0 {
			logger.Error("Fallback to default locale because of error: " + err.Error())
			locale = t.defaultLocale
			compData, err = t.GetComponentMessages(name, version, locale, component)
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
