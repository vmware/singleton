/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"net/http"
	"sync"
	"time"

	"github.com/emirpasic/gods/sets/linkedhashset"
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
	logger.Info(fmt.Sprintf("Initializing Singleton client with configuration: %#+v", i.cfg))

	if cache == nil {
		RegisterCache(newCache())
	}

	inst.trans = createTranslation(i.cfg)
}

func createTranslation(cfg Config) Translation {
	var transOrigins messageOriginList
	var sourceOrigins messageOriginList

	if len(mapSource.releases) > 0 {
		sourceOrigins = append(sourceOrigins, sourceAsOrigin{&mapSource})
	}
	if cfg.LocalSourceBundle != "" {
		sourceOrigins = append(sourceOrigins, sourceAsOrigin{newLocalSource(cfg.LocalSourceBundle)})
	}
	if cfg.ServerURL != "" {
		server, err := newServer(cfg.ServerURL)
		if err != nil {
			panic(err)
		}
		inst.server = server
		transOrigins = append(transOrigins, server)
		sourceOrigins = append(sourceOrigins, &sourceInTranslation{server})
	}
	if cfg.LocalBundles != "" {
		bundleTranslation := &bundleDAO{cfg.LocalBundles}
		transOrigins = append(transOrigins, bundleTranslation)
		sourceOrigins = append(sourceOrigins, &sourceInTranslation{bundleTranslation})
	}

	var origin messageOrigin

	if len(transOrigins) > 0 && len(sourceOrigins) > 0 {
		origin = &sourceComparison{source: sourceOrigins, messageOrigin: transOrigins}
	} else if len(sourceOrigins) > 0 {
		origin = &sourceComparison{source: sourceOrigins}
	} else if len(transOrigins) > 0 {
		origin = transOrigins
	}

	origin = &saveToCache{messageOrigin: origin}
	origin = &singleLoader{messageOrigin: origin}
	origin = &cacheService{origin}

	transImpl := transInst{origin}

	localeSet := linkedhashset.New(cfg.DefaultLocale /*, cfg.GetSourceLocale()*/)
	fallbackLocales := []string{}
	for _, locale := range localeSet.Values() {
		if locale != "" {
			fallbackLocales = append(fallbackLocales, locale.(string))
		}
	}
	return newTransMgr(&transImpl, fallbackLocales)
}

func checkConfig(cfg *Config) error {
	switch {
	case cfg.LocalBundles == "" && cfg.ServerURL == "" && len(mapSource.releases) == 0 && cfg.LocalSourceBundle == "":
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

// RegisterSource is the way sending source strings to Singleton client programmatically
func RegisterSource(name, version string, sources []ComponentMsgs) {
	logger.Info(fmt.Sprintf("Register source to %s/%s", name, version))

	id := releaseID{name, version}
	release, ok := mapSource.releases[id]
	if !ok {
		release = map[string]ComponentMsgs{}
		mapSource.releases[id] = release
	}
	for _, comp := range sources {
		release[comp.Component()] = comp
	}
}
