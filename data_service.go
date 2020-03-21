/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"net/http"
	"strconv"
	"time"

	"errors"
)

//!+dataService
type dataService struct {
	// enableCache bool

	cache Cache

	bundle *bundleDAO
	server *serverDAO
}

type itemType int

const (
	itemComponent itemType = iota
	itemLocales
	itemComponents
)

func (t itemType) String() string {
	switch t {
	case itemComponent:
		return "component"
	case itemLocales:
		return "locales"
	case itemComponents:
		return "components"
	default:
		return ""
	}
}

type dataItem struct {
	iType itemType
	id    interface{}
	data  interface{}
	attrs interface{}
}

func (ds *dataService) getItem(item *dataItem) (err error) {
	// if !ds.enableCache {
	// 	err = ds.fetchItem(item)
	// 	return
	// }

	ok := ds.getItemCache(item)
	if ok {
		if uInfo := ds.getItemUpdateInfo(item); uInfo.isExpired() {
			go ds.refreshItemCache(item, false)
		}

		return nil
	}

	errRefresh := ds.refreshItemCache(item, true)
	if errRefresh != nil {
		return errRefresh
	}

	return
}

func (ds *dataService) fetchItem(item *dataItem) (err error) {
	logger.Debug("Start fetching data")

	uInfo := cacheInfoInst.getUpdateInfo(item)

	if ds.server != nil {
		err = ds.server.getItem(item)
		if isNeedToUpdateCacheControl(err) {
			if err != nil {
				ds.getItemCache(item)
				// err = nil
			}
			return nil
		}

		logger.Error("Fail to get from server: " + err.Error())
	}

	if ds.bundle != nil {
		err = ds.bundle.getItem(item)
		if err == nil {
			uInfo.setAge(100) // Todo: for local bundles
			return
		}
	}

	return err
}

func (ds *dataService) updateCacheControl(item *dataItem, uInfo *singleCacheInfo) {
	headers := item.attrs.(http.Header)
	cc := headers.Get(httpHeaderCacheControl)
	age, parseErr := strconv.ParseInt(cc, 10, 64)
	if parseErr != nil {
		logger.Error("Wrong cache control: " + cc)
	} else {
		uInfo.setAge(age)
	}
	uInfo.setETag(headers.Get(httpHeaderETag))

}

func isNeedToUpdateCacheControl(err error) bool {
	if err != nil {
		myErr, ok := err.(*sgtnError)
		if !ok || myErr.etype != httpError || myErr.code != httpCode304 {
			return false
		}
	}

	return true
}
func (ds *dataService) getItemCache(item *dataItem) (ok bool) {
	item.data, ok = ds.cache.Get(item.id)
	return ok
}

func (ds *dataService) setItemCache(item *dataItem) {
	ds.cache.Set(item.id, item.data)
}
func (ds *dataService) refreshItemCache(item *dataItem, wait bool) error {
	uInfo := cacheInfoInst.getUpdateInfo(item)

	if uInfo.setUpdating() {
		defer uInfo.setUpdated()
		uInfo.setTime(time.Now().Unix())

		logger.Debug("Start refreshing cache")
		err := ds.fetchItem(item)
		if err != nil {
			return err
		}

		ds.setItemCache(item)
		return nil
	} else if wait {
		uInfo.waitUpdate()
	}

	found := ds.getItemCache(item)
	if !found {
		return errors.New("Fail to refresh cache")
	}

	return nil
}
func (ds *dataService) getItemUpdateInfo(item *dataItem) *singleCacheInfo {
	return cacheInfoInst.getUpdateInfo(item)
}

func (ds *dataService) registerCache(c Cache) {
	// if ds.enableCache {
	ds.cache = c
	// }
}

//!-dataService
