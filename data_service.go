/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"errors"
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
			go ds.fetch(item, false)
		}

		return nil
	}

	return ds.fetch(item, true)
}

func (ds *dataService) fetch(item *dataItem, wait bool) error {
	var err error
	info := getCacheInfo(item)

	if info.setUpdating() {
		defer info.setUpdated()

		info.setTime(time.Now().Unix())

		logger.Debug("Start fetching")

		if ds.server != nil {
			err = ds.server.get(item)
			if isFetchSucess(err) {
				updateCacheControl(item, info)
				if err == nil {
					ds.setCache(item)
				} else {
					ds.getCache(item)
				}
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
		ok := ds.getCache(item)
		if !ok {
			return errors.New("Fail to fetch")
		}
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

func isFetchSucess(err error) bool {
	if err != nil {
		myErr, ok := err.(*serverError)
		if !ok || myErr.code != httpCode304 {
			return false
		}
	}

	return true
}

func updateCacheControl(item *dataItem, info *itemCacheInfo) {
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

//!-dataService
