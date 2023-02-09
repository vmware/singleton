/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"sort"
	"testing"

	"github.com/stretchr/testify/suite"
	"gopkg.in/h2non/gock.v1"
)

var RegisteredSource_Server_Config = Config{ServerURL: ServerURL, DefaultLocale: localeDefault, SourceLocale: localeEn}

type RegisteredSource_Server_TestSuite struct {
	*RegisterSource_TestSuite
}

func (suite *RegisteredSource_Server_TestSuite) SetupSuite() {
	messages := NewMapComponentMsgs(RegisteredMap, localeEn, ComponentToRegister)
	resetInst(&RegisteredSource_Server_Config, func() { RegisterSource(name, version, []ComponentMsgs{messages}) })
}

func (suite *RegisteredSource_Server_TestSuite) TestGetComponentMessages() {
	defer gock.Off()

	var tests = []struct {
		desc      string
		locale    string
		component string
		mocks     []string
		expected  int
		errorMsg  string
	}{
		{"Get an English component - should get from source directly", localeEn, ComponentToRegister, []string{}, len(RegisteredMap), ""},
		{"Get a Chinese component", locale, ComponentToRegister, []string{"componentMessages-zh-Hans-sunglow", "componentMessages-en-sunglow"}, 8, ""},
		{"Get a component of unsupported locale", localeUnsupported, ComponentToRegister, []string{"componentMessages-xxx-sunglow"}, 0, "Failed to get translation"},
		{"Get a nonexistent component of source locale", localeSource, nonexistentComponent, []string{"componentMessages-latest-comp-notexist"}, 0, "Failed to get translation"},
		{"Get a nonexistent component of other locale", locale, nonexistentComponent, []string{"componentMessages-zh-Hans-comp-notexist"}, 0, "Failed to get translation"},
	}

	for _, testData := range tests {
		EnableMultipleMockData(testData.mocks)
		messages, err := GetTranslation().GetComponentMessages(name, version, testData.locale, testData.component)
		if testData.errorMsg == "" {
			suite.Nil(err, "%s failed", testData.desc)
			suite.Equalf(testData.expected, messages.Size(), "%s: different string numbers are found.", testData.desc)
		} else {
			suite.Contains(err.Error(), testData.errorMsg)
		}
		suite.True(gock.IsDone())
	}
}

func (suite *RegisteredSource_Server_TestSuite) TestGetLocaleList() {
	defer gock.Off()

	var tests = []struct {
		desc               string
		mocks              []string
		expectedLocaleList []string
	}{
		{"Get locale list", []string{"productLocales"}, []string{localeEn, localeDefault, locale}},
	}

	for _, testData := range tests {
		EnableMultipleMockData(testData.mocks)
		localeList, err := GetTranslation().GetLocaleList(name, version)
		sort.Strings(localeList)
		suite.Nil(err, "%s: failed to get locale list", testData.desc)
		suite.EqualValues(testData.expectedLocaleList, localeList)
		suite.True(gock.IsDone())
	}
}

func TestRegisteredSource_Server_TestSuite(t *testing.T) {
	suite.Run(t, &RegisteredSource_Server_TestSuite{RegisterSource_TestSuite: new(RegisterSource_TestSuite)})
}

// func TestLocalSource(t *testing.T) {
// 	var tests = []struct {
// 		desc      string
// 		locale    string
// 		component string
// 		expected  int
// 		err       string
// 	}{
// 		{"Get messages of a component normally", "en", "sunglow", 4, ""},
// 	}

// 	cfg := testCfg
// 	cfg.ServerURL = ""
// 	cfg.LocalBundles = ""
// 	cfg.localSource = "testdata/sources"
// 	resetInst(&cfg, nil)
// 	trans := GetTranslation()
// 	for _, testData := range tests {
// 		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
// 		if err != nil {
// 			t.Errorf("%s failed: %v", testData.desc, err)
// 			continue
// 		}
// 		if messages.(*MapComponentMsgs).Size() != testData.expected {
// 			t.Errorf("%s = %d, want %d", testData.desc, messages.(*MapComponentMsgs).Size(), testData.expected)
// 		}

// 		messagesInCache, found := cache.Get(dataItemID{itemComponent, name, version, testData.locale, testData.component})
// 		assert.True(t, found)
// 		assert.NotNil(t, messagesInCache)
// 		assert.Equal(t, testData.expected, messagesInCache.(*dataItem).data.(*MapComponentMsgs).Size())
// 	}

// 	assert.True(t, gock.IsDone())
// }
