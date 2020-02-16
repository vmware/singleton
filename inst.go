/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"net/http"
	"sync"
	"time"
)

var (
	instMap sync.Map
	logger  Logger
)

// Instance Singleton instance
type Instance struct {
	cfg           *Config
	trans         *defaultTrans
	initializOnce sync.Once
}

func init() {
	SetLogger(newLogger())
	httpclient = &http.Client{Timeout: time.Second * servertimeout}
}

// NewInst Create a new Singleton instance
func NewInst(cfg Config) (*Instance, bool) {
	actual, loaded := instMap.LoadOrStore(cfg.Name, new(Instance))
	if !loaded {
		logger.Info("Creating a new instance of " + cfg.Name)
	}

	actualInst := actual.(*Instance)
	actualInst.cfg = &cfg
	actualInst.initialize()

	return actualInst, loaded
}

// GetInst Get a Singleton instance by name
func GetInst(name string) (*Instance, bool) {
	inst, ok := instMap.Load(name)
	if ok {
		return inst.(*Instance), ok
	}
	return nil, ok
}

func (i *Instance) initialize() {
	i.initializOnce.Do(i.doInitialize)
}

func (i *Instance) doInitialize() {
	logger.Debug("Initializing the instance of " + i.cfg.Name)
	service, _ := newDataService(i.cfg)
	i.trans = &defaultTrans{cfg: i.cfg, dService: service}
	i.RegisterCache(newCache())
	if i.cfg.EnableCache && i.cfg.InitializeCache {
		i.InitializeCache()
	}
}

// InitializeCache Initialize cache
func (i *Instance) InitializeCache() error {
	if i.cfg.EnableCache {
		return i.trans.dService.initializeCache()
	}
	return nil
}

// GetConfig Get the config of Singleton instance
func (i *Instance) GetConfig() Config {
	return *i.cfg
}

// GetTranslation Get translation instance
func (i *Instance) GetTranslation() Translation {
	return i.trans
}

// AddHTTPHeaders Add customized HTTP headers
func (i *Instance) AddHTTPHeaders(h map[string]string) {
	i.trans.dService.server.addHTTPHeaders(h)
}

// RegisterCache Register cache implementation. There is a default implementation
func (i *Instance) RegisterCache(cs Cache) {
	i.trans.dService.registerCache(cs)
}

// SetLogger Set a global logger. There is a default console logger
func SetLogger(l Logger) {
	logger = l
}
