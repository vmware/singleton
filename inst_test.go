/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"testing"

	"github.com/nbio/st"
	"github.com/stretchr/testify/assert"
	"gopkg.in/h2non/gock.v1"
)

func TestNewInst(t *testing.T) {
	defer Trace(curFunName())()

	var testCfg = backCfg
	testInst, ok := replaceInst(&testCfg)
	assert.False(t, ok)
	assert.Equal(t, testCfg, testInst.GetConfig())

	// TODO: Test bundle

	if len(testCfg.SingletonServer) != 0 {
		assert.NotNil(t, testInst.trans.dService.server)
	}

	// Verify translation manager
	assert.NotNil(t, testInst.trans)

	// Verify data service
	dataService := testInst.trans.dService
	assert.NotNil(t, dataService)
	if testCfg.EnableCache {
		assert.NotNil(t, dataService.cache)
		assert.NotNil(t, dataService.cacheSyncInfo)
	}
}

func TestNewInstWithCache(t *testing.T) {
	defer Trace(curFunName())()

	test := struct {
		desc  string
		mocks []string
	}{
		"NewInstWithCache",
		[]string{
			"componentMessages-zh-Hans-sunglow",
			"componentMessages-en-US-sunglow",
			"componentMessages-fr-sunglow",
			"componentMessages-zh-Hans-users",
			"componentMessages-en-US-users",
			"componentMessages-fr-users",
			"productComponents",
			"productLocales",
		},
	}

	defer gock.Off()

	for m := 0; m < len(test.mocks); m++ {
		EnableMockData(test.mocks[m])
	}

	newCfg := backCfg
	newCfg.InitializeCache = true
	newInst, _ := replaceInst(&newCfg)

	pData := newInst.trans.dService.cache.(*defaultCache).tMessages.(*defaultTransMsgs)

	assert.Equalf(t, 4, pData.Size(), "Total components are %d, want %d.", pData.Size(), 4)
	assert.True(t, gock.IsDone())

	// Initiaze cache one more time, shouldn't connect server
	_, err := newInst.GetTranslation().GetComponentMessages("zh-Hans", "sunglow")
	assert.Nil(t, err)

	// Clear cache
	clearCache(newInst)
	assert.Equal(t, 0, pData.Size())
}

func TestNewSameInst(t *testing.T) {
	defer Trace(curFunName())()

	testCfg := backCfg
	testInst, _ := replaceInst(&testCfg)

	newInst, loaded := NewInst(testCfg)
	assert.True(t, loaded)
	assert.Same(t, testInst, newInst)
}

func TestNewSecondInst(t *testing.T) {
	defer Trace(curFunName())()

	secondCfg, _ := NewConfig("testdata/conf/config2.yaml")
	secondInst, loaded := NewInst(*secondCfg)
	assert.False(t, loaded)
	assert.NotNil(t, secondInst)

	gotInst, ok := GetInst(secondCfg.Name)
	assert.True(t, ok)
	assert.Same(t, secondInst, gotInst)
}

func TestGetInst(t *testing.T) {
	defer Trace(curFunName())()

	testCfg := backCfg
	testInst, _ := replaceInst(&testCfg)

	gotInst, ok := GetInst(testCfg.Name)
	assert.True(t, ok)
	assert.Same(t, testInst, gotInst)
}
func TestGetNullInst(t *testing.T) {
	defer Trace(curFunName())()

	gotInst, ok := GetInst("Nonexistent")
	assert.False(t, ok)
	assert.Nil(t, gotInst)
}

func TestDisableCache(t *testing.T) {
	defer Trace(curFunName())()

	newCfg := backCfg
	newCfg.EnableCache = false
	newInst, _ := replaceInst(&newCfg)

	var testData = struct {
		desc  string
		mocks []string
	}{"Get without cache", []string{"componentMessages-zh-Hans-sunglow", "productLocales", "productComponents"}}

	locale := "zh-Hans"
	component := "sunglow"
	expected := 7

	defer gock.Off()

	trans := newInst.GetTranslation()
	cache := newInst.trans.dService.cache
	assert.Nil(t, cache)
	cacheSyncInfo := newInst.trans.dService.cacheSyncInfo
	assert.Nil(t, cacheSyncInfo)

	// check again to make sure cache doesn't work
	for i := 0; i < 2; i++ {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, err := trans.GetComponentMessages(locale, component)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			return
		}
		if messages.Size() != expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.Size(), expected)
		}

		// Get locales
		expectedLocales := 3
		locales, err := trans.GetLocaleList()
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			return
		}
		assert.Equal(t, expectedLocales, len(locales))

		// Get components
		expectedComponents := 2
		components, err := trans.GetComponentList()
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			return
		}
		assert.Equal(t, expectedComponents, len(components))

		assert.True(t, gock.IsDone())
	}

	errInit := newInst.InitializeCache()
	assert.Nil(t, errInit)
}

func TestCacheNotExpire(t *testing.T) {
	defer Trace(curFunName())()

	newCfg := backCfg
	newCfg.CacheExpiredTime = 0
	newInst, _ := replaceInst(&newCfg)

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		expected  int
		err       string
	}{
		{"Get messages of a component which cache never expires", []string{"componentMessages-zh-Hans-sunglow"}, "zh-Hans", "sunglow", 7, ""},
	}

	defer gock.Off()

	trans := newInst.GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, err := trans.GetComponentMessages(testData.locale, testData.component)
		assert.True(t, gock.IsDone())
		st.Expect(t, err, nil)
		assert.Equal(t, testData.expected, messages.Size())

		_, err = trans.GetComponentMessages(testData.locale, testData.component)
		st.Expect(t, err, nil)
	}
	assert.True(t, gock.IsDone())
}
