/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"errors"
	"net"
	"net/http"
	"os"
	"strings"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/go-http-utils/headers"

	"sgtnserver/api"
	"sgtnserver/internal/sgtnerror"
)

func TestTraceIDs(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	// Test outside trace IDs
	req := e.GET(GetBundleURL, Name, Version, "zh-Hans", "sunglow")
	resp := req.WithHeader("abc", "abc trace ID").WithHeader("123", "123 trace ID").Expect()
	resp.Status(http.StatusOK)
}

func TestRecovery(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	url1 := "/TestRecovery"
	GinTestEngine.GET(url1, func(c *gin.Context) {
		panic(url1)
	})
	e.GET(url1).Expect()

	oldMode := gin.Mode()
	gin.SetMode(gin.DebugMode)
	defer gin.SetMode(oldMode)
	url2 := "/TestRecovery_broken_pipeline"
	GinTestEngine.GET(url2, func(c *gin.Context) {
		err := &net.OpError{Err: &os.SyscallError{Err: errors.New("broken pipe")}}
		panic(err)
	})

	e.GET(url2).Expect().Status(http.StatusOK)
}

func TestCompressResponse(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	// Test Gzip
	resp := e.GET(GetBundleURL, Name, Version, "zh-Hans", "sunglow").WithHeader(headers.AcceptEncoding, api.CompressionGzip).Expect()
	resp.ContentEncoding(api.CompressionGzip)

	// Test br
	resp = e.GET(GetBundleURL, Name, Version, "zh-Hans", "sunglow").WithHeader(headers.AcceptEncoding, api.CompressionBrotli).Expect()
	resp.ContentEncoding(api.CompressionBrotli)
}

func TestAbortWithError(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)
	resp := e.GET(GetBundleURL, Name, Version, "zh-Hans").Expect()
	resp.Status(http.StatusBadRequest)
	resp.Body().Contains("component")
}

func TestAllFailed(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, d := range []struct {
		locales, components string
	}{
		{"zh-Invalid,en-Invalid", "sunglow"},
	} {
		resp := e.GET(GetBundlesURL, Name, Version).
			WithQuery("locales", d.locales).WithQuery("components", d.components).Expect()
		resp.Status(http.StatusNotFound)
		for _, v := range strings.Split(d.locales, ",") {
			resp.Body().Contains(v)
		}
	}
}

func TestPartialSuccess(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, d := range []struct {
		testName            string
		locales, components string
		wantedCode          int
	}{
		{testName: "Partial Successful", locales: "zh-Hans,en-Invalid", components: "sunglow", wantedCode: sgtnerror.StatusPartialSuccess.Code()},
		{testName: "All Successful", locales: "zh-Hans,en", components: "sunglow", wantedCode: http.StatusOK},
		{testName: "All Failed", locales: "zh-Hans,en", components: "invalidComponent", wantedCode: http.StatusNotFound},
	} {
		d := d
		t.Run(d.testName, func(t *testing.T) {
			resp := e.GET(GetBundlesURL, Name, Version).WithQuery("locales", d.locales).
				WithQuery("components", d.components).Expect()
			resp.Status(d.wantedCode)
		})
	}
}

func TestAllowList(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)
	productName := "not-found"
	resp := e.GET(GetBundleURL, productName, Version, "zh-Hans", "sunglow").Expect()
	resp.Status(http.StatusBadRequest)
	resp.Body().Contains("doesn't exist")
}

func TestEtag(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	resp := e.GET(GetBundleURL, Name, Version, Locale, Component).Expect()
	resp.Status(http.StatusOK)
	log.Info(resp.Header(headers.ETag).Raw())
	resp.Header(headers.ETag).Equal(`"405-42766a785b0558578b5a0890774007a34e5c91ef"`)

	// Send request again to test Etag
	req := e.GET(GetBundleURL, Name, Version, Locale, Component)
	resp = req.WithHeader(headers.IfNoneMatch, resp.Header(headers.ETag).Raw()).Expect()
	resp.Status(http.StatusNotModified)
	resp.Body().Empty()
}
