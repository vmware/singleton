/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import "fmt"

func (ol messageOriginList) Get(item *dataItem) (err error) {
	switch item.id.iType {
	case itemComponent:
		return ol.getComponentMessages(item)
	case itemLocales, itemComponents:
		return ol.getList(item)
	default:
		return nil
	}
}

func (ol messageOriginList) getComponentMessages(item *dataItem) (err error) {
	for _, o := range ol {
		if err = o.Get(item); err == nil {
			return
		}

		// log error message
		logger.Warn(fmt.Sprintf(originQueryFailure, o, err.Error()))
		if e, ok := err.(stackTracer); ok {
			logger.Warn(fmt.Sprintf("%+v", e.StackTrace()))
		}
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
			// TODO: change error message
			logger.Error(fmt.Sprintf(originQueryFailure, o, err.Error()))
			if e, ok := err.(stackTracer); ok {
				logger.Error(fmt.Sprintf("%+v", e.StackTrace()))
			}
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
