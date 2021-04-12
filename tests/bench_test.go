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
)

func BenchmarkGetComponentsMessages(b *testing.B) {
	const myURL = "/i18n/api/v2/translation/products/" + Name + "/versions/" + Version + "?components=%s&locales=%s"

	w := httptest.NewRecorder()

	for i := 0; i < b.N; i++ {
		for _, d := range []struct {
			locales, components, wanted string
			code                        int
		}{
			{"zh-Hans", "sunglow",
				`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locales":["zh-Hans"],"components":["sunglow"],"bundles":[{"component":"sunglow","locale":"zh-Hans","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}"}}]}}`,
				http.StatusOK},
			{"zh-Hans", "",
				`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locales":["zh-Hans"],"components":["sunglow","users"],"bundles":[{"component":"users","locale":"zh-Hans","messages":{"Singleton.description":"{0} 是 Singleton 团队开发的通用 lib。","plural.files":"{0, plural,other {\"{1}\"上有 # 个文件。}}"}},{"component":"sunglow","locale":"zh-Hans","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}"}}]}}`,
				http.StatusOK},
			{"", "sunglow",
				`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locales":["de","en","es","es-MX","fr","fr-CA","ja","ko","pt","pt-PT","yue","yue-Hant","zh-Hans","zh-Hans-HK","zh-Hant","zh-Hant-HK"],"components":["sunglow"],"bundles":[{"component":"sunglow","locale":"de","messages":{"message":"Meldung-de","one.arg":"teste ein Argument {0}","pagination":"{0} – {1} von {2} Kunden","plural.files":"{files, plural,=0 {category 0 : No files.} one {category one : # Es gibt eine Datei auf {place}.} other {category other : Es gibt # Dateien auf {place}} }"}},{"component":"sunglow","locale":"en","messages":{"message":"Message-en","one.arg":"test one argument {0}","pagination":"{0} - {1} of {2} customers","plural.files":"{files, plural,one {category one : There is one file on {place}.}other {category other : There are # files on {place}.}}"}},{"component":"sunglow","locale":"es","messages":{"message":"Mensaje-es","one.arg":"prueba un argumento {0}","pagination":"{0}-{1} de {2} clientes","plural.files":"{files, plural, =0 {category 0 - no files.} one {category one - hay un archivo en {place}.} other {category other - hay # archivos en {place}.}}"}},{"component":"sunglow","locale":"es-MX","messages":{"message":"Mensaje-es-MX","one.arg":"prueba un argumento {0}","pagination":"{0}-{1} de {2} clientes","plural.files":"{files, plural, =0 {category 0 : no files.} one {category one : hay un archivo en {place}.} other {category other : hay # archivos en {place}.}}"}},{"component":"sunglow","locale":"fr","messages":{"message":"Message-fr","one.arg":"tester un argument {0}","pagination":"{0} - {1} clients sur {2}","plural.files":"{files, plural, one{category one : # il y a un fichier sur {place}.} other {category other : # il n'y a pas de fichiers sur {place}.}}"}},{"component":"sunglow","locale":"fr-CA","messages":{"message":"Message-fr-CA","one.arg":"tester un argument {0}","pagination":"{0} - {1} clients sur {2}","plural.files":"{files, plural, one{category one : # il y a un fichier sur {place}.} other {category other : # il n'y a pas de fichiers sur {place}.}}"}},{"component":"sunglow","locale":"ja","messages":{"message":"メッセージ","one.arg":"1つの引数をテストしてください{0}","pagination":"{0} ～ {1} / {2} ユーザー","plural.files":"{files, plural,=0 {category 0 : ファイルがありません。}one {category one : {place} に1つのファイルがあります。}other {category other : その他{place}には # ファイルがあります。}}"}},{"component":"sunglow","locale":"ko","messages":{"message":"메시지","one.arg":"하나의 인수 {0}을 (를) 테스트하십시오.","pagination":"{2}개 고객 중 {0} - {1}","plural.files":"{files, plural,=0 {category 0 : 파일 없음}one {category one : {place}에 하나의 파일이 있습니다.}other {category other : {place}에 # 파일이 있습니다.}}"}},{"component":"sunglow","locale":"pt","messages":{"message":"mensagem-pt","one.arg":"teste um argumento {0}","pagination":"{0} - {1} de {2} clientes","plural.files":"{files, plural,one {category one : Há # arquivo em {place}.}other {category other : Existem # arquivos em {place}.}}"}},{"component":"sunglow","locale":"pt-PT","messages":{"message":"mensagem-pt-pt","one.arg":"teste um argumento {0}","pagination":"{0} - {1} de {2} clientes","plural.files":"{files, plural,one {category one : Existe um arquivo em {place}.} other {category other : Existem # arquivos em {place}.}}"}},{"component":"sunglow","locale":"yue","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 - 无文件。}one {category one - 在{place}上有一个文件。}other {category other - {place}上有 # 文件。}}"}},{"component":"sunglow","locale":"yue-Hant","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 - 无文件。}one {category one - 在{place}上有一个文件。}other {category other - {place}上有 # 文件。}}"}},{"component":"sunglow","locale":"zh-Hans","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}"}},{"component":"sunglow","locale":"zh-Hans-HK","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}"}},{"component":"sunglow","locale":"zh-Hant","messages":{"message":"訊息","one.arg":"測試一個參數{0}","pagination":"{0} - {1} 個客戶，共 {2} 個客戶","plural.files":"{files, plural,=0 {category 0 : 無檔。}one {category one : 在{place}上有一個檔。}other {category other : {place}上有 # 檔。}}"}},{"component":"sunglow","locale":"zh-Hant-HK","messages":{"message":"訊息","one.arg":"測試一個參數{0}","pagination":"{0} - {1} 個客戶，共 {2} 個客戶","plural.files":"{files, plural,=0 {category 0 : 無檔。}one {category one : 在{place}上有一個檔。}other {category other : {place}上有 # 檔。}}"}}]}}`,
				http.StatusOK},
			{"", "",
				`{"response":{"code":200,"message":"OK"},"data":{"productName":"VPE","version":"1.0.0","locales":["de","en","es","es-MX","fr","fr-CA","ja","ko","pt","pt-PT","yue","yue-Hant","zh-Hans","zh-Hans-HK","zh-Hant","zh-Hant-HK"],"components":["sunglow","users"],"bundles":[{"component":"users","locale":"ko","messages":{"Singleton.description":"{0}는 Singleton 팀에서 개발한 일반적인 lib입니다.","plural.files":"{files, plural,other {{place} 파일에 # 파일이 있습니다.}}"}},{"component":"users","locale":"es","messages":{"Singleton.description":"{0} es un lib común desarrollado por el equipo Singleton.","plural.files":"{files, plural,one {Hay un archivo en {place}.}other {Hay # archivos en {place}.}}"}},{"component":"users","locale":"fr","messages":{"Singleton.description":"{0} est une bibliothèque commune développée par Singleton Team.","plural.files":"{files, plural,one {Il y a # fichier sur {place}.}other {Il y a # fichiers sur {place}.}}"}},{"component":"users","locale":"zh-Hans","messages":{"Singleton.description":"{0} 是 Singleton 团队开发的通用 lib。","plural.files":"{0, plural,other {\"{1}\"上有 # 个文件。}}"}},{"component":"users","locale":"de","messages":{"Singleton.description":"{0} ist Common lib, entwickelt von Singleton Team.","plural.files":"{0, plural,one {Es gibt eine Datei auf {place}.}other {Es gibt # Dateien auf {place}.}}"}},{"component":"users","locale":"zh-Hant","messages":{"Singleton.description":"{0} 是 Singleton 團隊開發的通用 lib。","plural.files":"{files, plural,other {{place}上有 # 個文檔。}}"}},{"component":"users","locale":"en","messages":{"Singleton.description":"{0} is common lib developed by Singleton team.","plural.files":"{0, plural,one {There is a file on \"{1}\".}other {There are # files on \"{1}\".}}","plural.reserved.character":"{0, plural,one {This is sharp '#'.}other {There are # sharp '#'.}}"}},{"component":"users","locale":"ja","messages":{"Singleton.description":"{0} は、Singleton チームによって開発された一般的な lib です。","plural.files":"{files, plural,other {{place} には # ファイルがあります。}}"}},{"component":"sunglow","locale":"ko","messages":{"message":"메시지","one.arg":"하나의 인수 {0}을 (를) 테스트하십시오.","pagination":"{2}개 고객 중 {0} - {1}","plural.files":"{files, plural,=0 {category 0 : 파일 없음}one {category one : {place}에 하나의 파일이 있습니다.}other {category other : {place}에 # 파일이 있습니다.}}"}},{"component":"sunglow","locale":"zh-Hant-HK","messages":{"message":"訊息","one.arg":"測試一個參數{0}","pagination":"{0} - {1} 個客戶，共 {2} 個客戶","plural.files":"{files, plural,=0 {category 0 : 無檔。}one {category one : 在{place}上有一個檔。}other {category other : {place}上有 # 檔。}}"}},{"component":"sunglow","locale":"yue-Hant","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 - 无文件。}one {category one - 在{place}上有一个文件。}other {category other - {place}上有 # 文件。}}"}},{"component":"sunglow","locale":"fr-CA","messages":{"message":"Message-fr-CA","one.arg":"tester un argument {0}","pagination":"{0} - {1} clients sur {2}","plural.files":"{files, plural, one{category one : # il y a un fichier sur {place}.} other {category other : # il n'y a pas de fichiers sur {place}.}}"}},{"component":"sunglow","locale":"pt","messages":{"message":"mensagem-pt","one.arg":"teste um argumento {0}","pagination":"{0} - {1} de {2} clientes","plural.files":"{files, plural,one {category one : Há # arquivo em {place}.}other {category other : Existem # arquivos em {place}.}}"}},{"component":"sunglow","locale":"pt-PT","messages":{"message":"mensagem-pt-pt","one.arg":"teste um argumento {0}","pagination":"{0} - {1} de {2} clientes","plural.files":"{files, plural,one {category one : Existe um arquivo em {place}.} other {category other : Existem # arquivos em {place}.}}"}},{"component":"sunglow","locale":"es","messages":{"message":"Mensaje-es","one.arg":"prueba un argumento {0}","pagination":"{0}-{1} de {2} clientes","plural.files":"{files, plural, =0 {category 0 - no files.} one {category one - hay un archivo en {place}.} other {category other - hay # archivos en {place}.}}"}},{"component":"sunglow","locale":"fr","messages":{"message":"Message-fr","one.arg":"tester un argument {0}","pagination":"{0} - {1} clients sur {2}","plural.files":"{files, plural, one{category one : # il y a un fichier sur {place}.} other {category other : # il n'y a pas de fichiers sur {place}.}}"}},{"component":"sunglow","locale":"zh-Hans","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}"}},{"component":"sunglow","locale":"yue","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 - 无文件。}one {category one - 在{place}上有一个文件。}other {category other - {place}上有 # 文件。}}"}},{"component":"sunglow","locale":"de","messages":{"message":"Meldung-de","one.arg":"teste ein Argument {0}","pagination":"{0} – {1} von {2} Kunden","plural.files":"{files, plural,=0 {category 0 : No files.} one {category one : # Es gibt eine Datei auf {place}.} other {category other : Es gibt # Dateien auf {place}} }"}},{"component":"sunglow","locale":"zh-Hant","messages":{"message":"訊息","one.arg":"測試一個參數{0}","pagination":"{0} - {1} 個客戶，共 {2} 個客戶","plural.files":"{files, plural,=0 {category 0 : 無檔。}one {category one : 在{place}上有一個檔。}other {category other : {place}上有 # 檔。}}"}},{"component":"sunglow","locale":"en","messages":{"message":"Message-en","one.arg":"test one argument {0}","pagination":"{0} - {1} of {2} customers","plural.files":"{files, plural,one {category one : There is one file on {place}.}other {category other : There are # files on {place}.}}"}},{"component":"sunglow","locale":"es-MX","messages":{"message":"Mensaje-es-MX","one.arg":"prueba un argumento {0}","pagination":"{0}-{1} de {2} clientes","plural.files":"{files, plural, =0 {category 0 : no files.} one {category one : hay un archivo en {place}.} other {category other : hay # archivos en {place}.}}"}},{"component":"sunglow","locale":"zh-Hans-HK","messages":{"message":"消息","one.arg":"测试一个参数{0}","pagination":"{0}-{1} 个客户，共 {2} 个","plural.files":"{files, plural,=0 {category 0 : 无文件。} =1 {category 1 : 在{place}上有且仅有一个文件。} one {category one : 在{place}上有一个文件。}other {category other : {place}上有 # 文件。}}"}},{"component":"sunglow","locale":"ja","messages":{"message":"メッセージ","one.arg":"1つの引数をテストしてください{0}","pagination":"{0} ～ {1} / {2} ユーザー","plural.files":"{files, plural,=0 {category 0 : ファイルがありません。}one {category one : {place} に1つのファイルがあります。}other {category other : その他{place}には # ファイルがあります。}}"}}]}}`,
				http.StatusOK},
		} {
			curURL := fmt.Sprintf(myURL, d.components, d.locales)

			req, _ := http.NewRequest("GET", curURL, nil)
			GinTestEngine.ServeHTTP(w, req)
		}
	}
}

func BenchmarkGetComponentMessages(b *testing.B) {
	const myURL = "/i18n/api/v2/translation/products/" + Name + "/versions/" + Version + "?components=%s&locales=%s"
	w := httptest.NewRecorder()

	for i := 0; i < b.N; i++ {
		curURL := fmt.Sprintf(myURL, "sunglow", "zh-Hans")
		req, _ := http.NewRequest("GET", curURL, nil)
		GinTestEngine.ServeHTTP(w, req)
	}
}

func BenchmarkGetPatternByLocale(b *testing.B) {
	const myURL = "/i18n/api/v2/formatting/patterns/locales/%s?scope=%s"
	w := httptest.NewRecorder()
	for i := 0; i < b.N; i++ {
		for _, d := range []struct{ locale, scope string }{
			{"en", "dates"},
			{"zh-Hans", "numbers"},
			{"es-US", "plurals"},
			{"de", "currencies,numbers"},
			{"de", "dates,currencies,numbers,plurals"},
			{"es-MX", "dateFields"},
		} {
			curURL := fmt.Sprintf(myURL, d.locale, d.scope)
			req, _ := http.NewRequest("GET", curURL, nil)
			GinTestEngine.ServeHTTP(w, req)
		}
	}
}
