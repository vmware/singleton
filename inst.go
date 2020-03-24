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
	trans         *defaultTrans
	components    []translationID
	initializOnce sync.Once
}

func init() {
	SetLogger(newLogger())
	httpclient = &http.Client{Timeout: time.Second * servertimeout}
}

// GetInst Get the Singleton instance
// func GetInst() *Instance {
// 	return &inst
// }

//GetInst initialize the instance
func GetInst(cfg *Config) *Instance {
	inst.cfg = *cfg
	inst.initializOnce.Do(inst.doInitialize)
	return &inst
}

func (i *Instance) doInitialize() {
	logger.Debug("Initializing Singleton instance.")

	dService := new(dataService)
	if len(i.cfg.OnlineServiceURL) != 0 {
		var err error
		dService.server, err = newServer(i.cfg.OnlineServiceURL)
		if err != nil {
			panic(err)
		}
	}
	if strings.TrimSpace(i.cfg.OfflineResourcesBaseURL) != "" {
		dService.bundle = &bundleDAO{i.cfg.OfflineResourcesBaseURL}
	}

	i.trans = &defaultTrans{dService, i.cfg.DefaultLocale}

	// if i.cfg.EnableCache {
	// dService.enableCache = i.cfg.EnableCache
	initCacheInfoMap()
	i.RegisterCache(newCache())
	// }
}

// GetConfig Get the config of Singleton instance
// func (i *Instance) GetConfig() Config {
// 	return i.cfg
// }

// GetTranslation Get translation instance
func (i *Instance) GetTranslation() Translation {
	return i.trans
}

// SetHTTPHeaders Set customized HTTP headers
func (i *Instance) SetHTTPHeaders(h map[string]string) {
	i.trans.ds.server.setHTTPHeaders(h)
}

// RegisterCache Register cache implementation. There is a default implementation
func (i *Instance) RegisterCache(c Cache) {
	i.trans.ds.cache = c
}

// SetLogger Set a global logger. There is a default console logger
func SetLogger(l Logger) {
	logger = l
}
