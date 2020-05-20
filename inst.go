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
	server        *serverDAO
	bundle        *bundleDAO
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
		server, err := newServer(i.cfg.ServerURL)
		if err != nil {
			panic(err)
		}
		i.server = server
		dService.originChain = append(dService.originChain, &serverService{server})
	}
	if strings.TrimSpace(i.cfg.LocalBundles) != "" {
		i.bundle = &bundleDAO{i.cfg.LocalBundles}
		dService.originChain = append(dService.originChain, &bundleService{i.bundle})
	}

	fallbackChains := []string{}
	fallbackChains = append(fallbackChains, i.cfg.DefaultLocale)
	if len(i.cfg.SourceLocale) != 0 && contains(fallbackChains, i.cfg.SourceLocale) == -1 {
		fallbackChains = append(fallbackChains, i.cfg.SourceLocale)
	}
	i.trans = &defaultTrans{dService, fallbackChains}

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
func SetHTTPHeaders(h map[string]string) error {
	if inst == nil {
		return errors.New(uninitialzed)
	}

	server := inst.server
	if server != nil {
		server.setHTTPHeaders(h)
	}

	return nil
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
