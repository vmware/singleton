/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"encoding/json"
	"net"
	"net/http"
	"net/url"
	"path"
	"strings"
	"sync/atomic"
	"time"

	"github.com/mitchellh/mapstructure"
	"github.com/pkg/errors"
)

const serverRetryInterval = 2 //second
const (
	serverNormal uint32 = iota
	serverTimeout
)

func newServer(OnlineServiceURL string) (*serverDAO, error) {
	svrURL, err := url.Parse(OnlineServiceURL)
	if err != nil {
		return nil, err
	}

	s := &serverDAO{svrURL: svrURL, headers: map[string]string{}}
	return s, nil
}

//!+ serverDAO
// serverDAO serverDAO definition
type serverDAO struct {
	svrURL          *url.URL
	status          uint32
	lastErrorMoment int64
	headers         map[string]string
}

func (s *serverDAO) get(item *dataItem) (err error) {
	var data interface{}
	info := getCacheInfo(item)

	switch item.iType {
	case itemComponent:
		data = new(queryProduct)
	case itemLocales:
		data = new(queryLocales)
	case itemComponents:
		data = new(queryComponents)
	default:
		return errors.Errorf("Invalid item type: %s", item.iType)
	}

	urlToQuery := s.prepareURL(item)

	s.headers[httpHeaderIfNoneMatch] = info.getETag()
	defer delete(s.headers, httpHeaderIfNoneMatch)
	resp, err := s.sendRequest(urlToQuery, s.headers, data)
	if resp != nil {
		item.attrs = resp.Header
	}
	if err != nil {
		return err
	}

	switch item.iType {
	case itemComponent:
		pData := data.(*queryProduct)
		if len(pData.Bundles) != 1 {
			return errors.New("Wrong data from server")
		}
		item.data = &defaultComponentMsgs{messages: pData.Bundles[0].Messages}
	case itemLocales:
		localesData := data.(*queryLocales)
		item.data = localesData.Locales
	case itemComponents:
		componentsData := data.(*queryComponents)
		item.data = componentsData.Components
	default:
		return errors.Errorf("Invalid item type: %s", item.iType)
	}

	// fmt.Printf("item to return: \n%#v\n", item)

	return nil
}

func (s *serverDAO) prepareURL(item *dataItem) *url.URL {
	urlToQuery := url.URL(*s.svrURL)
	var myURL, name, version, locale, component string
	switch item.iType {
	case itemComponent:
		id := item.id.(componentID)
		myURL = productTranslationGetConst
		name, version, locale, component = id.Name, id.Version, id.Locale, id.Component
		addURLParams(&urlToQuery, map[string]string{localesConst: locale, componentsConst: component})
	case itemLocales:
		id := item.id.(translationID)
		name, version = id.Name, id.Version
		myURL = productLocaleListGetConst
	case itemComponents:
		id := item.id.(translationID)
		name, version = id.Name, id.Version
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
		if oe, ok := err.(net.Error); ok {
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
	s.headers = h
}

//!- serverDAO

//!+ common functions
func addURLParam(u *url.URL, k, v string) {
	addURLParams(u, map[string]string{k: v})
}

func addURLParams(u *url.URL, args map[string]string) {
	values := u.Query()
	for k, v := range args {
		values.Add(k, v)
	}
	u.RawQuery = values.Encode()
}

var getDataFromServer = func(u *url.URL, header map[string]string, data interface{}) (*http.Response, error) {
	bodyObj := new(respBody)
	var bodyBytes []byte
	resp, err := httpget(u.String(), header, &bodyBytes)
	if err != nil {
		return resp, err
	}

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

	if err = mapstructure.Decode(bodyObj.Data, &data); err != nil {
		return resp, errors.WithStack(err)
	}

	//logger.Debug(fmt.Sprintf("decoded data is: %#v", data))

	return resp, nil
}

func isHTTPSuccess(code int) bool {
	return code >= 200 && code < 300
}

func isBusinessSuccess(code int) bool {
	// return code >= 600 && code < 700
	return code >= 200 && code < 300
}

//!- common functions

//!+ REST API Response structures
type (
	respBody struct {
		Result    respResult  `json:"response"`
		Signature string      `json:"signature"`
		Data      interface{} `json:"data"`
	}

	respResult struct {
		Code       int    `json:"code"`
		Message    string `json:"message"`
		ServerTime string `json:"serverTime"`
	}

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
