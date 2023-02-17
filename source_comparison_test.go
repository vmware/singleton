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

type SourceComparisonTestSuite struct {
	suite.Suite
	messages ComponentMsgs
}

func (suite *SourceComparisonTestSuite) SetupSuite() {
	suite.messages = NewMapComponentMsgs(RegisteredMap, localeEn, ComponentToRegister)
	resetInst(&RegisteredSourceServerConfig, func() { RegisterSource(name, version, []ComponentMsgs{suite.messages}) })
}

func (suite *SourceComparisonTestSuite) TestSourceComparison() {
	defer gock.Off()

	resetInst(&RegisteredSourceServerConfig, func() { RegisterSource(name, version, []ComponentMsgs{suite.messages}) })

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

func (suite *SourceComparisonTestSuite) TestGetLocaleList() {
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

func (suite *SourceComparisonTestSuite) TestGetComponentList() {
}

func (suite *SourceComparisonTestSuite) TestOldSourceIsEmpty() {
	defer gock.Off()

	resetInst(&RegisteredSourceServerConfig, func() { RegisterSource(name, version, []ComponentMsgs{suite.messages}) })

	EnableMockData("componentMessages-zh-Hans-sunglow")
	msg, err := GetTranslation().GetStringMessage(name, version, locale, ComponentToRegister, Key)
	suite.Nil(err)
	suite.Equal(Value, msg)
	msg, err = GetTranslation().GetStringMessage(name, version, locale, ComponentToRegister, UpdatedKey)
	suite.Nil(err)
	suite.Equal(OldZhValue, msg)

	suite.True(gock.IsDone())
}

func (suite *SourceComparisonTestSuite) TestIsExpired() {
	sourceComparison := &sourceComparison{&MockedOriginForOriginChain{}, &MockedOriginForOriginChain{}}

	itemSourceComponent := &dataItem{id: enComponentID}
	itemComponent := &dataItem{id: componentID}
	itemComponents := &dataItem{id: componentsID}
	itemLocales := &dataItem{id: localesID}

	returnValue := true
	sourceComparison.source.(*MockedOriginForOriginChain).On("IsExpired", itemSourceComponent).Once().Return(returnValue)
	expired := sourceComparison.IsExpired(itemSourceComponent)
	suite.Equal(returnValue, expired, "source expiration")
	sourceComparison.source.(*MockedOriginForOriginChain).AssertExpectations(suite.T())

	sourceComparison.messageOrigin.(*MockedOriginForOriginChain).On("IsExpired", itemComponent).Once().Return(returnValue)
	expired = sourceComparison.IsExpired(itemComponent)
	suite.Equal(returnValue, expired, "translation expiration")
	sourceComparison.messageOrigin.(*MockedOriginForOriginChain).AssertExpectations(suite.T())

	sourceComparison.messageOrigin.(*MockedOriginForOriginChain).On("IsExpired", itemComponents).Once().Return(returnValue)
	expired = sourceComparison.IsExpired(itemComponents)
	suite.Equal(returnValue, expired, "component list expiration")
	sourceComparison.messageOrigin.(*MockedOriginForOriginChain).AssertExpectations(suite.T())

	sourceComparison.messageOrigin.(*MockedOriginForOriginChain).On("IsExpired", itemLocales).Once().Return(returnValue)
	expired = sourceComparison.IsExpired(itemLocales)
	suite.Equal(returnValue, expired, "locale list expiration")
	sourceComparison.messageOrigin.(*MockedOriginForOriginChain).AssertExpectations(suite.T())
}

func TestSourceComparisonTestSuite(t *testing.T) {
	suite.Run(t, new(SourceComparisonTestSuite))
}
