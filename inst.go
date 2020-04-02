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

	"github.com/pkg/errors"
)

var (
	inst   = Instance{}
	logger Logger
)

// Instance Singleton instance
type Instance struct {
	cfg           Config
	trans         *defaultTrans
	components    []struct{ Name, Version string }
	initializOnce sync.Once
}

func init() {
	SetLogger(newLogger())
	httpclient = &http.Client{Timeout: time.Second * servertimeout}
}

//GetInst initialize the instance
func GetInst(cfg *Config) *Instance {
	if err := checkConfig(cfg); err != nil {
		panic(err)
	}

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

	initCacheInfoMap()
	i.RegisterCache(newCache())

}

func checkConfig(cfg *Config) error {
	switch {
	case cfg.OfflineResourcesBaseURL == "" && cfg.OnlineServiceURL == "":
		return errors.New("Both online_service_url and offline_resources_base_url are empty")
	}

	return nil
}

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
