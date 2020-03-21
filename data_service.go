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

func (ds *dataService) get(item *dataItem) (err error) {
	// if !ds.enableCache {
	// 	err = ds.fetch(item)
	// 	return
	// }

	ok := ds.getCache(item)
	if ok {
		if uInfo := ds.getCacheInfo(item); uInfo.isExpired() {
			go ds.refreshCache(item, false)
		}

		return nil
	}

	errRefresh := ds.refreshCache(item, true)
	if errRefresh != nil {
		return errRefresh
	}

	return
}

func (ds *dataService) refreshCache(item *dataItem, wait bool) error {
	uInfo := cacheInfosInst.get(item)

	if uInfo.setUpdating() {
		defer uInfo.setUpdated()
		uInfo.setTime(time.Now().Unix())

		logger.Debug("Start refreshing cache")
		err := ds.fetch(item)
		if err != nil {
			return err
		}

		ds.setCache(item)
		return nil
	} else if wait {
		uInfo.waitUpdate()
	}

	found := ds.getCache(item)
	if !found {
		return errors.New("Fail to refresh cache")
	}

	return nil
}

func (ds *dataService) fetch(item *dataItem) (err error) {
	logger.Debug("Start fetching data")

	uInfo := cacheInfosInst.get(item)

	if ds.server != nil {
		err = ds.server.get(item)
		if isNeedToUpdateCacheControl(err) {
			if err != nil {
				ds.getCache(item)
				// err = nil
			}
			return nil
		}

		logger.Error("Fail to get from server: " + err.Error())
	}

	if ds.bundle != nil {
		err = ds.bundle.get(item)
		if err == nil {
			uInfo.setAge(100) // Todo: for local bundles
			return
		}
	}

	return err
}

func (ds *dataService) getCache(item *dataItem) (ok bool) {
	item.data, ok = ds.cache.Get(item.id)
	return ok
}

func (ds *dataService) setCache(item *dataItem) {
	ds.cache.Set(item.id, item.data)
}

func (ds *dataService) getCacheInfo(item *dataItem) *singleCacheInfo {
	return cacheInfosInst.get(item)
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

//!-dataService
