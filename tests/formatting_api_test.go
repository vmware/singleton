/*
 * Copyright 2024 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"net/http"
	"testing"

	_ "sgtnserver/api/v2/formatting"

	"github.com/stretchr/testify/assert"
)

const LocalizedNumberURL = BaseURL + "/formatting/number/localizedNumber"

func TestLocalizedNumber(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	data := []struct {
		desc       string
		locale     string
		number     string
		scale      int
		expected   string
		wantedCode int
	}{
		{desc: "char number in the first group is equal to group length, scale and actual decimal part is 0, negative number", number: "-123456789", scale: 0, locale: "en-US", expected: `{"response":{"code":200,"message":"OK"},"data":{"locale":"en","number":"-123456789","scale":"0","formattedNumber":"-123,456,789"}}`, wantedCode: http.StatusOK},
		{desc: "char number in the first group is less than group length, scale is large than actual", number: "23456789.123456", scale: 10, locale: "en-US", expected: `{"response":{"code":200,"message":"OK"},"data":{"locale":"en","number":"23456789.123456","scale":"10","formattedNumber":"23,456,789.1234560000"}}`, wantedCode: http.StatusOK},
		{desc: "the first group is the only group, scale is less than actual", number: "789.123456", scale: 2, locale: "en-US", expected: `{"response":{"code":200,"message":"OK"},"data":{"locale":"en","number":"789.123456","scale":"2","formattedNumber":"789.12"}}`, wantedCode: http.StatusOK},
		{desc: "the first group is the only group and length is less than group length, scale is 0", number: "89.123456", scale: 0, locale: "en-US", expected: ` {"response":{"code":200,"message":"OK"},"data":{"locale":"en","number":"89.123456","scale":"0","formattedNumber":"89"}}`, wantedCode: http.StatusOK},
		{desc: "the first group is the only group and length is less than group length, decimal part is 0, scale is 2, negative number", number: "-89", scale: 2, locale: "en-US", expected: `{"response":{"code":200,"message":"OK"},"data":{"locale":"en","number":"-89","scale":"2","formattedNumber":"-89.00"}}`, wantedCode: http.StatusOK},
		{desc: "integer part is zero", number: "0", scale: 2, locale: "en-US", expected: `{"response":{"code":200,"message":"OK"},"data":{"locale":"en","number":"0","scale":"2","formattedNumber":"0.00"}}`, wantedCode: http.StatusOK},

		{desc: "locale 'bn', char number in the first group is equal to group length, scale and actual decimal part is 0, negative number", number: "-123456789", scale: 0, locale: "bn", expected: `{"response":{"code":200,"message":"OK"},"data":{"locale":"bn","number":"-123456789","scale":"0","formattedNumber":"-12,34,56,789"}}`, wantedCode: http.StatusOK},
		{desc: "locale 'bn', char number in the first group is less than group length, scale is large than actual, negative number", number: "-23456789.123456", scale: 10, locale: "bn", expected: `{"response":{"code":200,"message":"OK"},"data":{"locale":"bn","number":"-23456789.123456","scale":"10","formattedNumber":"-2,34,56,789.1234560000"}}`, wantedCode: http.StatusOK},
		{desc: "locale 'bn', the first group is the only group", number: "789", scale: 2, locale: "bn", expected: `{"response":{"code":200,"message":"OK"},"data":{"locale":"bn","number":"789","scale":"2","formattedNumber":"789.00"}}`, wantedCode: http.StatusOK},
	}

	for _, d := range data {
		d := d

		t.Run(d.desc, func(t *testing.T) {
			resp := e.GET(LocalizedNumberURL, Name, Version).WithQuery("locale", d.locale).WithQuery("number", d.number).WithQuery("scale", d.scale).Expect()
			resp.Status(d.wantedCode)
			assert.JSONEq(t, d.expected, resp.Body().Raw())
		})
	}
}
