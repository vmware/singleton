/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"net"
	"net/http"
	"net/url"
	"path"
	"strings"
	"sync/atomic"
	"time"

	json "github.com/json-iterator/go"
	"github.com/pkg/errors"
)

const serverRetryInterval = 2 // second
const (
	serverNormal uint32 = iota
	serverTimeout
)

func newServer(serverURL string) (*serverDAO, error) {
	svrURL, err := url.Parse(serverURL)
	if err != nil {
		return nil, err
	}

	s := &serverDAO{svrURL: svrURL, headers: atomic.Value{}}
	s.headers.Store(make(map[string]string, 0))

	return s, nil
}

//!+ serverDAO

type serverDAO struct {
	svrURL          *url.URL
	status          uint32
	lastErrorMoment int64
	headers         atomic.Value
}

func (s *serverDAO) Get(item *dataItem) (err error) {
	var data interface{}
	info := item.attrs.(*itemCacheInfo)

	switch item.id.iType {
	case itemComponent:
		data = new(queryProduct)
	case itemLocales:
		data = new(queryLocales)
	case itemComponents:
		data = new(queryComponents)
	default:
		return errors.Errorf(invalidItemType, item.id.iType)
	}

	urlToQuery := s.prepareURL(item)

	headers := s.getHTTPHeaders()
	headers[httpHeaderIfNoneMatch] = info.getETag()
	resp, err := s.sendRequest(urlToQuery, headers, data)
	if resp != nil {
		item.attrs = resp.Header
	}
	if err != nil {
		return err
	}

	switch item.id.iType {
	case itemComponent:
		pData := data.(*queryProduct)
		if len(pData.Bundles) != 1 || pData.Bundles[0].Messages == nil {
			return errors.New(wrongServerData)
		}
		item.data = &defaultComponentMsgs{messages: pData.Bundles[0].Messages}
	case itemLocales:
		localesData := data.(*queryLocales)
		if localesData.Locales == nil {
			return errors.New(wrongServerData)
		}
		item.data = localesData.Locales
	case itemComponents:
		componentsData := data.(*queryComponents)
		if componentsData.Components == nil {
			return errors.New(wrongServerData)
		}
		item.data = componentsData.Components
	default:
		return errors.Errorf(invalidItemType, item.id.iType)
	}

	return nil
}

func (s *serverDAO) IsExpired(item *dataItem) bool {
	info := getCacheInfo(item)
	return info.isExpired()
}

func (s *serverDAO) prepareURL(item *dataItem) *url.URL {
	urlToQuery := *s.svrURL
	var myURL string

	id := item.id
	name, version, locale, component := id.Name, id.Version, id.Locale, id.Component

	switch item.id.iType {
	case itemComponent:
		myURL = productTranslationGetConst
		addURLParams(&urlToQuery, map[string]string{localesConst: locale, componentsConst: component})
	case itemLocales:
		myURL = productLocaleListGetConst
	case itemComponents:
		myURL = productComponentListGetConst
	}

	myURL = strings.Replace(myURL, "{"+productNameConst+"}", name, 1)
	myURL = strings.Replace(myURL, "{"+versionConst+"}", version, 1)

	urlToQuery.Path = path.Join(urlToQuery.Path, myURL)

	return &urlToQuery
}

func (s *serverDAO) sendRequest(u *url.URL, header map[string]string, data interface{}) (*http.Response, error) {
	if atomic.LoadUint32(&s.status) == serverTimeout {
		if time.Now().Unix()-atomic.LoadInt64(&s.lastErrorMoment) < serverRetryInterval {
			return nil, errors.New("Server times out")
		}
		atomic.StoreUint32(&s.status, serverNormal)
	}

	resp, err := getDataFromServer(u, header, data)
	if err != nil {
		rootErr := errors.Cause(err)
		switch oe := rootErr.(type) {
		case net.Error:
			if oe.Timeout() {
				atomic.StoreUint32(&s.status, serverTimeout)
				atomic.StoreInt64(&s.lastErrorMoment, time.Now().Unix())
			}
		}

		return resp, err
	}

	return resp, nil
}

func (s *serverDAO) setHTTPHeaders(h map[string]string) {
	newHeaders := make(map[string]string, len(h))
	for k, v := range h {
		newHeaders[k] = v
	}

	s.headers.Store(newHeaders)
}

func (s *serverDAO) getHTTPHeaders() (newHeaders map[string]string) {
	originalHeaders := s.headers.Load().(map[string]string)
	newHeaders = make(map[string]string, len(originalHeaders))
	for k, v := range originalHeaders {
		newHeaders[k] = v
	}

	return
}

//!- serverDAO

//!+ common functions
func addURLParams(u *url.URL, args map[string]string) {
	values := u.Query()
	for k, v := range args {
		values.Add(k, v)
	}
	u.RawQuery = values.Encode()
}

var getDataFromServer = func(u *url.URL, header map[string]string, data interface{}) (*http.Response, error) {
	type respResult struct {
		Code       int    `json:"code"`
		Message    string `json:"message"`
		ServerTime string `json:"serverTime"`
	}
	bodyObj := &struct {
		Result    respResult `json:"response"`
		Signature string     `json:"signature"`
		Data      json.Any   `json:"data"`
	}{}

	var bodyBytes []byte
	resp, err := httpget(u.String(), header, &bodyBytes)
	if err != nil {
		return resp, err
	}

	// logger.Debug(fmt.Sprintf("resp is: %#v", resp))

	if !isHTTPSuccess(resp.StatusCode) {
		return resp, &serverError{resp.StatusCode, bodyObj.Result.Code, resp.Status, bodyObj.Result.Message}
	}

	err = json.Unmarshal(bodyBytes, bodyObj)
	if err != nil {
		return resp, errors.WithStack(err)
	}

	if !isBusinessSuccess(bodyObj.Result.Code) {
		return resp, &serverError{resp.StatusCode, bodyObj.Result.Code, resp.Status, bodyObj.Result.Message}
	}

	// newData := bodyObj.Data.MustBeValid()
	// bodyObj.Data.ToVal(newData)

	bodyObj.Data.ToVal(data)

	//logger.Debug(fmt.Sprintf("decoded data is: %#v", data))

	return resp, nil
}

func isHTTPSuccess(code int) bool {
	return code >= 200 && code < 300
}

func isBusinessSuccess(code int) bool {
	return code >= 200 && code < 300
}

//!- common functions

//!+ REST API Response structures
type (
	queryProduct struct {
		Name       string   `json:"productName"`
		Version    string   `json:"version"`
		Locales    []string `json:"locales"`
		Components []string `json:"components"`
		Bundles    []struct {
			Component string            `json:"component"`
			Messages  map[string]string `json:"messages"`
			Locale    string            `json:"locale"`
		} `json:"bundles"`
		URL    string `json:"url"`
		Status string `json:"status"`
		ID     int    `json:"id"`
	}

	queryComponents struct {
		Components []string `json:"components"`
		Version    string   `json:"version"`
		Name       string   `json:"productName"`
	}
	queryLocales struct {
		Locales []string `json:"locales"`
		Version string   `json:"version"`
		Name    string   `json:"productName"`
	}
)

//!- REST API Response structures
