/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"io/ioutil"
	"net/http"
)

const (
	servertimeout = 10
)

var (
	httpclient     *http.Client
	newHTTPRequest = http.NewRequest
)

func httpget(urlToGet string, header map[string]string, body *[]byte) (*http.Response, error) {
	logger.Info("URL to get is: " + urlToGet)

	req, err := newHTTPRequest(http.MethodGet, urlToGet, nil)
	if err != nil {
		return nil, err
	}
	req.Close = true
	for k, v := range header {
		req.Header.Add(k, v)
	}
	resp, err := httpclient.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	b, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return resp, err
	}
	*body = b

	return resp, err
}
