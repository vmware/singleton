/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/suite"
)

var mockSourceGetComponentListFunc func(name, version string, components []string, err error)
var mockSourceGetComponentMessagesFunc func(name, version, component string, componentMsgs ComponentMsgs, err error)

type MockedSource struct {
	mock.Mock
}

func (m *MockedSource) GetComponentList(name, version string) ([]string, error) {
	args := m.Called(name, version)
	components, err := args.Get(0).([]string), args.Error(0)
	mockSourceGetComponentListFunc(name, version, components, err)
	return components, err

}

func (m *MockedSource) GetComponentMessages(name, version, component string) (ComponentMsgs, error) {
	args := m.Called(name, version, component)
	componentMsgs, err := args.Get(0).(ComponentMsgs), args.Error(0)
	mockSourceGetComponentMessagesFunc(name, version, component, componentMsgs, err)
	return componentMsgs, err
}

type SourceAsOriginTestSuite struct {
	suite.Suite
	source
	messageOrigin
}

func (suite *SourceAsOriginTestSuite) SetupSuite() {
	messages := NewMapComponentMsgs(RegisteredMap, localeEn, ComponentToRegister)
	RegisterSource(name, version, []ComponentMsgs{messages})
	suite.source = &MockedSource{}
	suite.messageOrigin = sourceAsOrigin{suite.source}
}


// func TestSourceAsOriginTestSuite(t *testing.T) {
// 	suite.Run(t, new(SourceAsOriginTestSuite))
// }
