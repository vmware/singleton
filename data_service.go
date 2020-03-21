/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
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

func (ds *dataService) GetLocaleList(name, version string) (data []string, err error) {
	item := &dataItem{itemLocales, translationID{name, version}, nil, nil}

	err = ds.getItem(item)
	data, _ = item.data.([]string)
	return
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
	switch item.iType {
	case itemComponent:
		id := item.id.(componentID)
		item.data, ok = ds.cache.GetComponentMessages(id.Name, id.Version, id.Locale, id.Component)
		return
	case itemLocales:
		id := item.id.(translationID)
		locales := ds.cache.GetLocales(id.Name, id.Version)
		item.data = locales
		return len(locales) != 0
	case itemComponents:
		id := item.id.(translationID)
		components := ds.cache.GetComponents(id.Name, id.Version)
		item.data = components
		return len(components) != 0
	default:
		return false
	}
}
func (ds *dataService) setItemCache(item *dataItem) {
	switch item.iType {
	case itemComponent:
		id := item.id.(componentID)
		compData, _ := item.data.(ComponentMsgs)
		ds.cache.SetComponentMessages(id.Name, id.Version, id.Locale, id.Component, compData)
	case itemLocales:
		id := item.id.(translationID)
		ds.cache.SetLocales(id.Name, id.Version, item.data.([]string))
	case itemComponents:
		id := item.id.(translationID)
		ds.cache.SetComponents(id.Name, id.Version, item.data.([]string))
	default:
		return
	}
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

func (ds *dataService) GetComponentList(name, version string) (data []string, err error) {
	item := &dataItem{itemComponents, translationID{name, version}, nil, nil}

	err = ds.getItem(item)
	data, _ = item.data.([]string)

	return
}

func (ds *dataService) GetStringMessage(name, version, locale, component, key string) (string, error) {
	var errMsg string
	compData, err := ds.GetComponentMessages(name, version, locale, component)
	if err != nil {
		return key, err
	}

	message, ok := compData.Get(key)
	if !ok {
		errMsg = "No key in locale: " + locale + ", component: " + component
		return key, errors.New(errMsg)
	}

	return message, nil
}

func (ds *dataService) GetComponentMessages(name, version, locale, component string) (data ComponentMsgs, err error) {
	item := &dataItem{itemComponent, componentID{name, version, locale, component}, nil, nil}

	err = ds.getItem(item)
	data, _ = item.data.(ComponentMsgs)
	fmt.Printf("data to return: \n%#v\n", data)
	return
}

func (ds *dataService) fetchLocaleList(name, version string) (data []string, err error) {
	logger.Debug("Start fetching locale list")

	item := &dataItem{itemLocales, translationID{name, version}, nil, nil}
	if ds.server != nil {
		err = ds.server.getItem(item)
		if err == nil {
			return item.data.([]string), nil
		}
		logger.Error("Fail to get from server: " + err.Error())
	}

	if ds.bundle != nil {
		err = ds.bundle.getItem(item)
		if err == nil {
			return item.data.([]string), nil
		}
	}

	return
}
func (ds *dataService) fetchComponentList(name, version string) (data []string, err error) {
	logger.Debug("Start fetching component list")

	item := &dataItem{itemComponents, translationID{name, version}, nil, nil}
	if ds.server != nil {
		err = ds.server.getItem(item)
		if err == nil {
			return item.data.([]string), nil
		}
		logger.Error("Fail to get from server: " + err.Error())
	}

	if ds.bundle != nil {
		err = ds.bundle.getItem(item)
		if err == nil {
			return item.data.([]string), nil
		}
	}

	return
}
func (ds *dataService) fetchCompData(name, version, locale, component string) (data ComponentMsgs, err error) {
	logger.Debug("Start fetching data for locale: " + locale + ", component: " + component)

	item := &dataItem{itemComponent, componentID{name, version, locale, component}, nil, nil}
	if ds.server != nil {
		err = ds.server.getItem(item)
		if err == nil {
			return item.data.(ComponentMsgs), nil
		}
		logger.Error("Fail to get from server: " + err.Error())
	}

	if ds.bundle != nil {
		err = ds.bundle.getItem(item)
		if err == nil {
			return item.data.(ComponentMsgs), nil
		}
	}

	return
}

func (ds *dataService) refreshLocaleList(name, version string) (data []string, err error) {
	item := dataItem{itemLocales, translationID{name, version}, nil, nil}

	err = ds.refreshItemCache(&item, true)
	data, _ = item.data.([]string)

	return
}
func (ds *dataService) refreshComponentList(name, version string) (data []string, err error) {
	item := dataItem{itemComponents, translationID{name, version}, nil, nil}

	err = ds.refreshItemCache(&item, true)
	data, _ = item.data.([]string)

	return
}
func (ds *dataService) refreshCompCache(name, version, locale, component string, wait bool) (data ComponentMsgs, err error) {
	item := dataItem{itemComponent, componentID{name, version, locale, component}, nil, nil}

	err = ds.refreshItemCache(&item, true)
	data, _ = item.data.(ComponentMsgs)

	return
}

func (ds *dataService) registerCache(cs Cache) {
	// if ds.enableCache {
	ds.cache = cs
	// }
}

//!-dataService
