/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"errors"
	"io"
	"net/http"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestNewRequest(t *testing.T) {
	defer Trace(curFunName())()

	saved := newHTTPRequest
	defer func() { newHTTPRequest = saved }()

	errMsg := "TestNewRequest"
	newHTTPRequest = func(method, url string, body io.Reader) (*http.Request, error) {
		return nil, errors.New(errMsg)
	}

	urlToGet := "any url"
	respData := struct{}{}
	err := httpget(urlToGet, map[string]string{}, respData)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), errMsg)
}
