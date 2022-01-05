/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package cache

import (
	"sgtnserver/internal/logger"

	"go.uber.org/zap"
)

var caches = make(map[string]Cache)

// NewCache ...
func NewCache(name string, config map[string]interface{}) Cache {
	logger.Log.Debug("Create a new cache", zap.String("name", name), zap.Any("config", config))
	c, ok := caches[name]
	if !ok {
		c = NewRistrettoCache(config)
		caches[name] = c
	}
	return c
}

func GetCache(name string) (Cache, bool) {
	c, ok := caches[name]
	return c, ok
}
