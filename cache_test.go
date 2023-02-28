/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"testing"
	"time"

	"github.com/stretchr/testify/assert"
	"gopkg.in/h2non/gock.v1"
)

func TestCacheExpireWhenNeverExpire(t *testing.T) {

	newCfg := testCfg
	newCfg.ServerURL = ""
	resetInst(&newCfg, nil)

	locale, component := "fr", "sunglow"
	item := &dataItem{dataItemID{itemComponent, name, version, locale, component}, nil, nil, nil}

	GetTranslation().GetComponentMessages(name, version, locale, component)

	// value is initial value(cacheDefaultExpires) because only local bundles are available. No chance to change this.
	info := getCacheInfo(item.id)
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
		resetInst(&testCfg, nil)
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

func TestRefreshCache(t *testing.T) {

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		expected  int
		err       string
	}{
		{"RefreshCache", []string{"RefreshCache", "RefreshCacheSecondTime"}, "RefreshCache", "sunglow", 6, ""},
	}

	defer gock.Off()

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg, nil)
	trans := GetTranslation()
	for _, testData := range tests {
		EnableMockData(testData.mocks[0])
		id := dataItemID{itemComponent, name, version, testData.locale, testData.component}

		// Get component messages first to populate cache
		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, err, testData.desc)
		if messages.(*MapComponentMsgs).Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.(*MapComponentMsgs).Size(), testData.expected)
		}

		// Make sure mock data is consumed
		assert.True(t, gock.IsDone())
		gock.Clean()

		// Check the data in cache
		messagesInCache, found := cache.Get(id)
		assert.True(t, found)
		assert.NotNil(t, messagesInCache)
		assert.Equal(t, testData.expected, messagesInCache.(*dataItem).data.(*MapComponentMsgs).Size())

		// Getting before time out, no communication to server because mock is enabled
		messages, err = trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, err)
		if messages.(*MapComponentMsgs).Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.(*MapComponentMsgs).Size(), testData.expected)
		}

		// Enable mock, time out cache and fetch(refresh) again. This time the data is same as before
		EnableMockData(testData.mocks[1])
		expireCache(getCacheInfo(id))
		messages, err = trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, err)
		assert.Equal(t, testData.expected, messages.(*MapComponentMsgs).Size())

		// Start the go routine of refreshing cache, and wait for finish. Data entry number changes to 7.
		for getCacheInfo(id).isExpired() {
			time.Sleep(time.Microsecond)
		}

		// Make sure mock data is consumed
		assert.True(t, gock.IsDone())

		// Check the data in cache
		messagesInCache, found = cache.Get(id)
		assert.True(t, found)
		assert.Equal(t, 7, messagesInCache.(*dataItem).data.(ComponentMsgs).(*MapComponentMsgs).Size())
	}

	assert.True(t, gock.IsDone())
}
