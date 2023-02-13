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

type SourceComparison_TestSuite struct {
	suite.Suite
	messages ComponentMsgs
}

func (suite *SourceComparison_TestSuite) SetupSuite() {
	suite.messages = NewMapComponentMsgs(RegisteredMap, localeEn, ComponentToRegister)
	resetInst(&RegisteredSource_Server_Config, func() { RegisterSource(name, version, []ComponentMsgs{suite.messages}) })
}

func (suite *SourceComparison_TestSuite) TestSourceComparison() {
	defer gock.Off()

	resetInst(&RegisteredSource_Server_Config, func() { RegisterSource(name, version, []ComponentMsgs{suite.messages}) })

	var tests = []struct {
		desc      string
		locale    string
		component string
		mocks     []string
		expected  int
		errorMsg  string
	}{
		{"SourceComparison", locale, ComponentToRegister, []string{"componentMessages-zh-Hans-sunglow", "componentMessages-en-sunglow"}, 8, ""},
	}

	for _, testData := range tests {
		EnableMultipleMockData(testData.mocks)
		msg, err := GetTranslation().GetStringMessage(name, version, testData.locale, testData.component, Key)
		suite.Nil(err)
		suite.Equal(Value, msg)
		msg, err = GetTranslation().GetStringMessage(name, version, testData.locale, testData.component, UpdatedKey)
		suite.Nil(err)
		suite.Equal(UpdatedValue, msg)

		suite.True(gock.IsDone())
	}
}

func (suite *SourceComparison_TestSuite) TestGetLocaleList() {
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

func (suite *SourceComparison_TestSuite) TestOldSourceIsEmpty() {
	defer gock.Off()

	resetInst(&RegisteredSource_Server_Config, func() { RegisterSource(name, version, []ComponentMsgs{suite.messages}) })

	EnableMockData("componentMessages-zh-Hans-sunglow")
	msg, err := GetTranslation().GetStringMessage(name, version, locale, ComponentToRegister, Key)
	suite.Nil(err)
	suite.Equal(Value, msg)
	msg, err = GetTranslation().GetStringMessage(name, version, locale, ComponentToRegister, UpdatedKey)
	suite.Nil(err)
	suite.Equal(OldZhValue, msg)

	suite.True(gock.IsDone())
}

func TestSourceComparison_TestSuite(t *testing.T) {
	suite.Run(t, new(SourceComparison_TestSuite))
}
