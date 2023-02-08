/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"net"
	"net/http"
	"net/url"
	"regexp"
	"strconv"
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
	info := item.attrs

	var urlToQuery string
	switch item.id.iType {
	case itemComponent:
		data = new(queryBundle)
		urlToQuery = s.svrURL.String() + fmt.Sprintf(bundleGetConst, item.id.Name, item.id.Version, item.id.Locale, item.id.Component)
	case itemLocales:
		data = new(queryLocales)
		urlToQuery = s.svrURL.String() + fmt.Sprintf(productLocaleListGetConst, item.id.Name, item.id.Version)
	case itemComponents:
		data = new(queryComponents)
		urlToQuery = s.svrURL.String() + fmt.Sprintf(productComponentListGetConst, item.id.Name, item.id.Version)
	default:
		return errors.Errorf(invalidItemType, item.id.iType)
	}

	headers := s.getHTTPHeaders()
	headers[httpHeaderIfNoneMatch] = info.getETag()
	resp, err := s.sendRequest(urlToQuery, headers, data)
	if !isSuccess(err) {
		return err
	}
	item.attrs = newSingleCacheInfo()
	item.origin = s
	updateCacheControl(resp.Header, item.attrs)
	if err == nil { // http code 200
		item.attrs.setETag(resp.Header.Get(httpHeaderETag))
	} else { // http code 304
		item.attrs.setETag(info.eTag)
		// clone to do source comparison
		item.data = item.data.(ComponentMsgs).Clone()
		return nil
	}

	switch item.id.iType {
	case itemComponent:
		bData := data.(*queryBundle)
		item.data = &MapComponentMsgs{messages: bData.Messages, locale: convertLocale(item.id.Locale), component: item.id.Component}
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
	return item.attrs.isExpired()
}

func (s *serverDAO) sendRequest(u string, header map[string]string, data interface{}) (*http.Response, error) {
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
	}

	return resp, err
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

var getDataFromServer = func(u string, header map[string]string, data interface{}) (*http.Response, error) {
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
	resp, err := httpget(u, header, &bodyBytes)
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
	return (code >= 200 && code < 300) || (code >= 600 && code < 700)
}

//!- common functions

var cacheControlRE = regexp.MustCompile(`(?i)\bmax-age\b\s*=\s*\b(\d+)\b`)

func updateCacheControl(headers http.Header, info *itemCacheInfo) {
	if len(headers) == 0 || info == nil {
		return
	}

	cc := headers.Get(httpHeaderCacheControl)
	results := cacheControlRE.FindStringSubmatch(cc)
	if len(results) == 2 {
		age, parseErr := strconv.ParseInt(results[1], 10, 64)
		if parseErr == nil {
			info.setAge(age)
			return
		}
	}

	info.setAge(cacheDefaultExpires)
	logger.Warn("Wrong cache control: " + cc)
}

func isSuccess(err error) bool {
	if err != nil {
		myErr, ok := err.(*serverError)
		if !ok || myErr.code != http.StatusNotModified {
			return false
		}
	}

	return true
}

//!+ REST API Response structures
type (
	queryBundle struct {
		Name      string            `json:"productName"`
		Version   string            `json:"version"`
		Locale    string            `json:"locale"`
		Component string            `json:"component"`
		Messages  map[string]string `json:"messages"`
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
