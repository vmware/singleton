/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"sort"
	"testing"

	"github.com/pkg/errors"
	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/suite"
	"gopkg.in/h2non/gock.v1"
)

type MockedOrigin struct {
	mock.Mock
	GetFunc       func(item *dataItem, returnError error)
	IsExpiredFunc func(*dataItem, bool)
}

func (m *MockedOrigin) Get(item *dataItem) error {
	args := m.Called(item)
	returnValue := args.Error(0)
	item.origin = m
	item.attrs = newSingleCacheInfo()
	m.GetFunc(item, returnValue)
	return returnValue
}

func (m *MockedOrigin) IsExpired(item *dataItem) bool {
	args := m.Called(item)
	returnValue := args.Bool(0)
	m.IsExpiredFunc(item, returnValue)
	return returnValue
}

func NewMockedOrigin() *MockedOrigin {
	return &MockedOrigin{GetFunc: mockGetFunc, IsExpiredFunc: mockIsExpiredFunc}
}

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
		{"Get source locale without comparison", inst.cfg.GetSourceLocale(), ComponentToRegister, []string{}, 2, ""},
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

	expectedLocales := []string{"abc"}
	sourceOnly := &sourceComparison{source: &MockedOrigin{
		GetFunc: func(item *dataItem, returnError error) {
			if returnError == nil {
				item.data = expectedLocales
			} else {
				item.data = nil
			}
		}}}
	itemLocales := &dataItem{id: localesID}
	sourceOnly.source.(*MockedOrigin).On("Get", itemLocales).Once().Return(nil)
	err := sourceOnly.Get(itemLocales)
	suite.Nil(err)
	suite.Equal(expectedLocales, itemLocales.data)
	sourceOnly.source.(*MockedOrigin).AssertExpectations(suite.T())
}

func (suite *SourceComparisonTestSuite) TestGetComponentList() {
	mockedSource := MockedOrigin{}
	mockedTranslation := mockedSource
	sourceAndTranslation := &sourceComparison{&mockedSource, &mockedTranslation}

	var tests = []struct {
		desc       string
		returnData interface{}
		returnErr  error
	}{
		{"successful", []string{"abc"}, nil},
		{"failed", nil, errors.New("failed")},
	}
	itemComponents := &dataItem{id: componentsID}

	for _, test := range tests {
		sourceAndTranslation.source.(*MockedOrigin).GetFunc = func(item *dataItem, returnError error) {
			if returnError == nil {
				item.data = test.returnData
			} else {
				item.data = nil
			}
		}
		sourceAndTranslation.source.(*MockedOrigin).On("Get", itemComponents).Once().Return(test.returnErr)
		err := sourceAndTranslation.Get(itemComponents)
		suite.Equal(test.returnErr, err)
		suite.Equal(test.returnData, itemComponents.data)
		sourceAndTranslation.source.(*MockedOrigin).AssertExpectations(suite.T())
	}
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
	mockedSource := MockedOrigin{IsExpiredFunc: func(*dataItem, bool) {}}
	mockedTranslation := mockedSource
	sourceComparison := &sourceComparison{&mockedSource, &mockedTranslation}

	itemSourceComponent := &dataItem{id: enComponentID}
	itemComponent := &dataItem{id: componentID}
	itemComponents := &dataItem{id: componentsID}
	itemLocales := &dataItem{id: localesID}

	returnValue := true
	sourceComparison.source.(*MockedOrigin).On("IsExpired", itemSourceComponent).Once().Return(returnValue)
	expired := sourceComparison.IsExpired(itemSourceComponent)
	suite.Equal(returnValue, expired, "source expiration")
	sourceComparison.source.(*MockedOrigin).AssertExpectations(suite.T())

	sourceComparison.messageOrigin.(*MockedOrigin).On("IsExpired", itemComponent).Once().Return(returnValue)
	expired = sourceComparison.IsExpired(itemComponent)
	suite.Equal(returnValue, expired, "translation expiration")
	sourceComparison.messageOrigin.(*MockedOrigin).AssertExpectations(suite.T())

	sourceComparison.messageOrigin.(*MockedOrigin).On("IsExpired", itemComponents).Once().Return(returnValue)
	expired = sourceComparison.IsExpired(itemComponents)
	suite.Equal(returnValue, expired, "component list expiration")
	sourceComparison.messageOrigin.(*MockedOrigin).AssertExpectations(suite.T())

	sourceComparison.messageOrigin.(*MockedOrigin).On("IsExpired", itemLocales).Once().Return(returnValue)
	expired = sourceComparison.IsExpired(itemLocales)
	suite.Equal(returnValue, expired, "locale list expiration")
	sourceComparison.messageOrigin.(*MockedOrigin).AssertExpectations(suite.T())
}

func TestSourceComparisonTestSuite(t *testing.T) {
	suite.Run(t, new(SourceComparisonTestSuite))
}
