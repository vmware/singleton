/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"encoding/json"
	"fmt"
	"net/http"
)

const (
	servertimeout = 10
)

var (
	httpclient     *http.Client
	newHTTPRequest = http.NewRequest
)

func httpget(urlToGet string, header map[string]string, respData interface{}) error {
	logger.Info("URL to get is: " + urlToGet)

	req, err := newHTTPRequest(http.MethodGet, urlToGet, nil)
	if err != nil {
		return err
	}
	req.Close = true
	for k, v := range header {
		req.Header.Add(k, v)
	}
	resp, err := httpclient.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if !isSuccess(resp.StatusCode) {
		return fmt.Errorf("Getting failed, status code is: %s", resp.Status)
	}

	err = json.NewDecoder(resp.Body).Decode(respData)

	return err
}

func isSuccess(code int) bool {
	return code >= 200 && code < 300
}
