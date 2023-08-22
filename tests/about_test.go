/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"testing"

	"sgtnserver/api"
	_ "sgtnserver/api/v2/about"

	"github.com/stretchr/testify/assert"
)

const aboutURL = BaseURL + "/about/version"

func TestAboutInfo(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	tests := []struct {
		desc, productName, version, wanted string
	}{
		{"normal", Name, Version,
			`{"response":{"code":200,"message":"OK"},"data":{"bundle":{"changeId":"drop_id-222","productName":"VPE","version":"1.0.0"},"service":{"name":"singleton-i18n","version":"1.0.0","author":"John","createdBy":"Robot","buildDate":"04/08/2023","changeId":"93afa26a7c0616ff7fee3eb25318342d1dc8975b"}}}`},
		{"version doesn't exist", Name, "0.0.0.1",
			`{"response":{"code":207,"message":"Successful Partially"},"data":{"service":{"name":"singleton-i18n","version":"1.0.0","author":"John","createdBy":"Robot","buildDate":"04/08/2023","changeId":"93afa26a7c0616ff7fee3eb25318342d1dc8975b"}}}`},
	}

	for _, tt := range tests {
		tt := tt
		t.Run(tt.desc, func(t *testing.T) {
			req := e.GET(aboutURL, tt.productName, tt.version)
			req.WithQuery(api.ProductNameAPIKey, tt.productName).WithQuery(api.VersionAPIKey, tt.version)
			resp := req.Expect()

			assert.JSONEq(t, tt.wanted, resp.Body().Raw())
		})
	}
}
