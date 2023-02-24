/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"testing"

	"github.com/stretchr/testify/suite"
)

type SourceInTranslationTestSuite struct {
	suite.Suite
}

func (suite *SourceInTranslationTestSuite) SetupSuite() {
	newCfg := testCfg
	newCfg.ServerURL, newCfg.LocalSourceBundle = "", ""
	resetInst(&newCfg, nil)
}

func (suite *SourceInTranslationTestSuite) TestGetComponentMessages() {
	messages, err := GetTranslation().GetComponentMessages(name, version, inst.cfg.GetSourceLocale(), component)
	suite.Nil(err)
	suite.Equal(4, messages.Size())
	suite.IsType(&sourceInTranslation{}, getCachedItem(enComponentID).origin)
	message, err := GetTranslation().GetStringMessage(name, version, inst.cfg.GetSourceLocale(), component, key)
	suite.Nil(err)
	suite.Equal("Message-latest", message)
}

func (suite *SourceInTranslationTestSuite) TestGetComponentList() {
	components, err := GetTranslation().GetComponentList(name, version)
	suite.Nil(err)
	suite.Equal(2, len(components))
	suite.IsType(&sourceInTranslation{}, getCachedItem(componentsID).origin)
}

func TestSourceInTranslationTestSuite(t *testing.T) {
	suite.Run(t, new(SourceInTranslationTestSuite))
}
