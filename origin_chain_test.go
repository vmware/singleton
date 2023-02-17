/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"errors"
	"sort"
	"testing"

	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/suite"
)

var mockedItemData = struct{}{}
var mockGetFunc = func(item *dataItem, returnError error) {
	if returnError == nil {
		item.data = mockedItemData
	}
}

var mockIsExpiredFunc = func(*dataItem, bool) {}

type MockedOriginForOriginChain struct {
	mock.Mock
}

func (m *MockedOriginForOriginChain) Get(item *dataItem) error {
	args := m.Called(item)
	returnValue := args.Error(0)
	item.origin = m
	item.attrs = newSingleCacheInfo()
	mockGetFunc(item, returnValue)
	return returnValue
}

func (m *MockedOriginForOriginChain) IsExpired(item *dataItem) bool {
	args := m.Called(item)
	returnValue := args.Bool(0)
	mockIsExpiredFunc(item, returnValue)
	return returnValue
}

type OriginChainTestSuite struct {
	suite.Suite
	messageOriginList
}

func (suite *OriginChainTestSuite) SetupSuite() {
	testObj1, testObj2 := new(MockedOriginForOriginChain), new(MockedOriginForOriginChain)
	suite.messageOriginList = messageOriginList{testObj1, testObj2}
}

func (suite *OriginChainTestSuite) TestGetComponentMessages() {
	tests := []struct {
		desc     string
		errs     []error
		maxIndex int
	}{
		{"return from first origin", []error{nil}, 0},
		{"return from second origin", []error{errors.New("first error messages"), nil}, 1},
		{"return an error", []error{errors.New("first error messages"), errors.New("second error messages")}, 1},
	}

	for _, test := range tests {
		item := &dataItem{id: componentID}
		for i := 0; i <= test.maxIndex; i++ {
			suite.messageOriginList[i].(*MockedOriginForOriginChain).On("Get", item).Once().Return(test.errs[i])
		}

		err := suite.messageOriginList.Get(item)
		suite.Equal(test.errs[test.maxIndex], err)
		if test.errs[test.maxIndex] == nil {
			suite.Equal(mockedItemData, item.data)
		} else {
			suite.Nil(item.data)
		}
		for _, obj := range suite.messageOriginList {
			obj.(*MockedOriginForOriginChain).AssertExpectations(suite.T())
		}
	}
}

func (suite *OriginChainTestSuite) TestGetList() {
	tests := []struct {
		desc             string
		errs             []error
		returnData       [][]string
		returnErrorIndex int
		expected         []string
	}{
		{"all origins are successful", []error{nil, nil}, [][]string{{localeSource, localeDefault}, {localeDefault}}, -1, []string{localeSource, localeDefault}},
		{"one origin is failed", []error{errors.New("first error messages"), nil}, [][]string{nil, {localeDefault}}, -1, []string{localeDefault}},
		{"all origins are failed", []error{errors.New("first error messages"), errors.New("second error messages")}, [][]string{nil, nil}, 1, nil},
		// {"all origins are successful but with empty result", []error{nil, nil}, [][]string{nil, nil}, -1, nil},
	}

	for _, test := range tests {

		item := &dataItem{id: componentsID}

		mockGetFunc = func() func(item *dataItem, returnErr error) {
			i := 0
			return func(item *dataItem, returnError error) {
				item.data = test.returnData[i]
				i++
			}
		}()

		for i, o := range suite.messageOriginList {
			o.(*MockedOriginForOriginChain).On("Get", item).Once().Return(test.errs[i])
		}

		err := suite.messageOriginList.Get(item)
		if test.returnErrorIndex < 0 {
			suite.Nil(err, test.desc)
			sort.Strings(item.data.([]string))
			suite.Equal(test.expected, item.data, test.desc)
		} else {
			suite.Equal(test.errs[test.returnErrorIndex], err, test.desc)
			suite.Nil(item.data, test.desc)
		}

		for _, obj := range suite.messageOriginList {
			obj.(*MockedOriginForOriginChain).AssertExpectations(suite.T())
		}
	}
}

func (suite *OriginChainTestSuite) TestIsExpired() {
	tests := []struct {
		desc              string
		expiredSlice      []bool
		itemOriginIndex   int
		returnOriginIndex int
		expected          bool
	}{
		{"item is from first origin and origins return [true,true]", []bool{true, true}, 0, 0, true},
		{"item is from first origin and origins return [false,false]", []bool{false, false}, 0, 0, false},
		{"item is from first origin and origins return [true,false]", []bool{true, false}, 0, 0, true},
		{"item is from first origin and origins return [false,true]", []bool{false, true}, 0, 0, false},
		{"item is from second origin and origins return [true,true]", []bool{true, true}, 1, 0, true},
		{"item is from second origin and origins return [false,false]", []bool{false, false}, 1, 1, false},
		{"item is from second origin and origins return [true,false]", []bool{true, false}, 1, 0, true},
		{"item is from second origin and origins return [false,true]", []bool{false, true}, 1, 1, true},
	}

	for _, test := range tests {
		item := &dataItem{id: componentID}

		mockIsExpiredFunc = func(item *dataItem, expired bool) {
			item.origin = suite.messageOriginList[test.itemOriginIndex]
		}

		for i := 0; i <= test.returnOriginIndex; i++ {
			suite.messageOriginList[i].(*MockedOriginForOriginChain).On("IsExpired", item).Once().Return(test.expiredSlice[i])
		}

		expired := suite.messageOriginList.IsExpired(item)
		suite.Equal(test.expected, expired, test.desc)

		for _, obj := range suite.messageOriginList {
			obj.(*MockedOriginForOriginChain).AssertExpectations(suite.T())
		}
	}
}

func TestOriginChainTestSuite(t *testing.T) {
	suite.Run(t, new(OriginChainTestSuite))
}
