/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"net/url"
	"sync"
	"testing"
	"time"

	"github.com/nbio/st"
	"github.com/stretchr/testify/assert"
	"gopkg.in/h2non/gock.v1"
)

func TestGetCompMessages(t *testing.T) {

	defer Trace(curFunName())()

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

	testInst := resetInst(&testCfg)
	trans := testInst.GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		if err != nil {
			t.Errorf("%s failed: %v", testData.desc, err)
			continue
		}
		if messages.Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.Size(), testData.expected)
		}

		messagesInCache, found := testInst.dService.cache.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.True(t, found)
		assert.NotNil(t, messagesInCache)
		assert.Equal(t, testData.expected, messagesInCache.Size())
	}

	assert.True(t, gock.IsDone())
}

func TestGetStringMessage(t *testing.T) {
	defer Trace(curFunName())()
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

	testInst := resetInst(&testCfg)
	trans := testInst.GetTranslation()
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
	defer Trace(curFunName())()

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

	testInst := resetInst(&testCfg)
	trans := testInst.GetTranslation()
	dataService := testInst.trans
	cacheObj := testInst.dService.cache
	cacheSyncInfo := testInst.dService.cacheSyncInfo
	for _, testData := range tests {
		EnableMockData(testData.mocks[0])
		cacheUInfo := cacheSyncInfo.getCompUpdateInfo(name, version, testData.locale, testData.component)
		cacheUInfo.setAge(100)

		// Get component messages first to populate cache
		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		if messages.Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.Size(), testData.expected)
		}

		// Make sure mock data is comsumed
		assert.True(t, gock.IsDone())
		gock.Clean()

		// Check the data in cache
		messagesInCache, found := cacheObj.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.True(t, found)
		assert.NotNil(t, messagesInCache)
		assert.Equal(t, testData.expected, messagesInCache.Size())

		// Getting before time out, no communication to server because mock is enabled
		messages, err = dataService.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, err)
		if messages.Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.Size(), testData.expected)
		}

		// Enable mock, time out cache and refresh again. This time the data is same as before
		EnableMockData(testData.mocks[1])
		expireCache(cacheUInfo, cacheUInfo.age)
		messages, err = trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, err)
		assert.Equal(t, testData.expected, messages.Size())

		// Start the go routine of refresing cache, and wait for finish. Data entry number changes to 7.
		time.Sleep(10 * time.Millisecond)
		cacheUInfo.waitUpdate()
		// Make sure mock data is comsumed
		assert.True(t, gock.IsDone())

		// Check the data in cache
		messagesInCache, found = cacheObj.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.True(t, found)
		assert.Equal(t, 7, messagesInCache.Size())
	}

	assert.True(t, gock.IsDone())
}

// Refresh simultaneously. Hard to test. This is only for improve coverage
func TestRefreshCache2(t *testing.T) {
	defer Trace(curFunName())()

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

	testInst := resetInst(&testCfg)
	trans := testInst.GetTranslation()
	for _, testData := range tests {
		EnableMockDataWithTimes(testData.mocks[0], 100).Response.Delay(time.Microsecond)

		var wg sync.WaitGroup
		for i := 0; i < 100; i++ {
			wg.Add(1)
			go func() {
				defer wg.Done()
				messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
				assert.Nil(t, err)
				if messages.Size() != testData.expected {
					t.Errorf("%s = %d, want %d", testData.desc, messages.Size(), testData.expected)
				}
			}()
		}
		wg.Wait()

		gock.Flush()

		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, err)
		if messages.Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.Size(), testData.expected)
		}
	}
}

func TestGetStringFallback(t *testing.T) {
	defer Trace(curFunName())()

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

	testInst := resetInst(&testCfg)
	trans := testInst.GetTranslation()

	// Normal fallback
	locale := "zh-Hans"
	comp := "users"
	key := "Singleton.description"
	arg := "MyArg"
	expected := "MyArg est une bibliothèque commune développée par Singleton Team."
	message, err := trans.GetStringMessage(name, version, locale, comp, key, arg)
	st.Expect(t, err, nil)
	assert.Equal(t, expected, message)

	assert.True(t, gock.IsDone())
}

func TestGetStringAbnormal(t *testing.T) {
	defer Trace(curFunName())()

	test := struct {
		desc  string
		mocks []string
	}{
		"TestFallbackDefault",
		[]string{
			"componentMessages-fr-users",
			"componentMessages-zh-Hans-sunglow",
			"componentMessages-zh-Hans-comp-notexist",
		},
	}

	defer gock.Off()

	for m := 0; m < len(test.mocks); m++ {
		EnableMockData(test.mocks[m])
	}

	newCfg := testCfg
	newCfg.OfflineResourcesBaseURL = ""
	testInst := resetInst(&newCfg)
	trans := testInst.GetTranslation()

	localeZhhans := "zh-Hans"
	defaultLocaleFr := "fr"
	compSunglow := "sunglow"
	key := "MyKey"
	arg := "MyArg"

	// original locale has component, but doesn't have Key
	keyNonexistent := "nonexistent"
	message2, err2 := trans.GetStringMessage(name, version, localeZhhans, compSunglow, keyNonexistent, arg)
	assert.Contains(t, err2.Error(), localeZhhans)
	assert.Contains(t, err2.Error(), compSunglow)
	assert.Contains(t, err2.Error(), "No key in")
	assert.Equal(t, keyNonexistent, message2)

	// original locale doesn't have component.
	// default locale has component, but doesn't have Key
	compUsers := "users"
	message3, err3 := trans.GetStringMessage(name, version, localeZhhans, compUsers, keyNonexistent, arg)
	assert.Contains(t, err3.Error(), defaultLocaleFr)
	assert.Contains(t, err3.Error(), compUsers)
	assert.Contains(t, err3.Error(), "No key in")
	assert.Equal(t, keyNonexistent, message3)

	// Both locales doesn't have the component
	compNonexistent := "comp-notexist"
	message4, err4 := trans.GetStringMessage(name, version, localeZhhans, compNonexistent, key, arg)
	assert.NotNil(t, err4)
	assert.NotContains(t, err4.Error(), "No key in")
	assert.Equal(t, key, message4)

	// Get default locale directly. Default locale doesn't have the component
	message5, err5 := trans.GetStringMessage(name, version, defaultLocaleFr, compNonexistent, key, arg)
	assert.NotNil(t, err5)
	assert.NotContains(t, err5.Error(), "No key in")
	assert.Equal(t, key, message5)

	// Get default locale directly. Default locale doesn't have the key
	message6, err6 := trans.GetStringMessage(name, version, defaultLocaleFr, compUsers, keyNonexistent, arg)
	assert.NotNil(t, err6)
	assert.Contains(t, err6.Error(), "No key in")
	assert.Equal(t, keyNonexistent, message6)

	assert.True(t, gock.IsDone())
}

func TestDecodeError(t *testing.T) {
	defer Trace(curFunName())()

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		err       string
	}{
		{"DecodeError", []string{"componentMessages-zh-Hans-sunglow-decodeerror"}, "zh-Hans", "sunglow", "unconvertible type 'string'"},
	}
	defer gock.Off()

	newCfg := testCfg
	newCfg.OfflineResourcesBaseURL = ""
	testInst := resetInst(&newCfg)
	trans := testInst.GetTranslation()
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

func TestGetCompMessagesAbnormal(t *testing.T) {
	defer Trace(curFunName())()

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
	newCfg.OfflineResourcesBaseURL = ""
	testInst := resetInst(&newCfg)
	trans := testInst.GetTranslation()
	cache := testInst.dService.cache
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, messages)
		assert.Contains(t, err.Error(), testData.err)

		compCache, found := cache.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.False(t, found, testData.desc)
		assert.Nil(t, compCache, testData.desc)
	}

	assert.True(t, gock.IsDone())
}

func TestGetCompMessagesWrongServer(t *testing.T) {
	defer Trace(curFunName())()

	newCfg := testCfg
	newCfg.OfflineResourcesBaseURL = ""
	testInst := resetInst(&newCfg)
	wrongServer, err := url.Parse("wrongserver")
	assert.Nil(t, err)
	testInst.dService.server.svrURL = wrongServer

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

	trans := testInst.GetTranslation()
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
	defer Trace(curFunName())()

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		expected  int
		err       string
	}{
		{"Wrong Reponse content", []string{"componentMessages-zh-Hans-sunglow-WrongResponseContent"}, "zh-Hans", "WrongResponseContent", 0, "invalid character"},
	}

	defer gock.Off()

	newCfg := testCfg
	newCfg.OfflineResourcesBaseURL = ""
	testInst := resetInst(&newCfg)
	trans := testInst.GetTranslation()
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
	defer Trace(curFunName())()

	var tests = []struct {
		desc      string
		mocks     []string
		locale    string
		component string
		expected  int
		err       string
	}{
		{"Reponse contains partial data", []string{"componentMessages-zh-Hans-sunglow-ResponsePartial"}, "zh-Hans", "ResponsePartial", 0, ""},
	}

	defer gock.Off()

	newCfg := testCfg
	testInst := resetInst(&newCfg)
	trans := testInst.GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		messages, _ := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.True(t, messages == nil || messages.Size() == 0)
	}

	assert.True(t, gock.IsDone())
}

func TestAddHTTPHeader(t *testing.T) {
	defer Trace(curFunName())()

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
	testInst := resetInst(&newCfg)
	trans := testInst.GetTranslation()
	for _, testData := range tests {
		for _, m := range testData.mocks {
			EnableMockData(m)
		}

		testInst.AddHTTPHeaders(map[string]string{
			"user": "test_user",
			"pass": "goodpadd",
		})

		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
		assert.Nil(t, err)

		if messages.Size() != testData.expected {
			t.Errorf("%s = %d, want %d", testData.desc, messages.Size(), testData.expected)
		}
	}

	assert.True(t, gock.IsDone())
}

func TestGetComponents(t *testing.T) {
	defer Trace(curFunName())()

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
	testInst := resetInst(&newCfg)
	trans := testInst.GetTranslation()
	ui := testInst.dService.cacheSyncInfo.getComponentsUpdateInfo(name, version)
	ui.setAge(100)
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
		expireCache(ui, ui.age)
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

func TestGetLocales(t *testing.T) {
	defer Trace(curFunName())()

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
	testInst := resetInst(&newCfg)
	trans := testInst.GetTranslation()
	for _, testData := range tests {
		EnableMockData(testData.mocks[0])

		ui := testInst.dService.cacheSyncInfo.getLocalesUpdateInfo(name, version)
		ui.setAge(100)

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
		expireCache(ui, ui.age)
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
	defer Trace(curFunName())()

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
	testInst := resetInst(&newCfg)
	trans := testInst.GetTranslation()
	for _, m := range testData.mocks {
		EnableMockData(m)
	}

	messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
	assert.NotNil(t, err)
	assert.Nil(t, messages)

	assert.True(t, gock.IsDone())
}
