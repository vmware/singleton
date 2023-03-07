/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
)

type LocaleFallbackTestSuite struct {
	suite.Suite
}

func (suite *LocaleFallbackTestSuite) SetupSuite() {
}

func (suite *LocaleFallbackTestSuite) TestGetComponentMessages() {
	var expectedData ComponentMsgs
	expectedLocaleData := NewMapComponentMsgs(map[string]string{}, locale, component)
	expectedDefaultLocaleData := NewMapComponentMsgs(map[string]string{}, localeDefault, component)
	expectedSourceLocaleData := NewMapComponentMsgs(map[string]string{}, localeSource, component)
	item := &dataItem{id: componentID}
	itemDefaultLocale := &dataItem{id: defaultLocaleComponentID}
	itemSourceLocale := &dataItem{id: enComponentID}

	transMgr := newTransMgr(&transInst{&MockedOrigin{
		GetFunc: func(item *dataItem, returnError error) {
			if returnError == nil {
				item.data = expectedData
			} else {
				item.data = nil
			}
		}}}, []string{localeDefault, testCfg.SourceLocale})

	// Get a component without fallback
	expectedData = expectedLocaleData
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", item).Once().Return(nil)
	resultData, err := transMgr.getComponentMessages(name, version, locale, component)
	suite.Nil(err)
	suite.Equal(expectedData, resultData)
	transMgr.transInst.msgOrigin.(*MockedOrigin).AssertExpectations(suite.T())

	// fallback to the default locale
	expectedData = expectedDefaultLocaleData
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", item).Once().Return(assert.AnError)
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemDefaultLocale).Once().Return(nil)
	resultData, err = transMgr.getComponentMessages(name, version, locale, component)
	suite.Nil(err)
	suite.Equal(expectedData, resultData)
	transMgr.transInst.msgOrigin.(*MockedOrigin).AssertExpectations(suite.T())

	// fallback to the source locale
	expectedData = expectedSourceLocaleData
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", item).Once().Return(assert.AnError)
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemDefaultLocale).Once().Return(assert.AnError)
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemSourceLocale).Once().Return(nil)
	resultData, err = transMgr.getComponentMessages(name, version, locale, component)
	suite.Nil(err)
	suite.Equal(expectedData, resultData)
	transMgr.transInst.msgOrigin.(*MockedOrigin).AssertExpectations(suite.T())

	// return an error
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", item).Once().Return(assert.AnError)
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemDefaultLocale).Once().Return(assert.AnError)
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemSourceLocale).Once().Return(assert.AnError)
	resultData, err = transMgr.getComponentMessages(name, version, locale, component)
	suite.Equal(assert.AnError, err)
	suite.Nil(resultData)
	transMgr.transInst.msgOrigin.(*MockedOrigin).AssertExpectations(suite.T())

	// get default locale successfully
	expectedData = expectedDefaultLocaleData
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemDefaultLocale).Once().Return(nil)
	resultData, err = transMgr.getComponentMessages(name, version, localeDefault, component)
	suite.Nil(err)
	suite.Equal(expectedData, resultData)
	transMgr.transInst.msgOrigin.(*MockedOrigin).AssertExpectations(suite.T())

	// fallback from default locale to source locale
	expectedData = expectedSourceLocaleData
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemDefaultLocale).Once().Return(assert.AnError)
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemSourceLocale).Once().Return(nil)
	resultData, err = transMgr.getComponentMessages(name, version, localeDefault, component)
	suite.Nil(err)
	suite.Equal(expectedData, resultData)
	transMgr.transInst.msgOrigin.(*MockedOrigin).AssertExpectations(suite.T())

	// fallback from source locale to default locale
	expectedData = expectedDefaultLocaleData
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemDefaultLocale).Once().Return(nil)
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemSourceLocale).Once().Return(assert.AnError)
	resultData, err = transMgr.getComponentMessages(name, version, localeSource, component)
	suite.Nil(err)
	suite.Equal(expectedData, resultData)
	transMgr.transInst.msgOrigin.(*MockedOrigin).AssertExpectations(suite.T())

	// fallback from source locale to default locale and return an error
	expectedData = expectedDefaultLocaleData
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemDefaultLocale).Once().Return(assert.AnError)
	transMgr.transInst.msgOrigin.(*MockedOrigin).On("Get", itemSourceLocale).Once().Return(assert.AnError)
	resultData, err = transMgr.getComponentMessages(name, version, localeSource, component)
	suite.Equal(assert.AnError, err)
	suite.Nil(resultData)
	transMgr.transInst.msgOrigin.(*MockedOrigin).AssertExpectations(suite.T())
}

func TestLocaleFallbackTestSuite(t *testing.T) {
	suite.Run(t, new(LocaleFallbackTestSuite))
}
