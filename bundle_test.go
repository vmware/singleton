/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"os"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestBundleGetComponentList(t *testing.T) {

	newCfg := testCfg
	newCfg.ServerURL = ""
	resetInst(&newCfg)

	comps, err := inst.trans.GetComponentList(name, version)

	assert.Nil(t, err)
	assert.Equal(t, 2, len(comps))
}

func TestBundleGetLocaleList(t *testing.T) {

	newCfg := testCfg
	newCfg.ServerURL = ""
	resetInst(&newCfg)

	locales, err := inst.trans.GetLocaleList(name, version)
	logger.Debug(fmt.Sprintf("%#v\n", locales))
	assert.Nil(t, err)
	assert.Equal(t, 16, len(locales))
}

func TestBundleGetCompMessages(t *testing.T) {

	newCfg := testCfg
	newCfg.ServerURL = ""
	resetInst(&newCfg)

	locale := "fr"
	component := "sunglow"
	msgs, err := inst.trans.GetComponentMessages(name, version, locale, component)
	assert.Nil(t, err)
	assert.Equal(t, 4, msgs.(*defaultComponentMsgs).Size())
}

func TestBundleDirNonexistent(t *testing.T) {
	newCfg := testCfg
	newCfg.LocalBundles = "Path Not Exist"
	resetInst(&newCfg)

	_, err := inst.trans.GetComponentList(name, version)
	_, ok := err.(*os.PathError)
	assert.True(t, ok, "error isn't an PATH error: %s", err)
}
