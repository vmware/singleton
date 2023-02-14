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

type MockedOrigin struct {
	mock.Mock
	messageOrigin
}

func (m *MockedOrigin) Get(item *dataItem) error {
	args := m.Called(item)
	item.data = NewMapComponentMsgs(map[string]string{}, localeEn, component)
	time.Sleep(time.Millisecond)
	return args.Error(0)
}

type SingleLoaderTestSuite struct {
	suite.Suite
	singleLoader *singleLoader
}

func (suite *SingleLoaderTestSuite) SetupSuite() {
	messages := NewMapComponentMsgs(RegisteredMap, localeEn, ComponentToRegister)
	resetInst(&OnlyRegisteredSourceConfig, func() { RegisterSource(name, version, []ComponentMsgs{messages}) })
	suite.singleLoader = GetTranslation().(*transMgr).Translation.(*transInst).msgOrigin.(*cacheService).messageOrigin.(*singleLoader)
}

func (suite *SingleLoaderTestSuite) TestConcurrentGet() {
	// create an instance of our test object
	testObj := new(MockedOrigin)
	singleLoader := &singleLoader{messageOrigin: testObj}

	id := dataItemID{iType: itemComponent, Name: name, Version: version, Locale: localeEn, Component: component}
	var data ComponentMsgs
	var returnError error
	var muData sync.Mutex

	// setup expectations
	testObj.On("Get", mock.AnythingOfType("*sgtn.dataItem")).Once().Return(nil)

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

			err := singleLoader.Get(&item)
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
	testObj.AssertExpectations(suite.T())
}

// func (suite *SingleLoaderTestSuite)  TestRefreshCache( ) {

// 	var tests = []struct {
// 		desc      string
// 		mocks     []string
// 		locale    string
// 		component string
// 		expected  int
// 		err       string
// 	}{
// 		{"RefreshCache", []string{"RefreshCache", "RefreshCacheSecondTime"}, "RefreshCache", "sunglow", 6, ""},
// 	}

// 	defer gock.Off()

// 	newCfg := testCfg
// 	newCfg.LocalBundles = ""
// 	resetInst(&newCfg, nil)
// 	trans := GetTranslation()
// 	for _, testData := range tests {
// 		EnableMockData(testData.mocks[0])
// 		item := &dataItem{dataItemID{itemComponent, name, version, testData.locale, testData.component}, nil, nil, nil}
// 		info := getCacheInfo(item)
// 		status := trans.(*transMgr).Translation.(*transInst).msgOrigin.(*cacheService).getStatus(item)
// 		info.setAge(100)

// 		// Get component messages first to populate cache
// 		messages, err := trans.GetComponentMessages(name, version, testData.locale, testData.component)
// 		if messages.(*MapComponentMsgs).Size() != testData.expected {
// 			t.Errorf("%s = %d, want %d", testData.desc, messages.(*MapComponentMsgs).Size(), testData.expected)
// 		}

// 		// Make sure mock data is consumed
// 		suite.True(gock.IsDone())
// 		gock.Clean()

// 		// Check the data in cache
// 		messagesInCache, found := cache.Get(dataItemID{itemComponent, name, version, testData.locale, testData.component})
// 		suite.True(found)
// 		suite.NotNil(messagesInCache)
// 		suite.Equal(testData.expected, messagesInCache.(*MapComponentMsgs).Size())

// 		// Getting before time out, no communication to server because mock is enabled
// 		messages, err = trans.GetComponentMessages(name, version, testData.locale, testData.component)
// 		suite.Nil(err)
// 		if messages.(*MapComponentMsgs).Size() != testData.expected {
// 			t.Errorf("%s = %d, want %d", testData.desc, messages.(*MapComponentMsgs).Size(), testData.expected)
// 		}

// 		// Enable mock, time out cache and fetch(refresh) again. This time the data is same as before
// 		EnableMockData(testData.mocks[1])
// 		expireCache(info, info.age)
// 		messages, err = trans.GetComponentMessages(name, version, testData.locale, testData.component)
// 		suite.Nil(err)
// 		suite.Equal(testData.expected, messages.(*MapComponentMsgs).Size())

// 		// Start the go routine of refreshing cache, and wait for finish. Data entry number changes to 7.
// 		time.Sleep(10 * time.Millisecond)
// 		status.waitUpdate()
// 		// Make sure mock data is consumed
// 		suite.True(gock.IsDone())

// 		// Check the data in cache
// 		messagesInCache, found = cache.Get(dataItemID{itemComponent, name, version, testData.locale, testData.component})
// 		suite.True(found)
// 		suite.Equal(7, messagesInCache.(ComponentMsgs).(*MapComponentMsgs).Size())
// 	}

// 	suite.True(gock.IsDone())
// }

func TestSingleLoaderTestSuite(t *testing.T) {
	suite.Run(t, new(SingleLoaderTestSuite))
}
