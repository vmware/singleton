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

	testInst := resetInst(&testCfg)
	assert.Equal(t, testCfg.OfflineResourcesBaseURL, testInst.trans.ds.bundle.root)
	// TODO: Test bundle

	if len(testCfg.OnlineServiceURL) != 0 {
		assert.NotNil(t, testInst.trans.ds.server)
	}

	// Verify translation manager
	assert.NotNil(t, testInst.trans)

	// Verify data service
	dataService := testInst.trans.ds
	assert.NotNil(t, dataService)
	assert.NotNil(t, dataService.cache)
	assert.NotNil(t, cacheInfoMap)
}

func TestCheckConfig(t *testing.T) {
	defer Trace(curFunName())()

	newCfg := testCfg
	newCfg.OnlineServiceURL, newCfg.OfflineResourcesBaseURL = "", ""

	errString := "Both online_service_url and offline_resources_base_url are empty"
	err := checkConfig(&newCfg)
	assert.Equal(t, errString, err.Error())
}
