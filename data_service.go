/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"time"

	"errors"
)

//!+dataService
type dataService struct {
	enableCache bool

	cache         Cache
	cacheSyncInfo *cacheSyncInfo

	bundle *bundleDAO
	server *serverDAO
}

func (ds *dataService) GetLocaleList(name, version string) ([]string, error) {
	if !ds.enableCache {
		return ds.fetchLocaleList(name, version)
	}

	localeList := ds.cache.GetLocales(name, version)
	if len(localeList) != 0 {
		if uInfo := ds.cacheSyncInfo.getLocalesUpdateInfo(name, version); uInfo.isExpired() {
			go ds.refreshLocaleList(name, version)
		}

		return localeList, nil
	}

	if errRefresh := ds.refreshLocaleList(name, version); errRefresh != nil {
		return nil, errRefresh
	}

	localeList = ds.cache.GetLocales(name, version)

	return localeList, nil
}

func (ds *dataService) GetComponentList(name, version string) ([]string, error) {
	if !ds.enableCache {
		return ds.fetchComponentList(name, version)
	}

	compList := ds.cache.GetComponents(name, version)
	if len(compList) != 0 {
		if uInfo := ds.cacheSyncInfo.getComponentsUpdateInfo(name, version); uInfo.isExpired() {
			go ds.refreshComponentList(name, version)
		}

		return compList, nil
	}

	if errRefresh := ds.refreshComponentList(name, version); errRefresh != nil {
		return nil, errRefresh
	}

	compList = ds.cache.GetComponents(name, version)

	return compList, nil
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

func (ds *dataService) GetComponentMessages(name, version, locale, component string) (ComponentMsgs, error) {
	if !ds.enableCache {
		return ds.fetchCompData(name, version, locale, component)
	}

	compData, ok := ds.cache.GetComponentMessages(name, version, locale, component)
	if ok {
		if uInfo := ds.cacheSyncInfo.getCompUpdateInfo(name, version, locale, component); uInfo.isExpired() {
			go ds.refreshCompCache(name, version, locale, component, false)
		}
		return compData, nil
	}

	compData, errRefresh := ds.refreshCompCache(name, version, locale, component, true)
	if errRefresh != nil {
		return nil, errRefresh
	}
	return compData, nil
}

func (ds *dataService) fetchLocaleList(name, version string) (data []string, err error) {
	logger.Debug("Start fetching locale list")

	if ds.server != nil {
		data, err = ds.server.getLocales(name, version)
		if err == nil {
			return data, nil
		}
		logger.Error("Fail to get from server: " + err.Error())
	}

	if ds.bundle != nil {
		data, err = ds.bundle.getLocales(name, version)
		if err == nil {
			return data, nil
		}
	}

	return data, err
}
func (ds *dataService) fetchComponentList(name, version string) (data []string, err error) {
	logger.Debug("Start fetching component list")

	if ds.server != nil {
		data, err = ds.server.getComponents(name, version)
		if err == nil {
			return data, nil
		}
		logger.Error("Fail to get from server: " + err.Error())
	}

	if ds.bundle != nil {
		data, err = ds.bundle.getComponents(name, version)
		if err == nil {
			return data, nil
		}
	}

	return data, err
}
func (ds *dataService) fetchCompData(name, version, locale, component string) (data ComponentMsgs, err error) {
	logger.Debug("Start fetching data for locale: " + locale + ", component: " + component)

	if ds.server != nil {
		data, err = ds.server.getComponentMessages(name, version, locale, component)
		if err == nil {
			return data, nil
		}
		logger.Error("Fail to get from server: " + err.Error())
	}

	if ds.bundle != nil {
		data, err = ds.bundle.getComponentMessages(name, version, locale, component)
		if err == nil {
			return data, nil
		}
	}

	return data, err
}

func (ds *dataService) refreshLocaleList(name, version string) error {
	uInfo := ds.cacheSyncInfo.getLocalesUpdateInfo(name, version)

	logger.Debug("Start refreshing cache")
	uInfo.setTime(time.Now().Unix())

	compData, err := ds.fetchLocaleList(name, version)
	if err != nil {
		return err
	}

	ds.cache.SetLocales(name, version, compData)
	return nil
}
func (ds *dataService) refreshComponentList(name, version string) error {
	uInfo := ds.cacheSyncInfo.getComponentsUpdateInfo(name, version)

	logger.Debug("Start refreshing cache")
	uInfo.setTime(time.Now().Unix())

	compData, err := ds.fetchComponentList(name, version)
	if err != nil {
		return err
	}

	ds.cache.SetComponents(name, version, compData)
	return nil
}
func (ds *dataService) refreshCompCache(name, version, locale, component string, wait bool) (ComponentMsgs, error) {
	uInfo := ds.cacheSyncInfo.getCompUpdateInfo(name, version, locale, component)

	if uInfo.setUpdating() {
		defer uInfo.setUpdated()
		uInfo.setTime(time.Now().Unix())

		logger.Debug("Start refreshing cache for locale: " + locale + ", component: " + component)
		compData, err := ds.fetchCompData(name, version, locale, component)
		if err != nil {
			return nil, err
		}

		ds.cache.SetComponentMessages(name, version, locale, component, compData)
		return compData, nil
	} else if wait {
		uInfo.waitUpdate()
	}

	compData, found := ds.cache.GetComponentMessages(name, version, locale, component)
	if !found {
		return nil, errors.New("Fail to refresh cache of component messages")
	}
	return compData, nil
}

func (ds *dataService) registerCache(cs Cache) {
	if ds.enableCache {
		ds.cache = cs
	}
}

//!-dataService
