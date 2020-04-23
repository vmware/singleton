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
	inst   *instance
	logger Logger
)

// instance Singleton instance
type instance struct {
	cfg           Config
	trans         *defaultTrans
	components    []struct{ Name, Version string }
	initializOnce sync.Once
}

func init() {
	SetLogger(newLogger())
	httpclient = &http.Client{Timeout: time.Second * servertimeout}
}

//Initialize initialize the client
func Initialize(cfg *Config) {
	if err := checkConfig(cfg); err != nil {
		panic(err)
	}

	inst = &instance{}
	inst.cfg = *cfg
	inst.initializOnce.Do(inst.doInitialize)
}

func (i *instance) doInitialize() {
	logger.Debug("Initializing Singleton client.")

	dService := new(dataService)
	if len(i.cfg.ServerURL) != 0 {
		var err error
		dService.server, err = newServer(i.cfg.ServerURL)
		if err != nil {
			panic(err)
		}
	}
	if strings.TrimSpace(i.cfg.LocalBundles) != "" {
		dService.bundle = &bundleDAO{i.cfg.LocalBundles}
	}

	i.trans = &defaultTrans{dService, i.cfg.DefaultLocale}

	initCacheInfoMap()
	if cache == nil {
		RegisterCache(newCache())
	}
}

func checkConfig(cfg *Config) error {
	switch {
	case cfg.LocalBundles == "" && cfg.ServerURL == "":
		return errors.New("Neither Server URL nor Local Bundles is provided")
	case cfg.DefaultLocale == "":
		return errors.New("default_locale isn't provided")
	default:
		return nil
	}
}

// GetTranslation Get translation instance
func GetTranslation() Translation {
	if inst == nil {
		panic(errors.New(uninitialzed))
	}

	return inst.trans
}

// SetHTTPHeaders Set customized HTTP headers
func SetHTTPHeaders(h map[string]string) {
	if inst == nil {
		panic(errors.New(uninitialzed))
	}

	server := inst.trans.ds.server
	if server != nil {
		server.setHTTPHeaders(h)
	}
}

// RegisterCache Register cache implementation. There is a default implementation
func RegisterCache(c Cache) {
	if cache != nil {
		return
	}

	cache = c
}

// SetLogger Set a global logger. There is a default console logger
func SetLogger(l Logger) {
	logger = l
}
