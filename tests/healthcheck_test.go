/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"fmt"
	"net/http"
	"testing"

	"github.com/stretchr/testify/assert"

	_ "sgtnserver/api/actuator"
)

func TestActuator(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, d := range []struct {
		endpoint string
		code     int
		wanted   string
	}{
		{"health", http.StatusOK, `{"status":"UP"}`},
		// {"info", http.StatusOK, `{"app":{"version":"","env":"","name":""},"git":{"branch":"","url":"","hostName":"","username":"","buildStamp":"","commitAuthor":"","commitId":"","commitTime":""},"runtime":{"arch":"amd64","os":"darwin","port":0,"runtimeVersion":"go1.18.3"}}`},
	} {
		d := d
		t.Run(fmt.Sprintf("endpoint:%v", d.endpoint), func(t *testing.T) {
			resp := e.GET("/actuator/{endpoint}", d.endpoint).Expect()
			resp.Status(d.code)
			assert.JSONEq(t, d.wanted, resp.Body().Raw())
		})
	}
}
