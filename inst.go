/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"net/http"
	"sync"
	"time"

	"github.com/pkg/errors"
)

var (
	inst        *instance = &instance{} // TODO: change to non-pointer
	logger      Logger
	translation Translation
)

// instance Singleton instance
type instance struct {
	cfg            Config
	server         *serverDAO
	bundle         *bundleDAO
	initializeOnce sync.Once
}

// TODO: use init to initialize global vars instead of initializing when creating
// TODO: log stacktrace

func init() {
	SetLogger(newLogger())
	httpclient = &http.Client{Timeout: time.Second * servertimeout}
}

// Initialize initialize the client
func Initialize(cfg *Config) {
	if err := checkConfig(cfg); err != nil {
		panic(err)
	}

	inst.cfg = *cfg
	inst.initializeOnce.Do(inst.doInitialize)
}

func (i *instance) doInitialize() {
	logger.Info("Initializing Singleton client")

	if cache == nil {
		RegisterCache(newCache())
	}

	translation = createTranslation(i.cfg)
}

func createTranslation(cfg Config) Translation {
	// TODO: check pointer and non-pointer
	var transOrigins messageOriginList
	var sourceOrigins messageOriginList

	if len(mapSource.releases) > 0 {
		sourceOrigins = append(sourceOrigins, sourceAsOrigin{&mapSource})
	}
	if cfg.LocalSource != "" {
		sourceOrigins = append(sourceOrigins, sourceAsOrigin{newLocalSource(cfg.LocalSource)})
	}
	if cfg.ServerURL != "" {
		server, err := newServer(cfg.ServerURL)
		if err != nil {
			panic(err)
		}
		transOrigins = append(transOrigins, server)
		sourceOrigins = append(sourceOrigins, &InTranslationSource{server})
	}
	if cfg.LocalBundles != "" {
		bundleTranslation := &bundleDAO{cfg.LocalBundles}
		transOrigins = append(transOrigins, bundleTranslation)
		sourceOrigins = append(sourceOrigins, &InTranslationSource{bundleTranslation})
	}

	var origin messageOrigin

	if len(transOrigins) > 0 && len(sourceOrigins) > 0 {
		origin = &sourceComparison{source: sourceOrigins, messageOrigin: transOrigins}
	} else if len(sourceOrigins) > 0 {
		origin = sourceOrigins
	} else if len(transOrigins) > 0 {
		origin = transOrigins
	}

	origin = &saveToCache{messageOrigin: origin}
	origin = &singleLoader{messageOrigin: origin}
	origin = &cacheService{origin}

	transImpl := transInst{origin}

	fallbackLocales := uniqueStrings([]string{cfg.DefaultLocale}, []string{cfg.GetSourceLocale()})
	return newTransMgr(&transImpl, fallbackLocales)
}

func checkConfig(cfg *Config) error {
	switch {
	case cfg.LocalBundles == "" && cfg.ServerURL == "" && len(mapSource.releases) == 0 && cfg.LocalSource == "":
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

	return translation
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

func RegisterSource(name, version string, sources []ComponentMsgs) {
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
