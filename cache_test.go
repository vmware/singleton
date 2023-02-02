/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestCacheExpireWhenNeverExpire(t *testing.T) {

	newCfg := testCfg
	newCfg.ServerURL = ""
	resetInst(&newCfg)

	locale, component := "fr", "sunglow"
	item := &dataItem{dataItemID{itemComponent, name, version, locale, component}, nil, nil}
	info := getCacheInfo(item)

	GetTranslation().GetComponentMessages(name, version, locale, component)

	// value is initial value(cacheDefaultExpires) because only local bundles are available. No chance to change this.
	assert.Equal(t, int64(cacheDefaultExpires), info.age)

	// Rename dir to make sure getting from cache
	// bundleDir := GetTranslation().(*defaultTrans).ds.bundle.root
	// tempDir := bundleDir + "temp"
	// os.Rename(bundleDir, tempDir)
	// defer os.Rename(tempDir, bundleDir)

	// Run again to get from cache
	msgs, err := GetTranslation().GetComponentMessages(name, version, locale, component)
	assert.Nil(t, err)
	assert.Equal(t, 4, msgs.(*MapComponentMsgs).Size())
}

func TestRegisterCache(t *testing.T) {
	if cache == nil {
		resetInst(&testCfg)
	}

	oldCache := cache
	newCache := newCache()
	RegisterCache(newCache)
	//Check cache doesn't change because cache is already initialized.
	assert.Equal(t, oldCache, cache)

	cache = nil
	RegisterCache(newCache)
	//Check cache is changed because cache is nil before registration.
	assert.Equal(t, newCache, cache)
}
