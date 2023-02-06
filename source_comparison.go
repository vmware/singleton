/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"sync"
)

type sourceComparison struct {
	messageOrigin
	source messageOrigin
}

func (sc *sourceComparison) Get(item *dataItem) (err error) {
	switch item.id.iType {
	case itemComponent:
		if item.id.Locale == inst.cfg.GetSourceLocale() {
			return sc.source.Get(item)
		} else {
			return sc.getTranslation(item)
		}
	case itemLocales: // Get locale list from translation
		return sc.messageOrigin.Get(item)
	case itemComponents: // get component list from source
		return sc.source.Get(item)
	default:
		return nil
	}
}

// func (sc *sourceComparison) getList(item *dataItem) error {
// 	var sourceErr, err error
// 	sourceItem := &dataItem{id: item.id}
// 	if sourceErr = sc.source.Get(sourceItem); sourceErr != nil {
// 		// TODO: log error
// 		logger.Error(sourceErr.Error())
// 	}

// 	if err = sc.messageOrigin.Get(item); err != nil {
// 		// TODO: log error
// 		logger.Error(err.Error())
// 	}

// 	if sourceErr != nil && err != nil {
// 		return sourceErr
// 	} else {
// 		if sourceErr == nil && err == nil {
// 			item.data = uniqueStrings(sourceItem.data.([]string), item.data.([]string))
// 		} else if err != nil {
// 			*item = *sourceItem
// 		}

// 		return nil
// 	}
// }

func (sc *sourceComparison) getTranslation(item *dataItem) (err error) {
	if err = sc.messageOrigin.Get(item); err != nil {
		return
	}

	oldSourceItem := &dataItem{id: item.id}
	oldSourceItem.id.Locale = inst.cfg.GetSourceLocale()
	newSourceItem := &dataItem{id: item.id}

	var wg sync.WaitGroup
	wg.Add(2)

	go func() {
		// TODO: if failing to get old source, then need to add new messages in source item
		if oldSourceErr := item.origin.Get(oldSourceItem); oldSourceErr != nil {
			// TODO: log error
			logger.Error(oldSourceErr.Error())
		}
	}()
	go func() {
		if newSourceErr := sc.source.Get(newSourceItem); newSourceErr != nil {
			// TODO: log error
			logger.Error(newSourceErr.Error())
		}
	}()

	wg.Wait()

	if newSourceItem.data == nil {
		return
	}

	oldSourceMessages, _ := oldSourceItem.data.(ComponentMsgs)
	item.data = sc.doComparison(item.data.(ComponentMsgs), oldSourceMessages, newSourceItem.data.(ComponentMsgs))
	return nil
}

func (sc *sourceComparison) doComparison(transBundle, oldSource, newSource ComponentMsgs) ComponentMsgs {
	newSource.Range(func(key, newSourceMessage string) bool {
		if oldSource != nil {
			if oldSourceMessage, _ := oldSource.Get(key); oldSourceMessage != newSourceMessage {
				transBundle.Set(key, newSourceMessage)
			}
		} else if _, ok := transBundle.Get(key); !ok {
			transBundle.Set(key, newSourceMessage)
		}
		return true
	})

	return transBundle
}

func (sc *sourceComparison) IsExpired(item *dataItem) bool {
	if item.id.iType == itemComponent && item.id.Locale == inst.cfg.GetSourceLocale() {
		return sc.source.IsExpired(item)
	}

	return sc.messageOrigin.IsExpired(item)
}
