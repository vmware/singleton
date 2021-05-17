/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"fmt"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/stretchr/testify/assert"
)

func BenchmarkGetMultipleComponents(b *testing.B) {
	const myURL = BaseURL + "/translation/products/" + Name + "/versions/" + Version + "?components=%s&locales=%s"

	for i := 0; i < b.N; i++ {
		w := httptest.NewRecorder()
		curURL := fmt.Sprintf(myURL, "", "")
		params, _ := http.NewRequest("GET", curURL, nil)
		GinTestEngine.ServeHTTP(w, params)
		assert.Equal(b, http.StatusOK, w.Result().StatusCode)
	}
}

func BenchmarkGetOneComponentByProduct(b *testing.B) {
	const myURL = BaseURL + "/translation/products/" + Name + "/versions/" + Version + "?components=%s&locales=%s"

	for i := 0; i < b.N; i++ {
		w := httptest.NewRecorder()
		curURL := fmt.Sprintf(myURL, Component, Locale)
		params, _ := http.NewRequest("GET", curURL, nil)
		GinTestEngine.ServeHTTP(w, params)
		assert.Equal(b, http.StatusOK, w.Result().StatusCode)
	}
}

func BenchmarkGetComponent(b *testing.B) {
	const myURL = BaseURL + "/translation/products/" + Name + "/versions/" + Version + "/locales/%s/components/%s"

	for i := 0; i < b.N; i++ {
		w := httptest.NewRecorder()
		realURL := fmt.Sprintf(myURL, Locale, Component)
		params, _ := http.NewRequest("GET", realURL, nil)
		GinTestEngine.ServeHTTP(w, params)
		assert.Equal(b, http.StatusOK, w.Result().StatusCode)
	}
}

func BenchmarkGetPatternByLocale(b *testing.B) {
	const myURL = BaseURL + "/formatting/patterns/locales/%s?scope=%s"

	for i := 0; i < b.N; i++ {
		for _, d := range []struct{ locale, scope string }{
			{locale: "en", scope: "dates"},
			{locale: "zh-Hans", scope: "numbers"},
			{locale: "es-US", scope: "plurals"},
			{locale: "de", scope: "currencies,numbers"},
			{locale: "de", scope: "dates,currencies,numbers,plurals"},
			{locale: "es-MX", scope: "dateFields"},
		} {
			w := httptest.NewRecorder()
			realURL := fmt.Sprintf(myURL, d.locale, d.scope)
			params, _ := http.NewRequest("GET", realURL, nil)
			GinTestEngine.ServeHTTP(w, params)
			assert.Equal(b, http.StatusOK, w.Result().StatusCode)
		}
	}
}

func BenchmarkGetPatternByLangReg(b *testing.B) {
	const myURL = BaseURL + "/formatting/patterns?language=%s&region=%s&scope=%s"

	for i := 0; i < b.N; i++ {
		for _, d := range []struct{ language, region, scope string }{
			{language: "en", region: "US", scope: "dates"},
			{language: "zh-Hans", region: "CN", scope: "numbers"},
			{language: "es-US", region: "MX", scope: "plurals"},
			{language: "de", region: "IT", scope: "currencies,numbers"},
			{language: "de", region: "CH", scope: "dates,currencies,numbers,plurals"},
			{language: "es-MX", region: "PR", scope: "dateFields"},
		} {
			w := httptest.NewRecorder()
			curURL := fmt.Sprintf(myURL, d.language, d.region, d.scope)
			params, _ := http.NewRequest("GET", curURL, nil)
			GinTestEngine.ServeHTTP(w, params)
			assert.Equal(b, http.StatusOK, w.Result().StatusCode)
		}
	}
}
