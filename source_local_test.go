/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"testing"

	"github.com/stretchr/testify/suite"
)

type LocalSourceTestSuite struct {
	suite.Suite
}

func (suite *LocalSourceTestSuite) SetupSuite() {
	newCfg := testCfg
	newCfg.ServerURL = ""
	newCfg.LocalBundles = ""
	newCfg.LocalSourceBundle = LocalSourceBundle
	resetInst(&newCfg, nil)
}

func (suite *LocalSourceTestSuite) TestGetComponentMessages() {
	messages, err := GetTranslation().GetComponentMessages(name, version, inst.cfg.SourceLocale, component)
	suite.Nil(err)
	suite.Equal(4, messages.Size())
}

func (suite *LocalSourceTestSuite) TestGetComponentList() {
	components, err := GetTranslation().GetComponentList(name, version)
	suite.Nil(err)
	suite.Equal(2, len(components))
}

func TestLocalSourceTestSuite(t *testing.T) {
	suite.Run(t, new(LocalSourceTestSuite))
}
