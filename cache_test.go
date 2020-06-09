/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestCacheNeverExpire(t *testing.T) {
	defer Trace(curFunName())()

	newCfg := testCfg
	newCfg.ServerURL = ""
	resetInst(&newCfg)

	locale, component := "fr", "sunglow"
	item := &dataItem{dataItemID{itemComponent, name, version, locale, component}, nil, nil}
	info := getCacheInfo(item)

	// assert Initial value isn't cacheNeverExpires(-1)
	assert.NotEqual(t, int64(cacheNeverExpires), info.age)
	GetTranslation().GetComponentMessages(name, version, locale, component)

	// assert value is cacheNeverExpires(-1) because only local bundles are available.
	assert.Equal(t, int64(cacheNeverExpires), info.age)
}

func TestCacheExpireWhenNeverExpire(t *testing.T) {
	defer Trace(curFunName())()

	newCfg := testCfg
	newCfg.ServerURL = ""
	resetInst(&newCfg)

	locale, component := "fr", "sunglow"
	item := &dataItem{dataItemID{itemComponent, name, version, locale, component}, nil, nil}
	info := getCacheInfo(item)

	GetTranslation().GetComponentMessages(name, version, locale, component)

	// value is cacheNeverExpires(-1) because only local bundles are available.
	assert.Equal(t, int64(cacheNeverExpires), info.age)

	// Rename dir to make sure getting from cache
	// bundleDir := GetTranslation().(*defaultTrans).ds.bundle.root
	// tempDir := bundleDir + "temp"
	// os.Rename(bundleDir, tempDir)
	// defer os.Rename(tempDir, bundleDir)

	// Run again to get from cache
	msgs, err := GetTranslation().GetComponentMessages(name, version, locale, component)
	assert.Nil(t, err)
	assert.Equal(t, 4, msgs.(*defaultComponentMsgs).Size())
}
