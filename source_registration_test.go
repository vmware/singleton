/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"testing"

	"github.com/stretchr/testify/suite"
)

var (
	OnlyRegisteredSourceConfig     = Config{DefaultLocale: localeDefault, SourceLocale: localeEn}
	RegisteredKey, RegisteredValue = "RegisteredKey", "RegisteredValue"
	UpdatedKey, UpdatedValue       = "message", "UpdatedValue"
	UnchangedKey, UnchangedValue   = "one.arg", "test one argument {0}"
	RegisteredMap                  = map[string]string{RegisteredKey: RegisteredValue, UpdatedKey: UpdatedValue, UnchangedKey: UnchangedValue}
	ComponentToRegister            = component
)

type RegisterSourceTestSuite struct {
	suite.Suite
}

func (suite *RegisterSourceTestSuite) SetupSuite() {
	messages := NewMapComponentMsgs(RegisteredMap, localeEn, ComponentToRegister)
	resetInst(&OnlyRegisteredSourceConfig, func() { RegisterSource(name, version, []ComponentMsgs{messages}) })
	// suite.origin = GetTranslation().(*transMgr).transInst.msgOrigin.(*cacheService).messageOrigin.(*singleLoader).messageOrigin.(*saveToCache).messageOrigin.(*sourceComparison).source.(messageOriginList)[0]
}

func (suite *RegisterSourceTestSuite) TestGetComponentMessages() {
	var tests = []struct {
		desc      string
		locale    string
		component string
		expected  int
		errorMsg  string
	}{
		{"Get an English component", localeEn, ComponentToRegister, len(RegisteredMap), ""},
		{"Get a Chinese component", locale, ComponentToRegister, 0, locale},
	}

	for _, testData := range tests {
		messages, err := GetTranslation().GetComponentMessages(name, version, testData.locale, testData.component)
		if testData.errorMsg == "" {
			suite.Nil(err, "%s failed: %v", testData.desc, err)
			suite.Equalf(testData.expected, messages.Size(), "%s: different string numbers are found.", testData.desc)
			cachedItem := getCachedItem(dataItemID{itemComponent, name, version, testData.locale, testData.component})
			suite.IsType(sourceAsOrigin{}, cachedItem.origin)
			suite.False(getCacheService().IsExpired(cachedItem), testData.desc)
		} else {
			suite.Contains(err.Error(), testData.errorMsg)
		}
	}
}

func (suite *RegisterSourceTestSuite) TestGetStringMessage() {
	var tests = []struct {
		desc      string
		locale    string
		component string
	}{
		{"Get an English translation", localeEn, ComponentToRegister},
	}

	for _, testData := range tests {
		msg, err := GetTranslation().GetStringMessage(name, version, testData.locale, testData.component, RegisteredKey)
		suite.Nil(err)
		suite.Equal(RegisteredValue, msg)
	}
}

func (suite *RegisterSourceTestSuite) TestGetComponentList() {
	var tests = []struct {
		desc                  string
		expectedComponentList []string
	}{
		{"Get component list normally", []string{ComponentToRegister}},
	}

	for _, testData := range tests {
		compList, err := GetTranslation().GetComponentList(name, version)
		suite.Nil(err, "%s: failed to get component list", testData.desc)
		suite.EqualValues(testData.expectedComponentList, compList)
	}
}

func (suite *RegisterSourceTestSuite) TestGetLocaleList() {
	var tests = []struct {
		desc               string
		expectedLocaleList []string
	}{
		{"Get locale list normally", []string{localeEn}},
	}

	for _, testData := range tests {
		localeList, err := GetTranslation().GetLocaleList(name, version)
		suite.Nil(err, "%s: failed to get locale list", testData.desc)
		suite.EqualValues(testData.expectedLocaleList, localeList)
	}
}

func (suite *RegisterSourceTestSuite) TestGetNonexistentRelease() {
	nonExistentVersion := version + "-nonexistent"
	expectedError := fmt.Sprintf(errorReleaseNonexistent, name, nonExistentVersion)
	messages, err := GetTranslation().GetComponentMessages(name, nonExistentVersion, inst.cfg.GetSourceLocale(), component)
	suite.Equal(expectedError, err.Error())
	suite.Nil(messages)

	components, err := GetTranslation().GetComponentList(name, nonExistentVersion)
	suite.Equal(expectedError, err.Error())
	suite.Nil(components)
}

func TestRegisterSourceTestSuite(t *testing.T) {
	suite.Run(t, new(RegisterSourceTestSuite))
}
