/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"errors"
	"fmt"
	"io/ioutil"
	"net"
	"net/http"
	"os"
	"strings"
	"testing"

	"github.com/emirpasic/gods/sets/hashset"
	"github.com/gin-gonic/gin"
	"github.com/go-http-utils/headers"
	"github.com/stretchr/testify/assert"

	"sgtnserver/api"
	"sgtnserver/internal/common"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
)

func TestTraceIDs(t *testing.T) {
	oldTraceIDs := config.Settings.HeaderOfTraceID
	defer func() {
		config.Settings.HeaderOfTraceID = oldTraceIDs
	}()

	tests := []struct {
		logFile         string
		traceIDsSetting string
		headers         map[string]string
	}{
		{
			logFile:         logFolder + RandomString(4) + "_temp.log",
			traceIDsSetting: "abc,123,edf",
			headers:         map[string]string{"abc": "first trace ID", "123": "second trace ID", "edf": "third traceID"},
		},
		{
			logFile:         logFolder + RandomString(4) + "_temp.log",
			traceIDsSetting: "abc,123,edf",
			headers:         map[string]string{"abc": "first trace ID", "123": "second trace ID"},
		},
		{
			logFile:         logFolder + RandomString(4) + "_temp.log",
			traceIDsSetting: "abc,123,edf",
			headers:         map[string]string{"abc": "first trace ID", "XXX": "other trace ID"},
		},
		{
			logFile:         logFolder + RandomString(4) + "_temp.log",
			traceIDsSetting: "abc,123,edf",
			headers:         map[string]string{"XXX": "other trace ID"},
		},
		{
			logFile:         logFolder + RandomString(4) + "_temp.log",
			traceIDsSetting: "abc,123,edf",
			headers:         map[string]string{},
		},
		{
			logFile:         logFolder + RandomString(4) + "_temp.log",
			traceIDsSetting: "abc",
			headers:         map[string]string{"abc": "first trace ID", "123": "second trace ID"},
		},
		{
			logFile:         logFolder + RandomString(4) + "_temp.log",
			traceIDsSetting: "",
			headers:         map[string]string{"abc": "first trace ID", "123": "second trace ID"},
		},
	}

	for i, tt := range tests {
		tt := tt
		t.Run(fmt.Sprintf("case-%v:%v", i, tt.traceIDsSetting), func(t *testing.T) {
			defer ReplaceLogger(tt.logFile)()

			config.Settings.HeaderOfTraceID = tt.traceIDsSetting
			e := CreateHTTPExpect(t, api.InitServer())

			// Test outside trace IDs
			req := e.GET(GetBundleURL, Name, Version, Locale, Component)
			resp := req.WithHeaders(tt.headers).Expect()
			resp.Status(http.StatusOK)

			logger.Log.Sync()
			bts, _ := ioutil.ReadFile(tt.logFile)
			logContent := string(bts)

			idSet := hashset.New()
			for _, traceID := range strings.Split(tt.traceIDsSetting, common.ParamSep) {
				idSet.Add(traceID)
			}
			hasTraceIDsInLog := false
			for k, v := range tt.headers {
				if idSet.Contains(k) {
					assert.Contains(t, logContent, `"`+k+`"`)
					assert.Contains(t, logContent, `"`+v+`"`)
					hasTraceIDsInLog = true
				} else {
					assert.NotContains(t, logContent, `"`+k+`"`)
					assert.NotContains(t, logContent, `"`+v+`"`)
				}
			}
			if hasTraceIDsInLog {
				assert.Contains(t, logContent, "Outside trace IDs")
			} else {
				assert.NotContains(t, logContent, "Outside trace IDs")
			}
		})
	}
}

func TestRecovery(t *testing.T) {
	tests := []struct {
		testName     string
		ginMode      string
		URL, logFile string
		panicErr     error
		wantedCode   int
	}{
		{testName: "NormalRecovery",
			ginMode:    gin.TestMode,
			URL:        "/TestRecovery",
			logFile:    logFolder + "recovery.log",
			panicErr:   errors.New("Recovery error"),
			wantedCode: http.StatusInternalServerError,
		},
		{testName: "BorkenPipe",
			ginMode:    gin.DebugMode,
			URL:        "/TestRecovery_broken_pipeline",
			logFile:    logFolder + "brokenpipe.log",
			panicErr:   &net.OpError{Err: &os.SyscallError{Err: errors.New("broken pipe error")}},
			wantedCode: http.StatusOK,
		},
	}

	for _, tt := range tests {
		tt := tt
		t.Run(tt.testName, func(t *testing.T) {
			oldMode := gin.Mode()
			gin.SetMode(tt.ginMode)
			defer gin.SetMode(oldMode)

			defer ReplaceLogger(tt.logFile)()

			ginEngine := api.InitServer()
			e := CreateHTTPExpect(t, ginEngine)
			ginEngine.GET(tt.URL, func(c *gin.Context) { panic(tt.panicErr) })

			e.GET(tt.URL).Expect().Status(tt.wantedCode)

			logger.Log.Sync()
			bts, _ := ioutil.ReadFile(tt.logFile)
			logContent := string(bts)
			assert.Contains(t, logContent, "[Recovery from panic]")
			assert.Contains(t, logContent, tt.panicErr.Error())
			if tt.ginMode == gin.DebugMode {
				assert.Contains(t, logContent, `"request"`)
			}
		})
	}
}

func TestCompressResponse(t *testing.T) {
	oldAlgorithm := config.Settings.Server.CompressionAlgorithm
	defer func() {
		config.Settings.Server.CompressionAlgorithm = oldAlgorithm
	}()

	tests := []struct {
		CompressionAlgorithm, header string
		expectedEncoding             []string
	}{
		{CompressionAlgorithm: "gzip&br", header: "", expectedEncoding: nil},
		{CompressionAlgorithm: "gzip&br", header: "gzip", expectedEncoding: []string{"gzip"}},
		{CompressionAlgorithm: "gzip&br", header: "br", expectedEncoding: []string{"br"}},

		{CompressionAlgorithm: "gzip", header: "", expectedEncoding: nil},
		{CompressionAlgorithm: "gzip", header: "gzip", expectedEncoding: []string{"gzip"}},
		{CompressionAlgorithm: "gzip", header: "br", expectedEncoding: nil},

		{CompressionAlgorithm: "br", header: "", expectedEncoding: nil},
		{CompressionAlgorithm: "br", header: "gzip", expectedEncoding: nil},
		{CompressionAlgorithm: "br", header: "br", expectedEncoding: []string{"br"}},

		{CompressionAlgorithm: "", header: "", expectedEncoding: nil},
		{CompressionAlgorithm: "", header: "gzip", expectedEncoding: nil},
		{CompressionAlgorithm: "", header: "br", expectedEncoding: nil},
	}

	for _, tt := range tests {
		tt := tt
		t.Run("'"+tt.CompressionAlgorithm+"':'"+tt.header+"'", func(t *testing.T) {
			config.Settings.Server.CompressionAlgorithm = tt.CompressionAlgorithm
			e := CreateHTTPExpect(t, api.InitServer())
			req := e.GET(GetBundleURL, Name, Version, Locale, Component)
			if tt.header != "" {
				req.WithHeader(headers.AcceptEncoding, tt.header)
			}
			respNoCompression := req.Expect()
			respNoCompression.ContentEncoding(tt.expectedEncoding...)
		})
	}
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
		for _, v := range strings.Split(d.locales, common.ParamSep) {
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
	resp := e.GET(GetBundleURL, "not-found", Version, "zh-Hans", "sunglow").Expect()
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
