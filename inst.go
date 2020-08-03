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
	cfg            Config
	trans          Translation
	server         *serverDAO
	bundle         *bundleDAO
	initializeOnce sync.Once
}

func init() {
	SetLogger(newLogger())
	httpclient = &http.Client{Timeout: time.Second * servertimeout}
}

// Initialize initialize the client
func Initialize(cfg *Config) {
	if err := checkConfig(cfg); err != nil {
		panic(err)
	}

	inst = &instance{}
	inst.cfg = *cfg
	inst.initializeOnce.Do(inst.doInitialize)
}

func (i *instance) doInitialize() {
	logger.Debug("Initializing Singleton client.")

	var originList messageOriginList
	if len(i.cfg.ServerURL) != 0 {
		server, err := newServer(i.cfg.ServerURL)
		if err != nil {
			panic(err)
		}
		i.server = server
		originList = append(originList, server)
	}
	if strings.TrimSpace(i.cfg.LocalBundles) != "" {
		i.bundle = &bundleDAO{i.cfg.LocalBundles}
		originList = append(originList, i.bundle)
	}
	cacheService := newCacheService(originList)

	transImpl := transInst{cacheService}
	var fallbackChains []string
	fallbackChains = append(fallbackChains, i.cfg.DefaultLocale)
	i.trans = newTransMgr(&transImpl, fallbackChains)

	initCacheInfoMap()
	if cache == nil {
		RegisterCache(newCache())
	}
}

func checkConfig(cfg *Config) error {
	switch {
	case cfg.LocalBundles == "" && cfg.ServerURL == "":
		return errors.New(originNotProvided)
	case cfg.DefaultLocale == "":
		return errors.New(defaultLocaleNotProvided)
	default:
		return nil
	}
}

// GetTranslation Get translation instance
func GetTranslation() Translation {
	if inst == nil {
		panic(errors.New(uninitialized))
	}

	return inst.trans
}

// SetHTTPHeaders Set customized HTTP headers
func SetHTTPHeaders(h map[string]string) error {
	if inst == nil {
		return errors.New(uninitialized)
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
