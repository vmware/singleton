/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestBundleGetComponentList(t *testing.T) {
	defer Trace(curFunName())()

	newCfg := testCfg
	newCfg.OnlineServiceURL = ""
	testInst := resetInst(&newCfg)

	comps, err := testInst.trans.GetComponentList(name, version)

	assert.Nil(t, err)
	assert.Equal(t, 2, len(comps))
}

func TestBundleGetLocaleList(t *testing.T) {
	defer Trace(curFunName())()

	newCfg := testCfg
	newCfg.OnlineServiceURL = ""
	testInst := resetInst(&newCfg)

	locales, err := testInst.trans.GetLocaleList(name, version)
	logger.Debug(fmt.Sprintf("%#v\n", locales))
	assert.Nil(t, err)
	assert.Equal(t, 16, len(locales))
}

func TestBundleGetCompMessages(t *testing.T) {
	defer Trace(curFunName())()

	newCfg := testCfg
	newCfg.OnlineServiceURL = ""
	testInst := resetInst(&newCfg)

	locale := "fr"
	component := "sunglow"
	msgs, err := testInst.trans.GetComponentMessages(name, version, locale, component)
	assert.Nil(t, err)
	assert.Equal(t, 4, msgs.(*defaultComponentMsgs).Size())
}
