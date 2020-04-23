/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestGetInst(t *testing.T) {
	defer Trace(curFunName())()

	resetInst(&testCfg)
	assert.Equal(t, testCfg.LocalBundles, inst.trans.ds.bundle.root)
	// TODO: Test bundle

	if len(testCfg.ServerURL) != 0 {
		assert.NotNil(t, inst.trans.ds.server)
	}

	// Verify translation manager
	assert.NotNil(t, inst.trans)

	// Verify data service
	dataService := inst.trans.ds
	assert.NotNil(t, dataService)
	assert.NotNil(t, cache)
	assert.NotNil(t, cacheInfoMap)
}

func TestCheckConfig(t *testing.T) {
	defer Trace(curFunName())()

	newCfg := testCfg
	newCfg.ServerURL, newCfg.LocalBundles = "", ""

	errString := "Neither online_service_url nor offline_resources_base_url is provided"
	err := checkConfig(&newCfg)
	assert.Equal(t, errString, err.Error())
}
