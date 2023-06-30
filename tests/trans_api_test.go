/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"path"
	"strconv"
	"strings"
	"testing"
	"time"

	"sgtnserver/api"
	"sgtnserver/api/v2/translation"
	"sgtnserver/internal/config"
	"sgtnserver/internal/sgtnerror"
	transmodule "sgtnserver/modules/translation"
	"sgtnserver/modules/translation/bundleinfo"
	"sgtnserver/modules/translation/translationservice"

	"github.com/go-http-utils/headers"
	"github.com/stretchr/testify/assert"
)

func TestGetSupportedComponents(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)
	expected := `{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","components":["sunglow","users"]}}`

	resp := e.GET(GetSupportedComponentsURL, Name, Version).Expect()
	resp.Status(http.StatusOK)
	assert.JSONEq(t, expected, resp.Body().Raw())

	resp2 := e.GET(GetSupportedComponentsURL, Name, Version).Expect()
	assert.Equal(t, resp.Body().Raw(), resp2.Body().Raw(), "ETag aren't same!")
}

func TestGetSupportedLocales(t *testing.T) {
	expected := `{"response":{"code":200,"message":"OK"},"data":{"locales":["de","en","es-MX","es","fr-CA","fr","ja","ko","pt-PT","pt","yue-Hant","yue","zh-Hans-HK","zh-Hans","zh-Hant-HK","zh-Hant"],"productName":"VPE","version":"1.0.0"}}`

	e := CreateHTTPExpect(t, GinTestEngine)
	resp := e.GET(GetSupportedLocalesURL, Name, Version).Expect()
	resp.Status(http.StatusOK)
	assert.JSONEq(t, expected, resp.Body().Raw())

	resp2 := e.GET(GetSupportedLocalesURL, Name, Version).Expect()
	assert.Equal(t, resp.Body().Raw(), resp2.Body().Raw(), "ETag aren't same!")
}

func TestGetBundlesMessages(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	for _, d := range []struct {
		locales, components, wanted string
		code                        int
	}{
		{"zh-Hans", "sunglow",
			`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locales":["zh-Hans"],"components":["sunglow"],"bundles":[{"component":"sunglow","locale":"zh-Hans","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}"}}]}}`,
			http.StatusOK},
		{"zh-Hans", "",
			`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locales":["zh-Hans"],"components":["sunglow","users"],"bundles":[{"component":"sunglow","locale":"zh-Hans","messages":{"plural.files": "{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}","message": "消息","pagination": "{0}-{1} 个客户，共 {2} 个","one.arg": "测试一个参数{0}"}},{"component":"users","locale":"zh-Hans","messages":{"Singleton.description": "{0} 是 Singleton 团队开发的通用 lib。","plural.files": "{0, plural,other {\"{1}\"上有 # 个文件。}}"}}]}}`,
			http.StatusOK},
		{"", "sunglow",
			`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locales":["de","en","es-MX","es","fr-CA","fr","ja","ko","pt-PT","pt","yue-Hant","yue","zh-Hans-HK","zh-Hans","zh-Hant-HK","zh-Hant"],"components":["sunglow"],"bundles":[{"component":"sunglow","locale":"de","messages":{"plural.files": "{files, plural,=0 {category 0 : No files.} one {category one : # Es gibt eine Datei auf {place}.} other {category other : Es gibt # Dateien auf {place}} }","message": "Meldung-de","pagination": "{0} – {1} von {2} Kunden","one.arg": "teste ein Argument {0}"}},{"component":"sunglow","locale":"en","messages":{"plural.files": "{files, plural,one {category one : There is one file on {place}.}other {category other : There are # files on {place}.}}","message": "Message-en","pagination": "{0} - {1} of {2} customers","one.arg": "test one argument {0}"}},{"component":"sunglow","locale":"es-MX","messages":{"plural.files": "{files, plural, =0 {category 0 : no files.} one {category one : hay un archivo en {place}.} other {category other : hay # archivos en {place}.}}","message": "Mensaje-es-MX","pagination": "{0}-{1} de {2} clientes","one.arg": "prueba un argumento {0}"}},{"component":"sunglow","locale":"es","messages":{"plural.files": "{files, plural, =0 {category 0 - no files.} one {category one - hay un archivo en {place}.} other {category other - hay # archivos en {place}.}}","message": "Mensaje-es","pagination": "{0}-{1} de {2} clientes","one.arg": "prueba un argumento {0}"}},{"component":"sunglow","locale":"fr-CA","messages":{"plural.files": "{files, plural, one{category one : # il y a un fichier sur {place}.} other {category other : # il n'y a pas de fichiers sur {place}.}}","message": "Message-fr-CA","pagination": "{0} - {1} clients sur {2}","one.arg": "tester un argument {0}"}},{"component":"sunglow","locale":"fr","messages":{"plural.files": "{files, plural, one{category one : # il y a un fichier sur {place}.} other {category other : # il n'y a pas de fichiers sur {place}.}}","message": "Message-fr","pagination": "{0} - {1} clients sur {2}","one.arg": "tester un argument {0}"}},{"component":"sunglow","locale":"ja","messages":{"plural.files": "{files, plural,=0 {category 0 : ファイルがありません。}one {category one : {place} に1つのファイルがあります。}other {category other : その他{place}には # ファイルがあります。}}","message": "メッセージ","pagination": "{0} ～ {1} / {2} ユーザー","one.arg": "1つの引数をテストしてください{0}"}},{"component":"sunglow","locale":"ko","messages":{"plural.files": "{files, plural,=0 {category 0 : 파일 없음}one {category one : {place}에 하나의 파일이 있습니다.}other {category other : {place}에 # 파일이 있습니다.}}","message": "메시지","pagination": "{2}개 고객 중 {0} - {1}","one.arg": "하나의 인수 {0}을 (를) 테스트하십시오."}},{"component":"sunglow","locale":"pt-PT","messages":{"plural.files": "{files, plural,one {category one : Existe um arquivo em {place}.} other {category other : Existem # arquivos em {place}.}}","message": "mensagem-pt-pt","pagination": "{0} - {1} de {2} clientes","one.arg": "teste um argumento {0}"}},{"component":"sunglow","locale":"pt","messages":{"plural.files": "{files, plural,one {category one : Há # arquivo em {place}.}other {category other : Existem # arquivos em {place}.}}","message": "mensagem-pt","pagination": "{0} - {1} de {2} clientes","one.arg": "teste um argumento {0}"}},{"component":"sunglow","locale":"yue-Hant","messages":{"plural.files": "{files, plural,=0 {category 0 - 无文件。}one {category one - 在{place}上有一个文件。}other {category other - {place}上有 # 文件。}}","message": "消息","pagination": "{0}-{1} 个客户，共 {2} 个","one.arg": "测试一个参数{0}"}},{"component":"sunglow","locale":"yue","messages":{"plural.files": "{files, plural,=0 {category 0 - 无文件。}one {category one - 在{place}上有一个文件。}other {category other - {place}上有 # 文件。}}","message": "消息","pagination": "{0}-{1} 个客户，共 {2} 个","one.arg": "测试一个参数{0}"}},{"component":"sunglow","locale":"zh-Hans-HK","messages":{"plural.files": "{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}","message": "消息","pagination": "{0}-{1} 个客户，共 {2} 个","one.arg": "测试一个参数{0}"}},{"component":"sunglow","locale":"zh-Hans","messages":{"plural.files": "{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}","message": "消息","pagination": "{0}-{1} 个客户，共 {2} 个","one.arg": "测试一个参数{0}"}},{"component":"sunglow","locale":"zh-Hant-HK","messages":{"plural.files": "{files, plural,=0 {category 0 : 無檔。}one {category one : 在{place}上有一個檔。}other {category other : {place}上有 # 檔。}}","message": "訊息","pagination": "{0} - {1} 個客戶，共 {2} 個客戶","one.arg": "測試一個參數{0}"}},{"component":"sunglow","locale":"zh-Hant","messages":{"plural.files": "{files, plural,=0 {category 0 : 無檔。}one {category one : 在{place}上有一個檔。}other {category other : {place}上有 # 檔。}}","message": "訊息","pagination": "{0} - {1} 個客戶，共 {2} 個客戶","one.arg": "測試一個參數{0}"}}]}}`,
			http.StatusOK},
		{"", "",
			`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locales":["de","en","es-MX","es","fr-CA","fr","ja","ko","pt-PT","pt","yue-Hant","yue","zh-Hans-HK","zh-Hans","zh-Hant-HK","zh-Hant"],"components":["sunglow","users"],"bundles":[{"component":"sunglow","locale":"de","messages":{"plural.files": "{files, plural,=0 {category 0 : No files.} one {category one : # Es gibt eine Datei auf {place}.} other {category other : Es gibt # Dateien auf {place}} }","message": "Meldung-de","pagination": "{0} – {1} von {2} Kunden","one.arg": "teste ein Argument {0}"}},{"component":"sunglow","locale":"en","messages":{"plural.files": "{files, plural,one {category one : There is one file on {place}.}other {category other : There are # files on {place}.}}","message": "Message-en","pagination": "{0} - {1} of {2} customers","one.arg": "test one argument {0}"}},{"component":"sunglow","locale":"es-MX","messages":{"plural.files": "{files, plural, =0 {category 0 : no files.} one {category one : hay un archivo en {place}.} other {category other : hay # archivos en {place}.}}","message": "Mensaje-es-MX","pagination": "{0}-{1} de {2} clientes","one.arg": "prueba un argumento {0}"}},{"component":"sunglow","locale":"es","messages":{"plural.files": "{files, plural, =0 {category 0 - no files.} one {category one - hay un archivo en {place}.} other {category other - hay # archivos en {place}.}}","message": "Mensaje-es","pagination": "{0}-{1} de {2} clientes","one.arg": "prueba un argumento {0}"}},{"component":"sunglow","locale":"fr-CA","messages":{"plural.files": "{files, plural, one{category one : # il y a un fichier sur {place}.} other {category other : # il n'y a pas de fichiers sur {place}.}}","message": "Message-fr-CA","pagination": "{0} - {1} clients sur {2}","one.arg": "tester un argument {0}"}},{"component":"sunglow","locale":"fr","messages":{"plural.files": "{files, plural, one{category one : # il y a un fichier sur {place}.} other {category other : # il n'y a pas de fichiers sur {place}.}}","message": "Message-fr","pagination": "{0} - {1} clients sur {2}","one.arg": "tester un argument {0}"}},{"component":"sunglow","locale":"ja","messages":{"plural.files": "{files, plural,=0 {category 0 : ファイルがありません。}one {category one : {place} に1つのファイルがあります。}other {category other : その他{place}には # ファイルがあります。}}","message": "メッセージ","pagination": "{0} ～ {1} / {2} ユーザー","one.arg": "1つの引数をテストしてください{0}"}},{"component":"sunglow","locale":"ko","messages":{"plural.files": "{files, plural,=0 {category 0 : 파일 없음}one {category one : {place}에 하나의 파일이 있습니다.}other {category other : {place}에 # 파일이 있습니다.}}","message": "메시지","pagination": "{2}개 고객 중 {0} - {1}","one.arg": "하나의 인수 {0}을 (를) 테스트하십시오."}},{"component":"sunglow","locale":"pt-PT","messages":{"plural.files": "{files, plural,one {category one : Existe um arquivo em {place}.} other {category other : Existem # arquivos em {place}.}}","message": "mensagem-pt-pt","pagination": "{0} - {1} de {2} clientes","one.arg": "teste um argumento {0}"}},{"component":"sunglow","locale":"pt","messages":{"plural.files": "{files, plural,one {category one : Há # arquivo em {place}.}other {category other : Existem # arquivos em {place}.}}","message": "mensagem-pt","pagination": "{0} - {1} de {2} clientes","one.arg": "teste um argumento {0}"}},{"component":"sunglow","locale":"yue-Hant","messages":{"plural.files": "{files, plural,=0 {category 0 - 无文件。}one {category one - 在{place}上有一个文件。}other {category other - {place}上有 # 文件。}}","message": "消息","pagination": "{0}-{1} 个客户，共 {2} 个","one.arg": "测试一个参数{0}"}},{"component":"sunglow","locale":"yue","messages":{"plural.files": "{files, plural,=0 {category 0 - 无文件。}one {category one - 在{place}上有一个文件。}other {category other - {place}上有 # 文件。}}","message": "消息","pagination": "{0}-{1} 个客户，共 {2} 个","one.arg": "测试一个参数{0}"}},{"component":"sunglow","locale":"zh-Hans-HK","messages":{"plural.files": "{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}","message": "消息","pagination": "{0}-{1} 个客户，共 {2} 个","one.arg": "测试一个参数{0}"}},{"component":"sunglow","locale":"zh-Hans","messages":{"plural.files": "{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}","message": "消息","pagination": "{0}-{1} 个客户，共 {2} 个","one.arg": "测试一个参数{0}"}},{"component":"sunglow","locale":"zh-Hant-HK","messages":{"plural.files": "{files, plural,=0 {category 0 : 無檔。}one {category one : 在{place}上有一個檔。}other {category other : {place}上有 # 檔。}}","message": "訊息","pagination": "{0} - {1} 個客戶，共 {2} 個客戶","one.arg": "測試一個參數{0}"}},{"component":"sunglow","locale":"zh-Hant","messages":{"plural.files": "{files, plural,=0 {category 0 : 無檔。}one {category one : 在{place}上有一個檔。}other {category other : {place}上有 # 檔。}}","message": "訊息","pagination": "{0} - {1} 個客戶，共 {2} 個客戶","one.arg": "測試一個參數{0}"}},{"component":"users","locale":"de","messages":{"Singleton.description": "{0} ist Common lib, entwickelt von Singleton Team.","plural.files": "{0, plural,one {Es gibt eine Datei auf {place}.}other {Es gibt # Dateien auf {place}.}}"}},{"component":"users","locale":"en","messages":{"Singleton.description": "{0} is common lib developed by Singleton team.","plural.files": "{0, plural,one {There is a file on \"{1}\".}other {There are # files on \"{1}\".}}","plural.reserved.character": "{0, plural,one {This is sharp '#'.}other {There are # sharp '#'.}}"}},{"component":"users","locale":"es","messages":{"Singleton.description": "{0} es un lib común desarrollado por el equipo Singleton.","plural.files": "{files, plural,one {Hay un archivo en {place}.}other {Hay # archivos en {place}.}}"}},{"component":"users","locale":"fr","messages":{"Singleton.description": "{0} est une bibliothèque commune développée par Singleton Team.","plural.files": "{files, plural,one {Il y a # fichier sur {place}.}other {Il y a # fichiers sur {place}.}}"}},{"component":"users","locale":"ja","messages":{"Singleton.description": "{0} は、Singleton チームによって開発された一般的な lib です。","plural.files": "{files, plural,other {{place} には # ファイルがあります。}}"}},{"component":"users","locale":"ko","messages":{"Singleton.description": "{0}는 Singleton 팀에서 개발한 일반적인 lib입니다.","plural.files": "{files, plural,other {{place} 파일에 # 파일이 있습니다.}}"}},{"component":"users","locale":"zh-Hans","messages":{"Singleton.description": "{0} 是 Singleton 团队开发的通用 lib。","plural.files": "{0, plural,other {\"{1}\"上有 # 个文件。}}"}},{"component":"users","locale":"zh-Hant","messages":{"Singleton.description": "{0} 是 Singleton 團隊開發的通用 lib。","plural.files": "{files, plural,other {{place}上有 # 個文檔。}}"}}]}}`,
			http.StatusOK},
	} {
		d := d
		t.Run(fmt.Sprintf("%v:%v", d.locales, d.components), func(t *testing.T) {
			resp := e.GET(GetBundlesURL, Name, Version).
				WithQuery("locales", d.locales).WithQuery("components", d.components).Expect()
			resp.Status(d.code)
			assert.JSONEq(t, d.wanted, resp.Body().Raw())

			resp2 := e.GET(GetBundlesURL, Name, Version).
				WithQuery("locales", d.locales).WithQuery("components", d.components).Expect()
			assert.Equal(t, resp.Body().Raw(), resp2.Body().Raw(), "ETag aren't same!")
		})
	}
}

func TestCrossDomainByGetBundle(t *testing.T) {
	host := "localhost"
	if config.Settings.Server.HTTPPort != 80 {
		host += fmt.Sprintf(":%d", config.Settings.Server.HTTPPort)
	}

	origin := "http://" + host

	oldSetting := config.Settings
	defer func() {
		config.Settings = oldSetting
		l3Service.ClearCache(context.TODO())
	}()

	tests := []struct {
		testName         string
		Enable           bool
		AllowCredentials bool
		AllowOrigin      string
		AllowMethods     string
		AllowHeaders     string
		MaxAge           time.Duration

		origin string
		host   string
	}{
		{testName: "Disabled", Enable: false},
		{testName: "allowAllOrigins", Enable: true, AllowOrigin: "*", origin: "http://anyhost"},
		{testName: "allowSomeOrigins_LocalReq", Enable: true, AllowOrigin: "http://allowedhost", origin: origin},
		{testName: "allowSomeOrigins_denied", Enable: true, AllowOrigin: "http://allowedhost", origin: "http://deniedhost"},
		{testName: "allowSomeOrigins_allowed", Enable: true, AllowOrigin: "http://allowedhost", origin: "http://allowedhost"},
		{testName: "allowGetOnly", Enable: true, AllowOrigin: "*", AllowMethods: "GET", origin: "http://anyhost"},
	}

	for i, tt := range tests {
		tt := tt

		t.Run(fmt.Sprintf("%d-%s", i, tt.testName), func(t *testing.T) {
			config.Settings.CrossDomain.Enable = tt.Enable
			config.Settings.CrossDomain.AllowCredentials = tt.AllowCredentials
			config.Settings.CrossDomain.AllowOrigin = tt.AllowOrigin
			if tt.AllowMethods != "" {
				config.Settings.CrossDomain.AllowMethods = tt.AllowMethods
			} else {
				config.Settings.CrossDomain.AllowMethods = "GET,POST,PUT,DELETE,OPTIONS"
			}
			config.Settings.CrossDomain.AllowHeaders = tt.AllowHeaders
			config.Settings.CrossDomain.MaxAge = tt.MaxAge

			e := CreateHTTPExpect(t, api.InitServer())

			// Without origin header. This isn't a cross domain request
			resp := e.GET(GetBundleURL, Name, Version, Locale, Component).WithHost(host).Expect()
			resp.Status(http.StatusOK)
			if resp.Raw().StatusCode == http.StatusOK {
				// Verify "Access-Control-Allow-Origin" header in response
				resp.Header("Access-Control-Allow-Origin").Empty()
			}

			if tt.Enable {
				resp = e.GET(GetBundleURL, Name, Version, Locale, Component).WithHost(host).WithHeader("Origin", tt.origin).Expect()
				if tt.origin == origin {
					resp.Status(http.StatusOK)
					// Verify "Access-Control-Allow-Origin" header in response
					resp.Header("Access-Control-Allow-Origin").Empty()
				} else { // Verify "Access-Control-Allow-Origin" header in response
					if tt.AllowOrigin == "*" || tt.AllowOrigin == tt.origin {
						resp.Status(http.StatusOK)
						resp.Header("Access-Control-Allow-Origin").Equal(tt.AllowOrigin)
					} else {
						resp.Status(http.StatusForbidden)
					}
				}

				component := RandomString(6)
				defer os.RemoveAll(path.Join(config.Settings.LocalBundle.BasePath, Name, Version, component))
				resp = e.PUT(PutBundlesURL, Name, Version).WithHost(host).WithHeader("Origin", tt.origin).WithBytes([]byte(fmt.Sprintf(bundleDataToPut, Name, Version, Locale, component, Key, Msg))).Expect()
				if tt.AllowOrigin == "GET" {
					// Verify PUT is forbidden
					resp.Status(http.StatusForbidden)
				} else {
					if tt.origin == origin {
						resp.Status(http.StatusOK)
						// Verify "Access-Control-Allow-Origin" header in response
						resp.Header("Access-Control-Allow-Origin").Empty()
					} else { // Verify "Access-Control-Allow-Origin" header in response
						if tt.AllowOrigin == "*" || tt.AllowOrigin == tt.origin {
							resp.Status(http.StatusOK)
							resp.Header("Access-Control-Allow-Origin").Equal(tt.AllowOrigin)
						} else {
							resp.Status(http.StatusForbidden)
						}
					}
				}
			}
		})
	}
}

func TestCrossDomain_OPTIONS(t *testing.T) {
	const originHeader = "http://localhost"
	e := CreateHTTPExpect(t, GinTestEngine)

	resp := e.OPTIONS("/").WithHeader("Origin", originHeader).Expect()
	resp.Status(http.StatusNoContent)

	resp.Header("Access-Control-Allow-Credentials").Equal(strconv.FormatBool(config.Settings.CrossDomain.AllowCredentials))
	resp.Header("Access-Control-Allow-Headers").EqualFold(config.Settings.CrossDomain.AllowHeaders)
	resp.Header("Access-Control-Allow-Methods").Equal(config.Settings.CrossDomain.AllowMethods)
	resp.Header("Access-Control-Allow-Origin").EqualFold(config.Settings.CrossDomain.AllowOrigin)
	resp.Header("Access-Control-Max-Age").Equal(strconv.FormatInt(int64(config.Settings.CrossDomain.MaxAge.Seconds()), 10))
}

func TestGetBundleNormal(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	req := e.GET(GetBundleURL, Name, Version, "zh-Hans", "sunglow")
	resp := req.Expect()
	expected := `{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locale":"zh-Hans","component":"sunglow","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}"}}}`
	resp.Status(http.StatusOK)
	assert.JSONEq(t, expected, resp.Body().Raw())

	// Test CacheControl
	resp.Header(headers.CacheControl).Equal(config.Settings.Server.CacheControl)
	etag := resp.Headers().Value(headers.ETag).Array().First().String().Raw()

	// Send request again to test Etag
	req = e.GET(GetBundleURL, Name, Version, "zh-Hans", "sunglow")
	resp = req.WithHeader(headers.IfNoneMatch, etag).Expect()
	resp.Status(http.StatusNotModified)
	resp.Body().Empty()

	resp = e.GET(GetBundleURL, Name, Version, "zh-Hans", "nonexistent").Expect()
	resp.Status(http.StatusOK).Body().Contains("nonexistent")
}

func TestGetSingleMessage(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	resp := e.GET(GetKeyURL, Name, Version, "zh-Hans", Component, "one.arg").Expect()
	resp.Status(http.StatusOK).Body().Contains("测试一个参数{0}")
}

const bundleDataToPut = `
{
  "data": {
	"productName": "%s",
	"version": "%s",
	"translation": [
	  {
		"locale": "%s",
		"component": "%s",
		"messages": {
		  "%s": "%s",
		  "one.arg": "test one argument {0}"
		}
	  }
	]
  }
}
`

func TestPutBundle(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)
	newProduct, newVersion := "newProduct", "100"

	tests := []struct {
		TestName              string
		name, version, locale string
		wantedCode            int
	}{
		{TestName: "Normal", name: Name, version: Version, locale: Locale, wantedCode: http.StatusOK},
		{TestName: "NewProduct", name: newProduct, version: Version, locale: Locale, wantedCode: http.StatusBadRequest},
		{TestName: "NewVersion", name: Name, version: newVersion, locale: Locale, wantedCode: http.StatusOK},
		{TestName: "inValidProduct", name: "---", version: Version, locale: Locale, wantedCode: http.StatusBadRequest},
		{TestName: "invalidVersion", name: Name, version: "---", locale: Locale, wantedCode: http.StatusBadRequest},
	}

	os.RemoveAll(path.Join(config.Settings.LocalBundle.BasePath, newProduct))
	os.RemoveAll(path.Join(config.Settings.LocalBundle.BasePath, Name, newVersion))
	bundleinfo.RefreshBundleInfo(context.TODO())

	for _, tt := range tests {
		tt := tt
		t.Run(tt.TestName, func(t *testing.T) {
			// t.Parallel()

			component := RandomString(6)
			defer os.RemoveAll(path.Join(config.Settings.LocalBundle.BasePath, tt.name, tt.version, component))

			resp := e.PUT(PutBundlesURL, tt.name, tt.version).WithBytes([]byte(fmt.Sprintf(bundleDataToPut, tt.name, tt.version, tt.locale, component, Key, Msg))).Expect()

			resp.Status(http.StatusOK)
			bError, _ := GetErrorAndData(resp.Body().Raw())
			assert.Equal(t, tt.wantedCode, bError.Code)

			if bError.Code == http.StatusOK {
				id := transmodule.MessageID{Name: tt.name, Version: tt.version, Locale: Locale, Component: component, Key: Key}
				// Query to check putting is successful
				data, err := translationservice.GetService().GetString(context.TODO(), &id)
				assert.Nil(t, err)
				assert.Equal(t, Msg, data.Translation)
			}
		})
	}
}

func TestPutBundleNameVersionInconsistent(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	tests := []struct {
		TestName                  string
		name, version, locale     string
		nameInbody, versionInbody string
		wantedCode                int
	}{
		{TestName: "nameInconsistent", name: Name, version: Version, locale: Locale, nameInbody: "otherName", versionInbody: Version, wantedCode: http.StatusBadRequest},
		{TestName: "versionInconsistent", name: Name, version: Version, locale: Locale, nameInbody: Name, versionInbody: "otherVersion", wantedCode: http.StatusBadRequest},
	}

	for _, tt := range tests {
		tt := tt
		t.Run(tt.TestName, func(t *testing.T) {
			// t.Parallel()

			component := RandomString(6)
			defer os.RemoveAll(path.Join(config.Settings.LocalBundle.BasePath, tt.name, tt.version, component))

			resp := e.PUT(PutBundlesURL, tt.name, tt.version).WithBytes([]byte(fmt.Sprintf(bundleDataToPut, tt.nameInbody, tt.versionInbody, tt.locale, component, Key, Msg))).Expect()

			resp.Status(http.StatusOK)
			bError, _ := GetErrorAndData(resp.Body().Raw())
			assert.Equal(t, tt.wantedCode, bError.Code)
		})
	}
}

func TestPutBundleWithoutData(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	tests := []struct {
		testName              string
		name, version, locale string
		wantedCode            int
	}{
		{testName: "withoutData", name: Name, version: Version, locale: Locale, wantedCode: http.StatusBadRequest},
	}

	for _, tt := range tests {
		tt := tt
		t.Run(tt.testName, func(t *testing.T) {
			// t.Parallel()

			component := RandomString(6)
			defer os.RemoveAll(path.Join(config.Settings.LocalBundle.BasePath, tt.name, tt.version, component))

			req := translation.UpdateTranslationDTO{}
			json.Unmarshal([]byte(fmt.Sprintf(bundleDataToPut, tt.name, tt.version, tt.locale, component, Key, Msg)), &req)
			req.Data.Translation = nil
			bts, _ := json.Marshal(req)
			resp := e.PUT(PutBundlesURL, tt.name, tt.version).WithBytes(bts).Expect()

			resp.Status(http.StatusOK)
			bError, _ := GetErrorAndData(resp.Body().Raw())
			assert.Equal(t, tt.wantedCode, bError.Code)
		})
	}
}

func TestPutBundleUpdateKey(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)
	newVersion := "100"

	defer func() {
		os.RemoveAll(path.Join(config.Settings.LocalBundle.BasePath, Name, newVersion))
		bundleinfo.RefreshBundleInfo(context.TODO())
	}()

	resp := e.PUT(PutBundlesURL, Name, newVersion).WithBytes([]byte(fmt.Sprintf(bundleDataToPut, Name, newVersion, Locale, Component, Key, Msg))).Expect()
	bError, _ := GetErrorAndData(resp.Body().Raw())
	assert.Equal(t, http.StatusOK, bError.Code)

	// put the second string
	key2, trans2 := "key1", "msg1"
	resp = e.PUT(PutBundlesURL, Name, newVersion).WithBytes([]byte(fmt.Sprintf(bundleDataToPut, Name, newVersion, Locale, Component, key2, trans2))).Expect()
	bError, _ = GetErrorAndData(resp.Body().Raw())
	assert.Equal(t, http.StatusOK, bError.Code)

	// check existing keys still exist
	id := transmodule.MessageID{Name: Name, Version: newVersion, Locale: Locale, Component: Component, Key: Key}
	data, _ := translationservice.GetService().GetString(context.TODO(), &id)
	assert.Equal(t, Msg, data.Translation)
}

func TestVersionFallback(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	resp := e.GET(GetSupportedComponentsURL, Name, "1.0.1.0").Expect()
	resp.Status(http.StatusOK)

	serviceErr, _ := GetErrorAndData(resp.Body().Raw())
	assert.Equal(t, sgtnerror.StatusVersionFallbackTranslation.Code(), serviceErr.Code)
	assert.Contains(t, serviceErr.UserMsg, sgtnerror.StatusVersionFallbackTranslation.Message())
}

func TestApiTransExceptionArgs(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)

	tests := []struct {
		testName                         string
		name, version, locale, component string
		key                              string
		wantedCode                       int
	}{
		{testName: "invalidProductName", name: "invalidName", version: Version, locale: Locale, component: Component, key: Key, wantedCode: http.StatusBadRequest},
		{testName: "illegalProductCharacter", name: "---", version: Version, locale: Locale, component: Component, key: Key, wantedCode: http.StatusBadRequest},
		{testName: "invalidVersion", name: Name, version: "invalidVersion", locale: Locale, component: Component, key: Key, wantedCode: http.StatusBadRequest},
		{testName: "illegalVersionCharacter", name: Name, version: "abc", locale: Locale, component: Component, key: Key, wantedCode: http.StatusBadRequest},
		{testName: "invalidLocale", name: Name, version: Version, locale: "invalidLocale", component: Component, key: Key, wantedCode: sgtnerror.StatusNotFound.Code()},
		{testName: "illegalLocaleCharacter", name: Name, version: Version, locale: "€", component: Component, key: Key, wantedCode: http.StatusBadRequest},
		{testName: "invalidComponent", name: Name, version: Version, locale: Locale, component: "invalidComponent", key: Key, wantedCode: sgtnerror.StatusNotFound.Code()},
		{testName: "illegalComponentCharacter", name: Name, version: Version, locale: Locale, component: "\t", key: Key, wantedCode: http.StatusBadRequest},
		{testName: "invalidKey", name: Name, version: Version, locale: Locale, component: Component, key: "nonexistent", wantedCode: sgtnerror.StatusNotFound.Code()},
		{testName: "illegalKeyCharacter", name: Name, version: Version, locale: Locale, component: Component, key: "￥", wantedCode: http.StatusBadRequest},
	}

	for _, tt := range tests {
		tt := tt
		t.Run(tt.testName, func(t *testing.T) {
			// t.Parallel()

			resp := e.GET(GetKeyURL, tt.name, tt.version, tt.locale, tt.component, tt.key).Expect()
			bError, _ := GetErrorAndData(resp.Body().Raw())

			resp.Status(http.StatusOK)

			if tt.locale == "invalidLocale" {
				assert.Equal(t, http.StatusOK, bError.Code)
			} else {
				assert.Equal(t, tt.wantedCode, bError.Code)
			}

			if strings.Contains(tt.testName, "Product") || strings.Contains(tt.testName, "Version") {
				resp := e.GET(GetSupportedComponentsURL, tt.name, tt.version).Expect()
				bError, _ := GetErrorAndData(resp.Body().Raw())
				assert.Equal(t, tt.wantedCode, bError.Code)

				resp = e.GET(GetSupportedLocalesURL, tt.name, tt.version).Expect()
				bError, _ = GetErrorAndData(resp.Body().Raw())
				assert.Equal(t, tt.wantedCode, bError.Code)
			}

			if !strings.Contains(tt.testName, "Key") {
				resp := e.GET(GetBundleURL, tt.name, tt.version, tt.locale, tt.component).Expect()
				bError, _ := GetErrorAndData(resp.Body().Raw())
				assert.Equal(t, tt.wantedCode, bError.Code)

				resp = e.GET(GetBundlesURL, tt.name, tt.version).
					WithQuery("locales", tt.locale).WithQuery("components", tt.component).Expect()
				bError, _ = GetErrorAndData(resp.Body().Raw())
				assert.Equal(t, tt.wantedCode, bError.Code)
			}
		})
	}
}

func TestGetMultipleMessages(t *testing.T) {
	e := CreateHTTPExpect(t, GinTestEngine)
	anotherKey := "plural.files"
	nonexistentKey := "nonexistent.key"

	for _, d := range []struct {
		desc, keys string
		wanted     string
	}{
		{"Get 1 key", Key,
			`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locale":"en","component":"sunglow","messages":{"message":"Message-en"}}}`},
		{"Get 1 nonexistent key", nonexistentKey,
			`{"response":{"code":404,"message":"key 'nonexistent.key' isn't found"},"data":{"productName":"VPE","version":"1.0.0","locale":"en","component":"sunglow","messages":{}}}`},
		{"Get 2 keys", Key + "," + anotherKey,
			`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locale":"en","component":"sunglow","messages":{"message":"Message-en","plural.files":"{files, plural,one {category one : There is one file on {place}.}other {category other : There are # files on {place}.}}"}}}`},
		{"Get 2 keys with an nonexistent key", Key + "," + nonexistentKey,
			`{"response":{"code":207,"message":"Successful Partially"},"data":{"productName":"VPE","version":"1.0.0","locale":"en","component":"sunglow","messages":{"message":"Message-en"}}}`},
		{"Get 2 nonexistent key", nonexistentKey + "," + nonexistentKey + "1",
			`{"response":{"code":404,"message":"2 errors occurred. key 'nonexistent.key' isn't found; key 'nonexistent.key1' isn't found"},"data":{"productName":"VPE","version":"1.0.0","locale":"en","component":"sunglow","messages":{}}}`},
	} {
		d := d
		t.Run(d.desc, func(t *testing.T) {
			resp := e.GET(GetKeysURL, Name, Version, Locale, Component).
				WithQuery("keys", d.keys).Expect()

			// ioutil.WriteFile(d.keys+".json", []byte(resp.Body().Raw()), 0666)

			assert.JSONEq(t, d.wanted, resp.Body().Raw())
		})
	}
}
