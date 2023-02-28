/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"sync"
	"testing"
	"time"

	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/suite"
)

type MockedOriginForSingleLoader struct {
	mock.Mock
	messageOrigin
}

func (m *MockedOriginForSingleLoader) Get(item *dataItem) error {
	args := m.Called(item)
	item.data = NewMapComponentMsgs(map[string]string{}, localeEn, component)
	return args.Error(0)
}

type SingleLoaderTestSuite struct {
	suite.Suite
	singleLoader *singleLoader
	testObj      *MockedOriginForSingleLoader
}

func (suite *SingleLoaderTestSuite) SetupSuite() {
	// create an instance of our test object
	suite.testObj = new(MockedOriginForSingleLoader)
	suite.singleLoader = &singleLoader{messageOrigin: suite.testObj}
}

func (suite *SingleLoaderTestSuite) TestConcurrentGet() {

	id := dataItemID{iType: itemComponent, Name: name, Version: version, Locale: localeEn, Component: component}
	var data ComponentMsgs
	var returnError error
	var muData sync.Mutex

	// setup expectations
	suite.testObj.On("Get", mock.AnythingOfType("*sgtn.dataItem")).Once().Return(nil).After(time.Millisecond)

	// call the code we are testing
	loopCount := 100

	var startGroup, finishGroup sync.WaitGroup
	startGroup.Add(loopCount)
	finishGroup.Add(loopCount)
	for i := 0; i < loopCount; i++ {
		go func(i int) {
			defer finishGroup.Done()

			item := dataItem{id: id, data: i} //i is to identify routines

			startGroup.Done()
			startGroup.Wait() // wait for all routines started

			err := suite.singleLoader.Get(&item)
			muData.Lock()
			if data == nil {
				returnError = err
				data = item.data.(ComponentMsgs)
			} else {
				suite.Equal(returnError, err)
				suite.Equal(data, item.data)
			}
			muData.Unlock()
		}(i)
	}
	finishGroup.Wait()

	// assert that the expectations were met
	suite.testObj.AssertExpectations(suite.T())
}

func TestSingleLoaderTestSuite(t *testing.T) {
	suite.Run(t, new(SingleLoaderTestSuite))
}
