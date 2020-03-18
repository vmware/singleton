/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"net"
	"net/url"
	"path"
	"strings"
	"sync/atomic"
	"time"

	"errors"

	"github.com/mitchellh/mapstructure"
)

// serverDAO serverDAO definition
type serverDAO struct {
	svrURL          *url.URL
	status          uint32
	lastErrorMoment int64
	headers         map[string]string
}

const serverRetryInterval = 1 //second
const (
	serverNormal uint32 = iota
	serverTimeout
)

func newServer(OnlineServiceURL string) (*serverDAO, error) {
	svrURL, err := url.Parse(OnlineServiceURL)
	if err != nil {
		return nil, err
	}

	return &serverDAO{svrURL: svrURL}, nil
}

// getComponentMessages Get a component's messages
func (s *serverDAO) getComponentMessages(name, version, locale, component string) (ComponentMsgs, error) {
	urlToQuery := s.processURL(componentTranslationGetConst, name, version, locale, component)

	data := new(queryByCompData)
	if err := s.sendRequest(urlToQuery, s.headers, data); err != nil {
		return nil, err
	}

	compData := defaultComponentMsgs{messages: data.Messages}

	return &compData, nil
}

// getLocales Get supported locales
func (s *serverDAO) getLocales(name, version string) ([]string, error) {
	urlToQuery := s.processURL(productLocaleListGetConst, name, version)

	data := new(queryLocales)
	if err := s.sendRequest(urlToQuery, s.headers, data); err != nil {
		return nil, err
	}

	return data.Locales, nil
}

// getComponents Get supported components
func (s *serverDAO) getComponents(name, version string) ([]string, error) {
	urlToQuery := s.processURL(productComponentListGetConst, name, version)

	data := new(queryComponents)
	if err := s.sendRequest(urlToQuery, s.headers, data); err != nil {
		return nil, err
	}

	return data.Components, nil
}

func (s *serverDAO) addHTTPHeaders(h map[string]string) {
	s.headers = h
}

func (s *serverDAO) processURL(relURL string, args ...string) *url.URL {
	newRelURL := strings.Replace(relURL, "{"+productNameConst+"}", args[0], 1)
	newRelURL = strings.Replace(newRelURL, "{"+versionConst+"}", args[1], 1)

	urlToQuery := url.URL(*s.svrURL)
	urlToQuery.Path = path.Join(urlToQuery.Path, newRelURL)

	switch relURL {
	case componentTranslationGetConst:
		urlToQuery.Path = strings.Replace(urlToQuery.Path, "{"+localeConst+"}", args[2], 1)
		urlToQuery.Path = strings.Replace(urlToQuery.Path, "{"+componentConst+"}", args[3], 1)
	}

	return &urlToQuery
}
func (s *serverDAO) sendRequest(u *url.URL, header map[string]string, data interface{}) error {
	if atomic.LoadUint32(&s.status) == serverTimeout {
		if time.Now().Unix()-atomic.LoadInt64(&s.lastErrorMoment) < serverRetryInterval {
			return errors.New("Server times out")
		}
		atomic.StoreUint32(&s.status, serverNormal)
	}

	err := getDataFromServer(u, header, data)
	if err != nil {
		if oe, ok := err.(net.Error); ok {
			if oe.Timeout() {
				atomic.StoreUint32(&s.status, serverTimeout)
				atomic.StoreInt64(&s.lastErrorMoment, time.Now().Unix())
			}
		}

		return err
	}

	return nil
}

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

var getDataFromServer = func(u *url.URL, header map[string]string, data interface{}) error {
	respData := new(respBody)
	err := httpget(u.String(), header, respData)
	if err != nil {
		return err
	}

	if !isBusinessSuccess(respData.Result.Code) {
		return &sgtnError{businessError, respData.Result.Code, respData.Result.Message, nil}
	}

	if err = mapstructure.Decode(respData.Data, &data); err != nil {
		return err
	}

	return nil
}

func isBusinessSuccess(code int) bool {
	// return code >= 600 && code < 700
	return true
}

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

	queryByCompData struct {
		Name      string            `json:"productName"`
		Version   string            `json:"version"`
		Component string            `json:"component"`
		Messages  map[string]string `json:"messages"`
		Locale    string            `json:"locale"`
		Status    string            `json:"status"`
		ID        int               `json:"id"`
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
