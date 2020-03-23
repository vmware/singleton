/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"net/http"
	"net/url"
	"testing"

	"github.com/pkg/errors"
	"github.com/stretchr/testify/assert"
)

func TestGetLocaleCompAbnormal(t *testing.T) {
	defer Trace(curFunName())()

	saved := getDataFromServer
	defer func() { getDataFromServer = saved }()

	errMsg := "TestGetLocaleCompAbnormal"
	getDataFromServer = func(u *url.URL, header map[string]string, data interface{}) (*http.Response, error) {
		return nil, errors.New(errMsg)
	}

	newCfg := testCfg
	newCfg.OfflineResourcesBaseURL = ""
	testInst := resetInst(&newCfg)

	trans := testInst.GetTranslation()

	components, errcomp := trans.GetComponentList(name, version)
	assert.Nil(t, components)
	assert.Contains(t, errcomp.Error(), errMsg)

	components, errcomp = trans.GetComponentList(name, version)
	assert.Nil(t, components)
	assert.Contains(t, errcomp.Error(), errMsg)

	locales, errlocale := trans.GetLocaleList(name, version)
	assert.Nil(t, locales)
	assert.Contains(t, errlocale.Error(), errMsg)

	locales, errlocale = trans.GetLocaleList(name, version)
	assert.Nil(t, locales)
	assert.Contains(t, errlocale.Error(), errMsg)

	// clearCache(testInst)
	// errinit := testInst.InitializeCache()
	// assert.Contains(t, errinit.Error(), errMsg)

	// // Test return nil when disabling cache
	// testInst.cfg.EnableCache = false
	// errInit := testInst.InitializeCache()
	// assert.Nil(t, errInit)
}
