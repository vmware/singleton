/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"io"
	"net/http"
	"testing"

	"github.com/pkg/errors"
	"github.com/stretchr/testify/assert"
)

func TestNewRequest(t *testing.T) {

	saved := newHTTPRequest
	defer func() { newHTTPRequest = saved }()

	errMsg := "TestNewRequest"
	newHTTPRequest = func(method, url string, body io.Reader) (*http.Request, error) {
		return nil, errors.New(errMsg)
	}

	urlToGet := "any url"
	_, err := httpget(urlToGet, map[string]string{}, nil)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), errMsg)
}
