/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"fmt"
	"net"
	"net/url"
	"path"
	"strings"
	"sync/atomic"
	"time"

	"errors"

	"github.com/mitchellh/mapstructure"
)

type (
	// serverDAO serverDAO definition
	serverDAO struct {
		cfg         *Config
		svrURL      *url.URL
		status      uint32
		errormoment int64
		headers     map[string]string
	}

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

const serverRetryInter = 1 //second
const (
	serverNormal uint32 = iota
	serverTimeout
)

func newServer(cfg *Config) (*serverDAO, error) {
	svrURL, err := url.Parse(cfg.SingletonServer)
	if err != nil {
		return nil, err
	}

	return &serverDAO{cfg: cfg, svrURL: svrURL}, nil
}

// getComponentMessages Get a component's messages
func (s *serverDAO) getComponentMessages(locale, component string) (ComponentMsgs, error) {
	urlToQuery := s.processURL(componentTranslationGetConst, locale, component)

	data := new(queryByCompData)
	if err := s.sendRequest(urlToQuery, s.headers, data); err != nil {
		return nil, err
	}

	compData := defaultComponentMsgs{messages: data.Messages}

	return &compData, nil
}

// getLocales Get supported locales
func (s *serverDAO) getLocales() ([]string, error) {
	urlToQuery := s.processURL(productLocaleListGetConst)

	data := new(queryLocales)
	if err := s.sendRequest(urlToQuery, s.headers, data); err != nil {
		return nil, err
	}

	return data.Locales, nil
}

// getComponents Get supported components
func (s *serverDAO) getComponents() ([]string, error) {
	urlToQuery := s.processURL(productComponentListGetConst)

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
	newRelURL := strings.Replace(relURL, "{"+productNameConst+"}", s.cfg.Name, 1)
	newRelURL = strings.Replace(newRelURL, "{"+versionConst+"}", s.cfg.Version, 1)

	urlToQuery := url.URL(*s.svrURL)
	urlToQuery.Path = path.Join(urlToQuery.Path, newRelURL)

	switch relURL {
	case componentTranslationGetConst:
		urlToQuery.Path = strings.Replace(urlToQuery.Path, "{"+localeConst+"}", args[0], 1)
		urlToQuery.Path = strings.Replace(urlToQuery.Path, "{"+componentConst+"}", args[1], 1)
	}

	return &urlToQuery
}
func (s *serverDAO) sendRequest(u *url.URL, header map[string]string, data interface{}) error {
	if atomic.LoadUint32(&s.status) == serverTimeout {
		if time.Now().Unix()-atomic.LoadInt64(&s.errormoment) < serverRetryInter {
			return errors.New("Server times out")
		}
		atomic.StoreUint32(&s.status, serverNormal)
	}

	err := getDataFromServer(u, header, data)
	if err != nil {
		if oe, ok := err.(net.Error); ok {
			if oe.Timeout() {
				atomic.StoreUint32(&s.status, serverTimeout)
				atomic.StoreInt64(&s.errormoment, time.Now().Unix())
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

	if !isSuccess(respData.Result.Code) {
		err = fmt.Errorf("Fail to load from server. The code is: %d, message is: %s", respData.Result.Code, respData.Result.Message)
		return err
	}

	if err = mapstructure.Decode(respData.Data, &data); err != nil {
		return err
	}

	return nil
}
