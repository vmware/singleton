/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"net/http"
	"strconv"
	"time"
)

//!+dataService
type dataService struct {
	// enableCache bool

	cache Cache

	bundle *bundleDAO
	server *serverDAO
}

func (ds *dataService) get(item *dataItem) (err error) {
	// if !ds.enableCache {
	// 	err = ds.fetch(item)
	// 	return
	// }

	ok := ds.getCache(item)
	if ok {
		if info := ds.getCacheInfo(item); info.isExpired() {
			go ds.refreshCache(item, false)
		}

		return nil
	}

	errRefresh := ds.refreshCache(item, true)
	if errRefresh != nil {
		return errRefresh
	}

	ds.getCache(item)

	return
}

func (ds *dataService) refreshCache(item *dataItem, wait bool) error {
	var err error
	info := getCacheInfo(item)

	if info.setUpdating() {
		defer info.setUpdated()
		info.setTime(time.Now().Unix())

		logger.Debug("Start refreshing cache")

		if ds.server != nil {
			err = ds.server.get(item)
			if err == nil {
				ds.setCache(item)
			}
			if isNeedToUpdateCacheControl(err) {
				// ds.getCache(item)
				ds.updateCacheControl(item, info)
				return nil
			}

			logger.Error("Fail to get from server: " + err.Error())
		}

		if ds.bundle != nil {
			err = ds.bundle.get(item)
			if err == nil {
				info.setAge(100) // Todo: for local bundles
				ds.setCache(item)
				return nil
			}
		}

		return err

	} else if wait {
		info.waitUpdate()
	}

	return nil
}

func (ds *dataService) getCache(item *dataItem) (ok bool) {
	item.data, ok = ds.cache.Get(item.id)
	return ok
}

func (ds *dataService) setCache(item *dataItem) {
	ds.cache.Set(item.id, item.data)
}

func (ds *dataService) getCacheInfo(item *dataItem) *itemCacheInfo {
	return getCacheInfo(item)
}

func (ds *dataService) updateCacheControl(item *dataItem, info *itemCacheInfo) {
	headers := item.attrs.(http.Header)
	cc := headers.Get(httpHeaderCacheControl)
	age, parseErr := strconv.ParseInt(cc, 10, 64)
	if parseErr != nil {
		logger.Error("Wrong cache control: " + cc)
	} else {
		info.setAge(age)
	}
	info.setETag(headers.Get(httpHeaderETag))
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

//!-dataService
