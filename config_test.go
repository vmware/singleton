/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"io/ioutil"
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
)

type ConfigTestSuite struct {
	suite.Suite
	cfpath    string
	fileBytes []byte
}

func (suite *ConfigTestSuite) SetupSuite() {
	suite.cfpath = "testdata/conf/config.json"
	var err error
	suite.fileBytes, err = ioutil.ReadFile(suite.cfpath)
	assert.Nil(suite.T(), err)
}

func (suite *ConfigTestSuite) TestNewConfigNoFile() {

	cfPath := "doesn't exist"
	cfg, err := LoadConfig(cfPath)

	assert.Nil(suite.T(), cfg)
	assert.NotNil(suite.T(), err)
	logger.Debug(err.Error())
	assert.Contains(suite.T(), err.Error(), cfPath)
}

func TestConfigTestSuite(t *testing.T) {
	suite.Run(t, new(ConfigTestSuite))
}
