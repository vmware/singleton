/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"strings"
	"sync"

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

	item := &dataItem{dataItemID{itemLocales, name, version, "", ""}, nil, nil, nil}
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

	item := &dataItem{dataItemID{itemComponents, name, version, "", ""}, nil, nil, nil}
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

	item := &dataItem{dataItemID{itemComponent, name, version, locale, component}, nil, nil, nil}
	err = t.msgOrigin.Get(item)
	if nil != item.data {
		data, _ = item.data.(ComponentMsgs)
	}
	return
}

func (t *transInst) GetComponentsMessages(name, version string, locales, components []string) (msgs []ComponentMsgs, err error) {
	if len(locales) == 0 {
		locales, err = t.GetLocaleList(name, version)
		if err != nil {
			return nil, err
		}
	}
	if len(components) == 0 {
		components, err = t.GetComponentList(name, version)
		if err != nil {
			return nil, err
		}
	}

	totalNumber := len(locales) * len(components)
	msgs = make([]ComponentMsgs, 0, totalNumber)

	var wg sync.WaitGroup
	wg.Add(totalNumber)
	muList := sync.Mutex{}
	for _, locale := range locales {
		for _, component := range components {
			go func(locale, component string) {
				defer wg.Done()

				if bundleMsgs, bundleErr := t.GetComponentMessages(name, version, locale, component); bundleErr == nil {
					muList.Lock()
					msgs = append(msgs, bundleMsgs)
					muList.Unlock()
				} else { // log a warning message if translation is unavailable.
					logger.Warn(fmt.Sprintf("fail to get translation for {product '%s', version '%s', locale '%s', component '%s'}", name, version, locale, component))
				}
			}(locale, component)
		}
	}
	wg.Wait()

	if len(msgs) == 0 { // empty is an error, other cases are successful.
		return nil, fmt.Errorf("no translations are available for {product '%s', version '%s', locales '%v', components '%v'}", name, version, locales, components)
	}

	return
}

//!- transInst
