/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"testing"

	"github.com/stretchr/testify/suite"
)

var (
	OnlyRegisteredSource_Config = Config{DefaultLocale: localeDefault, SourceLocale: localeEn}
	Key, Value                  = "RegisteredKey", "Value"
	UpdatedKey, UpdatedValue    = "message", "UpdatedValue"
	RegisteredMap               = map[string]string{Key: Value, UpdatedKey: UpdatedValue}
	ComponentToRegister         = component
)

type RegisterSource_TestSuite struct {
	suite.Suite
}

func (suite *RegisterSource_TestSuite) SetupSuite() {
	messages := NewMapComponentMsgs(RegisteredMap, localeEn, ComponentToRegister)
	resetInst(&OnlyRegisteredSource_Config, func() { RegisterSource(name, version, []ComponentMsgs{messages}) })
}

func (suite *RegisterSource_TestSuite) TestGetComponentMessages() {
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
		} else {
			suite.Contains(err.Error(), testData.errorMsg)
		}
	}
}

func (suite *RegisterSource_TestSuite) TestGetStringMessage() {
	var tests = []struct {
		desc      string
		locale    string
		component string
	}{
		{"Get an English translation", localeEn, ComponentToRegister},
	}

	for _, testData := range tests {
		msg, err := GetTranslation().GetStringMessage(name, version, testData.locale, testData.component, Key)
		suite.Nil(err)
		suite.Equal(Value, msg)
	}
}

func (suite *RegisterSource_TestSuite) TestGetComponentList() {
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

func (suite *RegisterSource_TestSuite) TestGetLocaleList() {
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

func TestRegisterSourceTestSuite(t *testing.T) {
	suite.Run(t, new(RegisterSource_TestSuite))
}
