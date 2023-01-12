/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"net/url"
	"sync"
	"testing"
	"time"

	"github.com/nbio/st"
	"github.com/stretchr/testify/assert"
	"gopkg.in/h2non/gock.v1"
)

func TestGetCompMessages(t *testing.T) {

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		expected  int
		err       string
	}{
		{"Get messages of a component normally", []string{"componentMessages-zh-Hans-sunglow"}, "zh-Hans", "sunglow", 7, ""},
	}

	defer gock.Off()

	resetInst(&testCfg)
	trans := GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}
		if messages.(*defaultComponentMsgs).Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.(*defaultComponentMsgs).Size(), testData.expected)
		}

		messagesInCache, found := cache.Get(dataItemID{itemComponent, name, version, testData.locale, testData.component})
		assert.True(t, found)
		assert.NotNil(t, messagesInCache)
		assert.Equal(t, testData.expected, messagesInCache.(*defaultComponentMsgs).Size())
	}

	assert.True(t, gock.IsDone())
}

func TestGetStringMessage(t *testing.T) {

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		key       string
		args      []string
		expected  string
		err       string
	}{
		{"Get a string message normally", []string{"componentMessages-zh-Hans-sunglow"}, "zh-Hans", "sunglow", "application.title", []string{}, "欢迎来到 Singleton Go 示例应用!", ""},
	}

	defer gock.Off()

	resetInst(&testCfg)
	trans := GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		message, err := trans.GetStringMessage(name, version, testData.locale, testData.component, testData.key)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}

		if message != testData.expected {
			t.Errorf("%s = %q, want %q", testData.desc, message, testData.expected)
		}
	}
	assert.True(t, gock.IsDone())
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
	resetInst(&newCfg)
	trans := GetTranslation()
	for _, testData := range tests {
		EnableMockData(testData.mocks[0])
		item := &dataItem{dataItemID{itemComponent, name, version, testData.locale, testData.component}, nil, nil}
		info := getCacheInfo(item)
		status := trans.(*transMgr).Translation.(*transInst).msgOrigin.(*cacheService).getStatus(item)
		info.setAge(100)

		// Get component messages first to populate cache
		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		if messages.(*defaultComponentMsgs).Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.(*defaultComponentMsgs).Size(), testData.expected)
		}

		// Make sure mock data is consumed
		assert.True(t, gock.IsDone())
		gock.Clean()

		// Check the data in cache
		messagesInCache, found := cache.Get(dataItemID{itemComponent, name, version, testData.locale, testData.component})
		assert.True(t, found)
		assert.NotNil(t, messagesInCache)
		assert.Equal(t, testData.expected, messagesInCache.(*defaultComponentMsgs).Size())

		// Getting before time out, no communication to server because mock is enabled
		messages, err = trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, err)
		if messages.(*defaultComponentMsgs).Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.(*defaultComponentMsgs).Size(), testData.expected)
		}

		// Enable mock, time out cache and fetch(refresh) again. This time the data is same as before
		EnableMockData(testData.mocks[1])
		expireCache(info, info.age)
		messages, err = trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, err)
		assert.Equal(t, testData.expected, messages.(*defaultComponentMsgs).Size())

		// Start the go routine of refreshing cache, and wait for finish. Data entry number changes to 7.
		time.Sleep(10 * time.Millisecond)
		status.waitUpdate()
		// Make sure mock data is consumed
		assert.True(t, gock.IsDone())

		// Check the data in cache
		messagesInCache, found = cache.Get(dataItemID{itemComponent, name, version, testData.locale, testData.component})
		assert.True(t, found)
		assert.Equal(t, 7, messagesInCache.(ComponentMsgs).(*defaultComponentMsgs).Size())
	}

	assert.True(t, gock.IsDone())
}

// Refresh simultaneously. Hard to test. This is only for improve coverage
func TestRefreshCache2(t *testing.T) {

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		expected  int
		err       string
	}{
		{"RefreshCache", []string{"RefreshCache"}, "RefreshCache", "sunglow", 6, ""},
	}

	defer gock.Off()

	resetInst(&testCfg)
	trans := GetTranslation()
	for _, testData := range tests {
		EnableMockDataWithTimes(testData.mocks[0], 100).Response.Delay(time.Microsecond) // Delay to simulate concurrency

		var wg sync.WaitGroup
		for i := 0; i < 100; i++ {
			wg.Add(1)
			go func() {
				defer wg.Done()
				messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
				assert.Nil(t, err)
				if messages.(*defaultComponentMsgs).Size() != testData.expected {
					t.Errorf("%s = %d, want %d", testData.desc, messages.(*defaultComponentMsgs).Size(), testData.expected)
				}
			}()
		}
		wg.Wait()

		gock.Flush()

		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, err)
		if messages.(*defaultComponentMsgs).Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.(*defaultComponentMsgs).Size(), testData.expected)
		}
	}
}

// Test locale fallback when querying a message of a string
func TestGetStringFallback(t *testing.T) {

	test := struct {
		desc  string
		mocks []string
	}{
		"TestFallbackDefault",
		[]string{
			"componentMessages-fr-users",
		},
	}

	defer gock.Off()

	for m := 0; m < len(test.mocks); m++ {
		EnableMockData(test.mocks[m])
	}

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg)
	trans := GetTranslation()

	// Normal fallback
	locale := "zh-Hans"
	component := "users"
	key := "Singleton.description"
	arg := "MyArg"
	expected := "MyArg est une bibliothèque commune développée par Singleton Team."
	message, err := trans.GetStringMessage(name, version, locale, component, key, arg)
	st.Expect(t, err, nil)
	assert.Equal(t, expected, message)

	assert.True(t, gock.IsDone())
}

func TestGetStringAbnormal(t *testing.T) {
	test := struct {
		desc  string
		mocks []string
	}{
		"TestFallbackDefault",
		[]string{
			"componentMessages-fr-users",
			"componentMessages-zh-Hans-sunglow",
			"componentMessages-fr-sunglow",
			"componentMessages-zh-Hans-comp-notexist",
		},
	}

	defer gock.Off()

	for m := 0; m < len(test.mocks); m++ {
		EnableMockData(test.mocks[m])
	}

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg)
	trans := GetTranslation()

	localeZhhans := "zh-Hans"
	defaultLocaleFr := "fr"
	compSunglow := "sunglow"
	key := "MyKey"
	arg := "MyArg"

	// original locale has component, but doesn't have Key
	keyNonexistent := "nonexistent"
	message2, err2 := trans.GetStringMessage(name, version, localeZhhans, compSunglow, keyNonexistent, arg)
	assert.Contains(t, err2.Error(), defaultLocaleFr)
	assert.Contains(t, err2.Error(), compSunglow)
	assert.Contains(t, err2.Error(), "fail to get message")
	assert.Equal(t, keyNonexistent, message2)

	// original locale doesn't have component.
	// default locale has component, but doesn't have Key
	compUsers := "users"
	message3, err3 := trans.GetStringMessage(name, version, localeZhhans, compUsers, keyNonexistent, arg)
	assert.Contains(t, err3.Error(), defaultLocaleFr)
	assert.Contains(t, err3.Error(), compUsers)
	assert.Contains(t, err3.Error(), "fail to get message")
	assert.Equal(t, keyNonexistent, message3)

	// Both locales doesn't have the component
	compNonexistent := "comp-notexist"
	message4, err4 := trans.GetStringMessage(name, version, localeZhhans, compNonexistent, key, arg)
	assert.NotNil(t, err4)
	assert.NotContains(t, err4.Error(), "fail to get message")
	assert.Equal(t, key, message4)

	// Get default locale directly. Default locale doesn't have the component
	message5, err5 := trans.GetStringMessage(name, version, defaultLocaleFr, compNonexistent, key, arg)
	assert.NotNil(t, err5)
	assert.NotContains(t, err5.Error(), "fail to get message")
	assert.Equal(t, key, message5)

	// Get default locale directly. Default locale doesn't have the key
	message6, err6 := trans.GetStringMessage(name, version, defaultLocaleFr, compUsers, keyNonexistent, arg)
	assert.NotNil(t, err6)
	assert.Contains(t, err6.Error(), "fail to get message")
	assert.Equal(t, keyNonexistent, message6)

	assert.True(t, gock.IsDone())
}

// json.ToVal() doesn't return any error, so comment this case out.
// func TestDecodeError(t *testing.T) {
//

// 	var tests = []struct {
// 		desc      string
// 		mocks     []string
// 		locale    string
// 		component string
// 		err       string
// 	}{
// 		{"DecodeError", []string{"componentMessages-zh-Hans-sunglow-decodeerror"}, "zh-Hans", "sunglow", "Wrong data from server"},
// 	}
// 	defer gock.Off()

// 	newCfg := testCfg
// 	newCfg.LocalBundles = ""
// 	resetInst(&newCfg)
// 	trans := GetTranslation()
// 	for _, testData := range tests {
// 		for _, m := range testData.mocks {
// 			EnableMockData(m)
// 		}

// 		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
// 		assert.NotNil(t, err)
// 		assert.Nil(t, messages)
// 		assert.Contains(t, err.Error(), testData.err)
// 	}

// 	assert.True(t, gock.IsDone())

// }

func TestGetCompMessagesAbnormal(t *testing.T) {

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		expected  int
		err       string
	}{
		{"component doesn't exist", []string{"componentMessages-zh-Hans-comp-notexist"}, "zh-Hans", "comp-notexist", 0, "404"},
	}

	defer gock.Off()

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg)
	trans := GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, messages)
		assert.Contains(t, err.Error(), testData.err)

		compCache, found := cache.Get(dataItemID{itemComponent, name, version, testData.locale, testData.component})
		assert.False(t, found, testData.desc)
		assert.Nil(t, compCache, testData.desc)
	}

	assert.True(t, gock.IsDone())
}

func TestGetCompMessagesWrongServer(t *testing.T) {

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg)
	wrongServer, err := url.Parse("wrongserver")
	assert.Nil(t, err)
	inst.server.svrURL = wrongServer

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		expected  int
		err       string
	}{
		{"wrongserver", []string{}, "zh-Hans", "sunglow", 0, "unsupported protocol scheme"},
	}

	defer gock.Off()
	defer gock.DisableNetworking()
	gock.EnableNetworking()

	trans := GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, messages)
		assert.Contains(t, err.Error(), testData.err)

	}

	assert.True(t, gock.IsDone())
}

func TestGetCompMessagesWrongResponseContent(t *testing.T) {

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		expected  int
		err       string
	}{
		{"Wrong Response content", []string{"componentMessages-zh-Hans-sunglow-WrongResponseContent"}, "zh-Hans", "WrongResponseContent", 0, "ReadObjectCB: object not ended with"},
	}

	defer gock.Off()

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg)
	trans := GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, messages)
		assert.Contains(t, err.Error(), testData.err)
	}

	assert.True(t, gock.IsDone())
}

func TestGetCompMessagesResponsePartial(t *testing.T) {

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		expected  int
		err       string
	}{
		{"Response contains partial data", []string{"componentMessages-zh-Hans-sunglow-ResponsePartial"}, "zh-Hans", "ResponsePartial", 0, ""},
	}

	defer gock.Off()

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg)
	trans := GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, _ := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		// assert.Contains(t, "Fail to get from server", err.Error())
		assert.True(t, messages == nil || messages.(*defaultComponentMsgs).Size() == 0)
	}

	assert.True(t, gock.IsDone())
}

func TestAddHTTPHeader(t *testing.T) {

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		expected  int
		err       string
	}{
		{"Http headers", []string{"HTTPHeader"}, "zh-Hans", "headertest", 7, ""},
	}

	defer gock.Off()

	newCfg := testCfg
	resetInst(&newCfg)
	trans := GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		SetHTTPHeaders(map[string]string{
			"user": "test_user",
			"pass": "goodpadd",
		})

		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, err)

		if messages.(*defaultComponentMsgs).Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.(*defaultComponentMsgs).Size(), testData.expected)
		}
	}

	assert.True(t, gock.IsDone())
}

func TestGetComponentList(t *testing.T) {

	var tests = []struct {
		desc     string
		mocks    []string
		expected int
		err      string
	}{
		{"Get components normally", []string{"productComponents", "productComponents_secondtime"}, 2, ""},
	}

	defer gock.Off()

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg)
	trans := GetTranslation()
	item := &dataItem{dataItemID{itemComponents, name, version, "", ""}, nil, nil}
	info := getCacheInfo(item)
	info.setAge(100)
	for _, testData := range tests {

		EnableMockData(testData.mocks[0])

		components, err := trans.GetComponentList(name, version)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}
		assert.Equal(t, testData.expected, len(components))
		assert.True(t, gock.IsDone())

		// Get second time to test cache is working correctly
		components, err = trans.GetComponentList(name, version)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}
		assert.Equal(t, testData.expected, len(components))

		// Expire cache and get again
		EnableMockData(testData.mocks[1])
		expireCache(info, info.age)
		components, err = trans.GetComponentList(name, version)
		time.Sleep(time.Millisecond * 10)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}
		assert.Equal(t, testData.expected, len(components))
		assert.True(t, gock.IsDone())

		// test cache is working correctly
		components, err = trans.GetComponentList(name, version)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}
		assert.Equal(t, 3, len(components))
	}

	assert.True(t, gock.IsDone())
}

func TestGetLocaleList(t *testing.T) {

	var tests = []struct {
		desc     string
		mocks    []string
		expected int
		err      string
	}{
		{"Get locales normally", []string{"productLocales", "productLocales_secondtime"}, 3, ""},
	}

	defer gock.Off()

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg)
	trans := GetTranslation()
	for _, testData := range tests {
		EnableMockData(testData.mocks[0])

		item := &dataItem{dataItemID{itemLocales, name, version, "", ""}, nil, nil}
		info := getCacheInfo(item)
		info.setAge(100)

		locales, err := trans.GetLocaleList(name, version)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}

		assert.Equal(t, testData.expected, len(locales))
		assert.True(t, gock.IsDone())

		// Get second time to test cache is working correctly
		locales, err = trans.GetLocaleList(name, version)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}
		assert.Equal(t, testData.expected, len(locales))

		// Expire cache and get again
		EnableMockData(testData.mocks[1])
		expireCache(info, info.age)
		locales, err = trans.GetLocaleList(name, version)
		time.Sleep(time.Millisecond * 10)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}
		assert.Equal(t, testData.expected, len(locales))
		assert.True(t, gock.IsDone())

		// test cache is working correctly
		locales, err = trans.GetLocaleList(name, version)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}
		assert.Equal(t, 4, len(locales))
	}

	assert.True(t, gock.IsDone())
}

func TestHTTP404(t *testing.T) {

	var testData = struct {
		desc      string
		mocks     []string
		locale    string
		component string
		err       string
	}{
		"TestHTTP404", []string{"HTTP404"}, "zh-Hans", "HTTP404", ""}

	defer gock.Off()

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg)
	trans := GetTranslation()
	for _, m := range testData.mocks {
		EnableMockData(m)
	}

	messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
	assert.NotNil(t, err)
	assert.Nil(t, messages)

	assert.True(t, gock.IsDone())
}

func TestMultipleComponents(t *testing.T) {
	var tests = []struct {
		desc       string
		mocks      []string
		locales    []string
		components []string
		size       int
	}{
		{"Get messages of a bundle by multiple components interface",
			[]string{"componentMessages-zh-Hans-sunglow"},
			[]string{"zh-Hans"}, []string{"sunglow"}, 1},
		{"Get messages of a component by multiple components interface",
			[]string{"productLocales", "componentMessages-fr-sunglow", "componentMessages-zh-Hans-sunglow", "componentMessages-en-US-sunglow"},
			nil, []string{"sunglow"}, 3},
		{"Get messages of a locale by multiple components interface",
			[]string{"productComponents", "componentMessages-fr-sunglow", "componentMessages-fr-users"},
			[]string{"fr"}, nil, 2},
		{"Get messages of the product/version by multiple components interface",
			[]string{
				"productComponents", "productLocales",
				"componentMessages-fr-sunglow", "componentMessages-zh-Hans-sunglow", "componentMessages-en-US-sunglow",
				"componentMessages-fr-users", "componentMessages-zh-Hans-users", "componentMessages-en-US-users"},
			nil, nil, 6},
	}

	defer gock.Off()

	resetInst(&testCfg)
	trans := GetTranslation()
	for _, testData := range tests {
		logger.Debug(fmt.Sprintf("------------ Start testing: %s", testData.desc))
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, err := trans.GetComponentsMessages(name, version, testData.locales, testData.components)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}
		if len(messages) != testData.size {
			t.Errorf("%s = %d, want %d", testData.desc, len(messages), testData.size)
		}

		assert.True(t, gock.IsDone())

		clearCache()
	}
}

func TestMultipleComponentsAbnormal(t *testing.T) {
	var tests = []struct {
		desc        string
		mocks       []string
		locales     []string
		components  []string
		size        int
		errorString string
	}{
		{"Abnormal: fail to get locale list",
			[]string{"productLocales_400"},
			nil, []string{"sunglow"}, 0,
			`Error from server is HTTP code: 400, message: 400 Bad Request, business code: 0, message: `},
		{"Abnormal: fail to get component list",
			[]string{"productComponents_400"},
			[]string{"en"}, nil, 0,
			`Error from server is HTTP code: 400, message: 400 Bad Request, business code: 0, message: `},
		{"Abnormal: partial translations are available is treated as successful",
			[]string{
				"productComponents", "productLocales",
				"componentMessages-fr-sunglow", "componentMessages-zh-Hans-sunglow", "componentMessages-en-US-sunglow",
				"componentMessages-fr-users", "componentMessages-zh-Hans-users"},
			nil, nil, 5,
			""},
		{"Abnormal: empty result is an error",
			[]string{"HTTP404"},
			[]string{"zh-Hans"}, []string{"HTTP404"}, 0,
			"no translations are available for {product 'SgtnTest', version '1.0.0', locales '[zh-Hans]', components '[HTTP404]'}"},
	}

	defer gock.Off()

	newCfg := testCfg
	newCfg.LocalBundles = ""
	resetInst(&newCfg)
	trans := GetTranslation()
	for _, testData := range tests {
		logger.Debug(fmt.Sprintf("------------ Start testing: %s", testData.desc))
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, err := trans.GetComponentsMessages(name, version, testData.locales, testData.components)
		if len(testData.errorString) == 0 {
			assert.Nil(t, err)
		} else {
			assert.Equal(t, testData.errorString, err.Error())
		}
		if len(messages) != testData.size {
			t.Errorf("%s = %d, want %d", testData.desc, len(messages), testData.size)
		}

		assert.True(t, gock.IsDone())

		clearCache()
	}
}
