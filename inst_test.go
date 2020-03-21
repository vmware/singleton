/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestGetInst(t *testing.T) {
	defer Trace(curFunName())()

	testInst := resetInst(&testCfg)
	// assert.Equal(t, testCfg, testInst.GetConfig())
	assert.Equal(t, testCfg.OfflineResourcesBaseURL, testInst.trans.ds.bundle.root)
	// TODO: Test bundle

	if len(testCfg.OnlineServiceURL) != 0 {
		assert.NotNil(t, testInst.trans.ds.server)
	}

	// Verify translation manager
	assert.NotNil(t, testInst.trans)

	// Verify data service
	dataService := testInst.trans.ds
	assert.NotNil(t, dataService)
	// if testCfg.EnableCache {
	assert.NotNil(t, dataService.cache)
	assert.NotNil(t, cacheInfoMap)
	// }
}

// func TestDisableCache(t *testing.T) {
// 	defer Trace(curFunName())()

// 	cacheSync = nil
// 	newCfg := testCfg
// 	newCfg.EnableCache = false
// 	newInst := resetInst(&newCfg)

// 	var testData = struct {
// 		desc  string
// 		mocks []string
// 	}{"Get without cache", []string{"componentMessages-zh-Hans-sunglow", "productLocales", "productComponents"}}

// 	locale := "zh-Hans"
// 	component := "sunglow"
// 	expected := 7

// 	defer gock.Off()
// 	trans := newInst.GetTranslation()
// 	cache := newInst.dService.cache
// 	assert.Nil(t, cache)
// 	cacheInfo := cacheSync
// 	assert.Nil(t, cacheInfo)

// 	// check again to make sure cache doesn't work
// 	for i := 0; i < 2; i++ {
// 		for _, m := range testData.mocks {
// 			EnableMockData(m)
// 		}

// 		messages, err := trans.GetComponentMessages(name, version, locale, component)
// 		if err != nil {
// 			t.Errorf("%s failed: %v", testData.desc, err)
// 			return
// 		}
// 		if messages.Size() != expected {
// 			t.Errorf("%s = %d, want %d", testData.desc, messages.Size(), expected)
// 		}

// 		// Get locales
// 		expectedLocales := 3
// 		locales, err := trans.GetLocaleList(name, version)
// 		if err != nil {
// 			t.Errorf("%s failed: %v", testData.desc, err)
// 			return
// 		}
// 		assert.Equal(t, expectedLocales, len(locales))

// 		// Get components
// 		expectedComponents := 2
// 		components, err := trans.GetComponentList(name, version)
// 		if err != nil {
// 			t.Errorf("%s failed: %v", testData.desc, err)
// 			return
// 		}
// 		assert.Equal(t, expectedComponents, len(components))

// 		assert.True(t, gock.IsDone())
// 	}
// }
