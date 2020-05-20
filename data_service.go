/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"net/http"
	"regexp"
	"strconv"
	"time"

	"github.com/pkg/errors"
)

var cacheControlRE = regexp.MustCompile(`(?i)\bmax-age\b\s*=\s*\b(\d+)\b`)

type messageOrigin interface {
	Get(item *dataItem) error
}

//!+dataService
type (
	dataService struct {
		originChain []messageOrigin
	}
)

func (ds *dataService) get(item *dataItem) (err error) {
	data, ok := cache.Get(item.id)
	info := getCacheInfo(item)
	item.attrs = info
	if ok {
		item.data = data
		if info.isExpired() {
			go ds.fetch(item, false)
		}

		return nil
	}

	return ds.fetch(item, true)
}

func (ds *dataService) fetch(item *dataItem, wait bool) error {
	var err error
	info := item.attrs.(*itemCacheInfo)

	if info.setUpdating() {
		defer info.setUpdated()
		logger.Debug(fmt.Sprintf("Start fetching ID: %+v", item.id))

		info.setTime(time.Now().Unix())

		for _, o := range ds.originChain {
			if err = o.Get(item); err == nil {
				break
			}
		}
		return err

	} else if wait {
		info.waitUpdate()
		var ok bool
		item.data, ok = cache.Get(item.id)
		if !ok {
			return errors.New(fmt.Sprintf("Fail to fetch ID: %+v", item.id))
		}
	}

	return nil
}

//!-dataService

//!+serverService
type serverService struct {
	server *serverDAO
}

func (s *serverService) Get(item *dataItem) (err error) {
	info := item.attrs.(*itemCacheInfo)
	err = s.server.get(item)
	if isFetchSucess(err) {
		updateCacheControl(item, info)
		if err == nil {
			cache.Set(item.id, item.data)
		} else {
			item.data, _ = cache.Get(item.id)
		}
		return nil
	}

	type stackTracer interface {
		StackTrace() errors.StackTrace
	}
	if e, ok := err.(stackTracer); ok {
		logger.Error(fmt.Sprintf(serverFail+": %#v", e))
	} else {
		logger.Error(fmt.Sprintf(serverFail+": %s", err.Error()))
	}

	return err
}
func isFetchSucess(err error) bool {
	if err != nil {
		myErr, ok := err.(*serverError)
		if !ok || myErr.code != http.StatusNotModified {
			return false
		}
	}

	return true
}

func updateCacheControl(item *dataItem, info *itemCacheInfo) {
	headers := item.attrs.(http.Header)

	info.setETag(headers.Get(httpHeaderETag))

	cc := headers.Get(httpHeaderCacheControl)
	results := cacheControlRE.FindStringSubmatch(cc)
	if len(results) == 2 {
		age, parseErr := strconv.ParseInt(results[1], 10, 64)
		if parseErr == nil {
			info.setAge(age)
			return
		}
	}

	info.setAge(cacheDefaultExpires)
	logger.Error("Wrong cache control: " + cc)
}

//!-serverService

//!+bundleService
type bundleService struct {
	bundle *bundleDAO
}

func (s *bundleService) Get(item *dataItem) (err error) {
	info := item.attrs.(*itemCacheInfo)

	err = s.bundle.Get(item)
	if err == nil {
		var age int64 = cacheNeverExpires
		if inst.server != nil {
			age = cacheDefaultExpires
		}
		info.setAge(age)

		cache.Set(item.id, item.data)
		return nil
	}

	//	logger.Error(fmt.Sprintf("Fail to get from bundle: %s", err.Error()))

	return err
}

//!-bundleService
