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
	if m.GetFunc != nil {
		m.GetFunc(item, returnValue)
	}
	return returnValue
}

func (m *MockedOrigin) IsExpired(item *dataItem) bool {
	args := m.Called(item)
	returnValue := args.Bool(0)
	if m.IsExpiredFunc != nil {
		m.IsExpiredFunc(item, returnValue)
	}
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

	EnableMultipleMockData([]string{"componentMessages-zh-Hans-sunglow", "componentMessages-en-sunglow"})

	var tests = []struct {
		desc                                  string
		locale, component, key, expectedValue string
		expectedSize                          int
	}{
		{"a new key is added", locale, ComponentToRegister, RegisteredKey, RegisteredValue, 8},
		{"a key is updated", locale, ComponentToRegister, UpdatedKey, UpdatedValue, 8},
		{"a key is upchanged", locale, ComponentToRegister, UnchangedKey, "测试一个参数{0}", 8},
		{"source locale without comparison", inst.cfg.GetSourceLocale(), ComponentToRegister, UnchangedKey, UnchangedValue, len(RegisteredMap)},
	}

	for _, testData := range tests {

		messages, _ := GetTranslation().GetComponentMessages(name, version, testData.locale, testData.component)
		suite.Equal(testData.expectedSize, messages.Size())

		msg, err := GetTranslation().GetStringMessage(name, version, testData.locale, testData.component, testData.key)
		suite.Nil(err)
		suite.Equal(testData.expectedValue, msg)
	}
	suite.True(gock.IsDone())
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
	sourceOnly := newSourceComparison(&MockedOrigin{
		GetFunc: func(item *dataItem, returnError error) {
			if returnError == nil {
				item.data = expectedLocales
			} else {
				item.data = nil
			}
		}}, nil)
	itemLocales := &dataItem{id: localesID}
	sourceOnly.source.(*MockedOrigin).On("Get", itemLocales).Once().Return(nil)
	err := sourceOnly.Get(itemLocales)
	suite.Nil(err)
	suite.Equal(expectedLocales, itemLocales.data)
	sourceOnly.source.(*MockedOrigin).AssertExpectations(suite.T())
}

func (suite *SourceComparisonTestSuite) TestGetComponentList() {
	sourceAndTranslation := newSourceComparison(&MockedOrigin{}, &MockedOrigin{})

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

func (suite *SourceComparisonTestSuite) TestNewSourceIsEmpty() {
	item := &dataItem{id: componentID}
	sourceAndTranslation := newSourceComparison(&bundleDAO{testCfg.LocalBundles}, &MockedOrigin{})
	sourceAndTranslation.source.(*MockedOrigin).On("Get", mock.AnythingOfType("*sgtn.dataItem")).Once().Return(errors.New("failed"))

	err := sourceAndTranslation.Get(item)
	suite.Nil(err)
	message, _ := item.data.(ComponentMsgs).Get(key)
	suite.Equal("消息", message)
	sourceAndTranslation.source.(*MockedOrigin).AssertExpectations(suite.T())
}

func (suite *SourceComparisonTestSuite) TestOldSourceIsEmpty() {
	defer gock.Off()

	resetInst(&RegisteredSourceServerConfig, func() { RegisterSource(name, version, []ComponentMsgs{suite.messages}) })

	EnableMockData("componentMessages-zh-Hans-sunglow")
	msg, err := GetTranslation().GetStringMessage(name, version, locale, ComponentToRegister, RegisteredKey)
	suite.Nil(err)
	suite.Equal(RegisteredValue, msg)
	msg, err = GetTranslation().GetStringMessage(name, version, locale, ComponentToRegister, UpdatedKey)
	suite.Nil(err)
	suite.Equal(OldZhValue, msg)

	suite.True(gock.IsDone())
}

func (suite *SourceComparisonTestSuite) TestIsExpired() {
	sourceComparison := newSourceComparison(&MockedOrigin{}, &MockedOrigin{})

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
