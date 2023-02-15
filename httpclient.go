/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"io/ioutil"
	"net/http"

	"github.com/pkg/errors"
)

const (
	servertimeout = 10
)

var (
	httpclient     *http.Client
	newHTTPRequest = http.NewRequest
)

func httpget(urlToGet string, header map[string]string, body *[]byte) (*http.Response, error) {
	logger.Info(fmt.Sprintf("Get URL %s with headers %v", urlToGet, header))

	req, err := newHTTPRequest(http.MethodGet, urlToGet, nil)
	if err != nil {
		return nil, errors.WithStack(err)
	}
	req.Close = true
	for k, v := range header {
		req.Header.Add(k, v)
	}
	resp, err := httpclient.Do(req)
	if err != nil {
		return nil, errors.WithStack(err)
	}
	defer resp.Body.Close()

	b, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return resp, errors.WithStack(err)
	}
	*body = b

	return resp, err
}
