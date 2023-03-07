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

type transMgr struct {
	*transInst
	fallbackChain []string
}

func newTransMgr(t *transInst, fblocales []string) *transMgr {
	return &transMgr{transInst: t, fallbackChain: fblocales}
}

// GetStringMessage Get a message with optional arguments
func (t *transMgr) GetStringMessage(name, version, locale, component, key string, args ...string) (string, error) {
	bundleData, err := t.getComponentMessages(name, version, locale, component)
	if err == nil {
		if msg, ok := bundleData.Get(key); ok {
			for i, arg := range args {
				placeholder := fmt.Sprintf("{%d}", i)
				msg = strings.Replace(msg, placeholder, arg, 1)
			}
			return msg, nil
		}

		err = fmt.Errorf(notFoundKey, key)
	}

	return key, err
}

// getComponentMessages Get messages of a component with locale fallback
func (t *transMgr) getComponentMessages(name, version, locale, component string) (data ComponentMsgs, err error) {
	bundleData, err := t.transInst.GetComponentMessages(name, version, locale, component)
	if err == nil {
		return bundleData, nil
	}

	for _, fallbackLocale := range t.fallbackChain {
		if fallbackLocale != locale {
			bundleData, err = t.transInst.GetComponentMessages(name, version, fallbackLocale, component)
			if err == nil {
				return bundleData, nil
			}
		}
	}

	return nil, err
}

// GetComponentsMessages Get messages of multiple components
func (t *transMgr) GetComponentsMessages(name, version string, locales, components []string) (msgs []ComponentMsgs, err error) {
	if name == "" || version == "" {
		return nil, errors.New(wrongPara)
	}

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
					logger.Warn(fmt.Sprintf("failed to get translation for {product:%q, version:%q, locale:%q, component:%q}", name, version, locale, component))
				}
			}(locale, component)
		}
	}
	wg.Wait()

	if len(msgs) == 0 { // empty is an error, other cases are successful.
		return nil, fmt.Errorf("no translations are available for {product:%q, version:%q, locales:%v, components:%v}", name, version, locales, components)
	}

	return
}
