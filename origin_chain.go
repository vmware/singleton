/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"

	"github.com/pkg/errors"
)

func (ol messageOriginList) Get(item *dataItem) (err error) {
	switch item.id.iType {
	case itemComponent:
		return ol.getComponentMessages(item)
	case itemLocales, itemComponents:
		return ol.getList(item)
	default:
		return errors.Errorf(invalidItemType, item.id.iType)
	}
}

func (ol messageOriginList) getComponentMessages(item *dataItem) (err error) {
	for _, o := range ol {
		if err = o.Get(item); err == nil {
			return
		}

		logger.Warn(fmt.Sprintf(originQueryFailure, o, err))
	}

	return
}

func (ol messageOriginList) getList(item *dataItem) (returnError error) {
	var tempList = make([]([]string), 0, len(ol))
	for _, o := range ol {
		if err := o.Get(item); err == nil {
			tempList = append(tempList, item.data.([]string))
		} else {
			returnError = err
			logger.Error(fmt.Sprintf(originQueryFailure, o, err))
		}
	}

	totalResult := uniqueStrings(tempList...)
	if len(totalResult) > 0 {
		item.data = totalResult
		return nil
	}

	return // Here must have an error returned
}

func (ol messageOriginList) IsExpired(item *dataItem) bool {
	for _, o := range ol {
		if o.IsExpired(item) {
			return true
		}
		if item.origin == o { // for components and locales, origin is the last successful one in the chain
			return false
		}
	}

	return false
}
