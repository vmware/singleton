/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"net/http"
	"strings"
	"sync"
	"time"
)

var (
	// instMap sync.Map
	inst   = Instance{}
	logger Logger
)

// Instance Singleton instance
type Instance struct {
	cfg           Config
	dService      *dataService
	trans         *defaultTrans
	components    []componentID
	initializOnce sync.Once
}

func init() {
	SetLogger(newLogger())
	httpclient = &http.Client{Timeout: time.Second * servertimeout}
}

// GetInst Get the Singleton instance
func GetInst() *Instance {
	return &inst
}

//Initialize initialize the instance
func (i *Instance) Initialize(cfg *Config) {
	i.cfg = *cfg
	i.initializOnce.Do(i.doInitialize)
}

func (i *Instance) doInitialize() {
	logger.Debug("Initializing Singleton instance.")

	dService := new(dataService)
	// if i.cfg.EnableCache {
	// dService.enableCache = i.cfg.EnableCache
	cacheInfoInst = newCacheInfo()
	// }
	if len(i.cfg.OnlineServiceURL) != 0 {
		var err error
		dService.server, err = newServer(i.cfg.OnlineServiceURL)
		if err != nil {
			logger.Error(err.Error())
		}
	}
	if strings.TrimSpace(i.cfg.OfflineResourcesBaseURL) != "" {
		dService.bundle = &bundleDAO{i.cfg.OfflineResourcesBaseURL}
	}
	i.dService = dService

	i.trans = &defaultTrans{i.dService, i.cfg.DefaultLocale}
	i.RegisterCache(newCache())
}

// GetConfig Get the config of Singleton instance
// func (i *Instance) GetConfig() Config {
// 	return i.cfg
// }

// GetTranslation Get translation instance
func (i *Instance) GetTranslation() Translation {
	return i.trans
}

// SetHTTPHeaders Add customized HTTP headers
func (i *Instance) SetHTTPHeaders(h map[string]string) {
	i.trans.server.setHTTPHeaders(h)
}

// RegisterCache Register cache implementation. There is a default implementation
func (i *Instance) RegisterCache(cs Cache) {
	i.trans.registerCache(cs)
}

// SetLogger Set a global logger. There is a default console logger
func SetLogger(l Logger) {
	logger = l
}
