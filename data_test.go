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

// Test cache control
func TestCC(t *testing.T) {

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		etag      string
		maxage    int64
		msgLen    int
	}{
		{"Test Save CC", []string{"componentMessages-zh-Hans-sunglow", "componentMessages-en-sunglow", "componentMessages-latest-sunglow"}, "zh-Hans", "sunglow", "1234567890", 1221965, 7},
		{"Test Send CC", []string{"componentMessages-zh-Hans-sunglow-sendCC", "componentMessages-en-sunglow", "componentMessages-latest-sunglow"}, "zh-Hans", "sunglow", "0987654321", 2334, 7},
		{"Test Receive 304", []string{"componentMessages-zh-Hans-HTTP304", "componentMessages-en-sunglow", "componentMessages-latest-sunglow"}, "zh-Hans", "sunglow", "0987654321", 3445, 7},
	}

	defer gock.Off()

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg, nil)
	trans := GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		item := &dataItem{dataItemID{itemComponent, name, version, testData.locale, testData.component}, nil, nil, nil}
		err := trans.(*transMgr).Translation.(*transInst).msgOrigin.(*cacheService).Get(item)
		time.Sleep(10 * time.Millisecond)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}

		cachedData, _ := cache.Get(dataItemID{itemComponent, name, version, locale, component})
		cachedItem := cachedData.(*dataItem)
		messages := cachedItem.data.(ComponentMsgs)
		assert.NotNil(t, cachedItem.attrs)
		assert.Equal(t, testData.etag, cachedItem.attrs.getETag())
		assert.Equal(t, testData.maxage, cachedItem.attrs.age)
		assert.Equal(t, testData.msgLen, messages.(*MapComponentMsgs).Size())

		expireCache(cachedItem.attrs)
		assert.True(t, gock.IsDone())
	}
}

func TestFallbackToLocalBundles(t *testing.T) {
	resetInst(&testCfg, nil)

	locale, component := "fr", "sunglow"

	msgs, err := GetTranslation().GetComponentMessages(name, version, locale, component)
	assert.Nil(t, err)
	assert.Equal(t, 4, msgs.(*MapComponentMsgs).Size())

	item := &dataItem{dataItemID{itemComponent, name, version, locale, component}, nil, nil, nil}
	info := getCacheInfo(item.id)
	assert.Equal(t, int64(cacheDefaultExpires), info.age) // Set max age to cacheDefaultExpires when server is unavailable temporarily.
}
