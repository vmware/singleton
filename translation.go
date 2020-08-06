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

//!+ transInst
type transInst struct {
	msgOrigin messageOrigin
}

func (t *transInst) GetStringMessage(name, version, locale, component, key string, args ...string) (string, error) {
	if name == "" || version == "" || locale == "" || component == "" || key == "" {
		return key, errors.New(wrongPara)
	}
	bundleData, err := t.GetComponentMessages(name, version, locale, component)
	if err != nil {
		return "", err
	}

	if msg, ok := bundleData.Get(key); ok {
		for i, arg := range args {
			placeholder := fmt.Sprintf("{%d}", i)
			msg = strings.Replace(msg, placeholder, arg, 1)
		}
		return msg, nil
	} else {
		return "", fmt.Errorf("fail to get message for locale: %s, component: %s, key: %s", locale, component, key)
	}
}

func (t *transInst) GetLocaleList(name, version string) (data []string, err error) {
	if name == "" || version == "" {
		return nil, errors.New(wrongPara)
	}

	item := &dataItem{dataItemID{itemLocales, name, version, "", ""}, nil, nil}
	err = t.msgOrigin.Get(item)
	if nil != item.data {
		data, _ = item.data.([]string)
	}
	return
}

func (t *transInst) GetComponentList(name, version string) (data []string, err error) {
	if name == "" || version == "" {
		return nil, errors.New(wrongPara)
	}

	item := &dataItem{dataItemID{itemComponents, name, version, "", ""}, nil, nil}
	err = t.msgOrigin.Get(item)
	if nil != item.data {
		data, _ = item.data.([]string)
	}
	return
}

func (t *transInst) GetComponentMessages(name, version, locale, component string) (data ComponentMsgs, err error) {
	if name == "" || version == "" || locale == "" || component == "" {
		return nil, errors.New(wrongPara)
	}

	item := &dataItem{dataItemID{itemComponent, name, version, locale, component}, nil, nil}
	err = t.msgOrigin.Get(item)
	if nil != item.data {
		data, _ = item.data.(ComponentMsgs)
	}
	return
}

//!- transInst
