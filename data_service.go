/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"strings"
	"time"

	"errors"
)

//!+dataService
type dataService struct {
	cfg *Config

	cache         Cache
	cacheSyncInfo *cacheSyncInfo

	bundle *bundleDAO
	server *serverDAO
}

func newDataService(cfg *Config) (*dataService, error) {
	var err error
	service := new(dataService)
	service.cfg = cfg

	if cfg.EnableCache {
		service.cacheSyncInfo = newCacheSyncInfo(cfg)
	}

	if len(cfg.SingletonServer) != 0 {
		service.server, err = newServer(cfg)
		if err != nil {
			logger.Error("Fail to create server. " + err.Error())
		}
	}

	if strings.TrimSpace(cfg.LocalBundles) != "" {
		service.bundle = &bundleDAO{cfg}
	}

	return service, nil
}

func (ds *dataService) GetLocaleList() ([]string, error) {
	if !ds.cfg.EnableCache {
		return ds.fetchLocaleList()
	}

	localeList := ds.cache.GetLocales()
	if len(localeList) != 0 {
		if ds.cfg.CacheExpiredTime > 0 {
			if uInfo := ds.cacheSyncInfo.getLocalesUpdateInfo(); uInfo.isExpired(ds.cfg.CacheExpiredTime) {
				go ds.refreshLocaleList()
			}
		}

		return localeList, nil
	}

	if errRefresh := ds.refreshLocaleList(); errRefresh != nil {
		return nil, errRefresh
	}

	localeList = ds.cache.GetLocales()

	return localeList, nil
}

func (ds *dataService) GetComponentList() ([]string, error) {
	if !ds.cfg.EnableCache {
		return ds.fetchComponentList()
	}

	compList := ds.cache.GetComponents()
	if len(compList) != 0 {
		if ds.cfg.CacheExpiredTime > 0 {
			if uInfo := ds.cacheSyncInfo.getComponentsUpdateInfo(); uInfo.isExpired(ds.cfg.CacheExpiredTime) {
				go ds.refreshComponentList()
			}
		}

		return compList, nil
	}

	if errRefresh := ds.refreshComponentList(); errRefresh != nil {
		return nil, errRefresh
	}

	compList = ds.cache.GetComponents()

	return compList, nil
}

func (ds *dataService) getStringMessage(locale, component, key string) (string, error) {
	var errMsg string
	compData, err := ds.getComponentMessages(locale, component)
	if err != nil {
		if strings.Compare(strings.ToLower(locale), strings.ToLower(ds.cfg.DefaultLocale)) != 0 {
			logger.Error("Fallback to default locale because of error: " + err.Error())
			locale = ds.cfg.DefaultLocale
			compData, err = ds.getComponentMessages(locale, component)
		}
	}
	if err != nil {
		return key, err
	}

	if message, ok := compData.Get(key); ok {
		return message, nil
	}
	errMsg = "No key in locale: " + locale + ", component: " + component
	return key, errors.New(errMsg)
}

func (ds *dataService) getComponentMessages(locale, component string) (ComponentMsgs, error) {
	if !ds.cfg.EnableCache {
		return ds.fetchCompData(locale, component)
	}

	compData, ok := ds.cache.GetComponentMessages(locale, component)
	if ok {
		if ds.cfg.CacheExpiredTime > 0 {
			if uInfo := ds.cacheSyncInfo.getCompUpdateInfo(locale, component); uInfo.isExpired(ds.cfg.CacheExpiredTime) {
				go ds.refreshCompCache(locale, component, false)
			}
		}
		return compData, nil
	}

	compData, errRefresh := ds.refreshCompCache(locale, component, true)
	if errRefresh != nil {
		return nil, errRefresh
	}
	return compData, nil
}

func (ds *dataService) getComponentsMessages(locale string, components []string) (map[string]ComponentMsgs, error) {
	var err error
	if len(components) == 0 {
		if components, err = ds.GetComponentList(); err != nil {
			return nil, err
		}
	}
	returnMsgs := make(map[string]ComponentMsgs, len(components))
	for _, comp := range components {
		msgs, err := ds.getComponentMessages(locale, comp)
		if err != nil {
			return nil, err
		}
		returnMsgs[comp] = msgs
	}

	return returnMsgs, nil
}

func (ds *dataService) fetchLocaleList() (data []string, err error) {
	logger.Debug("Start fetching locale list")

	if ds.server != nil {
		data, err = ds.server.getLocales()
		if err == nil {
			return data, nil
		}
	}

	if ds.bundle != nil {
		data, err = ds.bundle.getLocales()
		if err == nil {
			return data, nil
		}
	}

	return data, err
}
func (ds *dataService) fetchComponentList() (data []string, err error) {
	logger.Debug("Start fetching component list")

	if ds.server != nil {
		data, err = ds.server.getComponents()
		if err == nil {
			return data, nil
		}
	}

	if ds.bundle != nil {
		data, err = ds.bundle.getComponents()
		if err == nil {
			return data, nil
		}
	}

	return data, err
}
func (ds *dataService) fetchCompData(locale, component string) (data ComponentMsgs, err error) {
	logger.Debug("Start fetching data for locale: " + locale + ", component: " + component)

	if ds.server != nil {
		data, err = ds.server.getComponentMessages(locale, component)
		if err == nil {
			return data, nil
		}
	}

	if ds.bundle != nil {
		data, err = ds.bundle.getComponentMessages(locale, component)
		if err == nil {
			return data, nil
		}
	}

	return data, err
}

func (ds *dataService) refreshLocaleList() error {
	uInfo := ds.cacheSyncInfo.getLocalesUpdateInfo()

	logger.Debug("Start refreshing cache")
	uInfo.setTime(time.Now().Unix())

	compData, err := ds.fetchLocaleList()
	if err != nil {
		return err
	}

	ds.cache.SetLocales(compData)
	return nil
}
func (ds *dataService) refreshComponentList() error {
	uInfo := ds.cacheSyncInfo.getComponentsUpdateInfo()

	logger.Debug("Start refreshing cache")
	uInfo.setTime(time.Now().Unix())

	compData, err := ds.fetchComponentList()
	if err != nil {
		return err
	}

	ds.cache.SetComponents(compData)
	return nil
}
func (ds *dataService) refreshCompCache(locale, component string, wait bool) (ComponentMsgs, error) {
	uInfo := ds.cacheSyncInfo.getCompUpdateInfo(locale, component)

	if uInfo.setUpdating() {
		defer uInfo.setUpdated()
		uInfo.setTime(time.Now().Unix())

		logger.Debug("Start refreshing cache for locale: " + locale + ", component: " + component)
		compData, err := ds.fetchCompData(locale, component)
		if err != nil {
			return nil, err
		}

		ds.cache.SetComponentMessages(locale, component, compData)
		return compData, nil
	} else if wait {
		uInfo.waitUpdate()
	}

	compData, found := ds.cache.GetComponentMessages(locale, component)
	if !found {
		return nil, errors.New("Fail to refresh cache of component messages")
	}
	return compData, nil
}

func (ds *dataService) initializeCache() error {
	logger.Info("Start initializing cache of " + ds.cfg.Name)

	ch := make(chan error, 2)
	localesCh := make(chan []string, 1)
	componentsCh := make(chan []string, 1)
	go func() {
		err := ds.refreshLocaleList()
		if nil == err {
			localesCh <- ds.cache.GetLocales()
		}
		ch <- err
	}()
	go func() {
		err := ds.refreshComponentList()
		if nil == err {
			componentsCh <- ds.cache.GetComponents()
		}
		ch <- err
	}()

	for i := 0; i < cap(ch); i++ {
		err := <-ch
		if err != nil {
			return err
		}
	}

	locales := <-localesCh
	components := <-componentsCh
	close(localesCh)
	close(componentsCh)
	total := len(locales) * len(components)

	type compresult struct {
		locale, component string
		err               error
	}
	results := make(chan compresult, total)
	for _, locale := range locales {
		for _, component := range components {
			go func(locale, component string) {
				_, errComp := ds.refreshCompCache(locale, component, true)
				results <- compresult{locale, component, errComp}
			}(locale, component)
		}
	}

	var errorReturn error
	for i := 0; i < total; i++ {
		r := <-results
		if r.err != nil {
			logger.Error(fmt.Sprintf("Error when initializing cache of locale '%s', component '%s'. Error is: %s", r.locale, r.component, r.err.Error()))
			errorReturn = r.err
		}
	}
	close(results)

	return errorReturn
}

func (ds *dataService) registerCache(cs Cache) {
	if ds.cfg.EnableCache {
		ds.cache = cs
	}
}

//!-dataService
