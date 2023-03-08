/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"github.com/pkg/errors"
)

//!+ transInst
type transInst struct {
	msgOrigin messageOrigin
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

//!- transInst
