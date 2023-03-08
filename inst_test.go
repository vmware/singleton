/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestGetInst(t *testing.T) {

	resetInst(&testCfg, nil)
	// assert.Equal(t, testCfg.LocalBundles, inst.bundle.root)
	// TODO: Test bundle

	if len(testCfg.ServerURL) != 0 {
		assert.NotNil(t, inst.server)
	}

	// Verify translation manager
	assert.NotNil(t, inst.trans)

	s := inst.trans.(*transMgr).transInst.msgOrigin
	assert.NotNil(t, s)
	assert.NotNil(t, cache)
}

func TestCheckConfig(t *testing.T) {

	newCfg := testCfg
	newCfg.ServerURL, newCfg.LocalBundles = "", ""

	errString := originNotProvided
	err := checkConfig(&newCfg)
	assert.Equal(t, errString, err.Error())

	newCfg2 := testCfg
	newCfg2.DefaultLocale = ""
	errString2 := defaultLocaleNotProvided
	err2 := checkConfig(&newCfg2)
	assert.Equal(t, errString2, err2.Error())

	assert.PanicsWithError(t, originNotProvided, func() { Initialize(&newCfg) })
}

func TestGetTranslation(t *testing.T) {
	inst = nil

	assert.PanicsWithError(t, uninitialized, func() { GetTranslation() })
}

func TestSetHttpHeaders(t *testing.T) {
	inst = nil

	err := SetHTTPHeaders(nil)
	assert.Error(t, err)
}
