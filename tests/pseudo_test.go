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
)

func TestPseudoMultipleBundles(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, d := range []struct {
		locales, components, wanted string
		pseudo                      bool
		code                        int
	}{
		{"zh-Hans", "sunglow",
			`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locales":["zh-Hans"],"components":["sunglow"],"bundles":[{"component":"sunglow","locale":"zh-Hans","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}"}}]}}`,
			false, http.StatusOK},
		{"zh-Hans", "sunglow",
			`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locales":["latest"],"components":["sunglow"],"pseudo":true,"bundles":[{"component":"sunglow","locale":"latest","messages":{"plural.files":"#@{files, plural,one {category one : There is one file on {place}.}other {category other : There are # files on {place}.}}#@","message":"#@Message-en#@","pagination":"#@{0} - {1} of {2} customers#@","one.arg":"#@test one argument {0}#@"}}]}}`,
			true, http.StatusOK},
		{"xxx", "",
			`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locales":["latest"],"components":["sunglow","users"],"pseudo":true,"bundles":[{"component":"sunglow","locale":"latest","messages":{"plural.files":"#@{files, plural,one {category one : There is one file on {place}.}other {category other : There are # files on {place}.}}#@","message":"#@Message-en#@","pagination":"#@{0} - {1} of {2} customers#@","one.arg":"#@test one argument {0}#@"}},{"component":"users","locale":"latest","messages":{"Singleton.description":"#@{0} is common lib developed by Singleton team.#@","plural.files":"#@{0, plural,one {There is a file on \"{1}\".}other {There are # files on \"{1}\".}}#@","plural.reserved.character":"#@{0, plural,one {This is sharp '#'.}other {There are # sharp '#'.}}#@"}}]}}`,
			true, http.StatusOK},
	} {
		d := d
		t.Run(fmt.Sprintf("%v:%v:pseudo(%v)", d.locales, d.components, d.pseudo), func(t *testing.T) {
			resp := e.GET(GetBundlesURL, Name, Version).
				WithQuery("locales", d.locales).WithQuery("components", d.components).WithQuery("pseudo", d.pseudo).Expect()
			resp.Status(d.code)
			assert.JSONEq(t, d.wanted, resp.Body().Raw())
		})
	}
}

func TestPseudoSingleBundle(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	req := e.GET(GetBundleURL, Name, Version, "zh-Hans", "sunglow").WithQuery("pseudo", true)
	resp := req.Expect()
	expected := `{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locale":"latest","component":"sunglow","pseudo":true,"messages":{"plural.files":"#@{files, plural,one {category one : There is one file on {place}.}other {category other : There are # files on {place}.}}#@","message":"#@Message-en#@","pagination":"#@{0} - {1} of {2} customers#@","one.arg":"#@test one argument {0}#@"}}}`
	resp.Status(http.StatusOK)
	assert.JSONEq(t, expected, resp.Body().Raw())
}

func TestPseudoSingleString(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, d := range []struct {
		locale, key, source string
		pseudo              bool
		wanted              string
		code                int
	}{
		{"zh-Hans", "one.arg", "", true,
			`{"response":{"code":200,"message":"OK"},"data":{"component":"sunglow","key":"one.arg","locale":"latest","productName":"VPE","pseudo":true,"source":"","status":"The pseudo translation is found","translation":"#@test one argument {0}#@","version":"1.0.0"}}`,
			http.StatusOK},
		{"zh-Hans", "nonexistent", "", true,
			`{"response":{"code":200,"message":"OK"},"data":{"component":"sunglow","key":"nonexistent","locale":"latest","productName":"VPE","pseudo":true,"source":"","status":"The pseudo translation is not found, return the received source with pseudo tag","translation":"@@@@","version":"1.0.0"}}`,
			http.StatusOK},
		{"zh-Hans", "nonexistent", "xxx", true,
			`{"response":{"code":200,"message":"OK"},"data":{"component":"sunglow","key":"nonexistent","locale":"latest","productName":"VPE","pseudo":true,"source":"xxx","status":"The pseudo translation is not found, return the received source with pseudo tag","translation":"@@xxx@@","version":"1.0.0"}}`,
			http.StatusOK},
	} {
		d := d
		t.Run(fmt.Sprintf("%v:%v", d.locale, d.pseudo), func(t *testing.T) {
			resp := e.GET(GetKeyURL, Name, Version, d.locale, Component, d.key).WithQuery("source", d.source).WithQuery("pseudo", d.pseudo).Expect()
			resp.Status(d.code)
			assert.JSONEq(t, d.wanted, resp.Body().Raw())
		})
	}
}
