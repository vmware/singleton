/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"encoding/json"
	"io/ioutil"
	"path/filepath"
	"strings"
)

const (
	bundlePreffix = "messages_"
	bundleSuffix  = ".json"
)

//!+bundleDAO
type bundleDAO struct {
	cfg *Config
}

func (d *bundleDAO) getComponents() ([]string, error) {
	fis, err := ioutil.ReadDir(d.cfg.LocalBundles)
	if err != nil {
		return nil, err
	}

	comps := make([]string, len(fis))
	for i, fi := range fis {
		if fi.IsDir() {
			comps[i] = fi.Name()
		}
	}

	return comps, nil
}
func (d *bundleDAO) getLocales() ([]string, error) {
	comps, err := d.getComponents()
	if err != nil {
		return nil, err
	}

	locales := map[string]struct{}{}
	for _, comp := range comps {
		fPath := filepath.Join(d.cfg.LocalBundles, comp)
		fis, err := ioutil.ReadDir(fPath)
		if err != nil {
			return nil, err
		}

		for _, fi := range fis {
			if !fi.IsDir() {
				locales[strings.ToLower(fi.Name())] = struct{}{}
			}
		}
	}

	lSlice := make([]string, 0, len(locales))
	for k := range locales {
		lSlice = append(lSlice, strings.TrimSuffix(strings.TrimPrefix(k, bundlePreffix), bundleSuffix))
	}

	return lSlice, nil
}

func (d *bundleDAO) getComponentMessages(locale, component string) (ComponentMsgs, error) {
	compDirPath := filepath.Join(d.cfg.LocalBundles, component)
	files, err := ioutil.ReadDir(compDirPath)
	if err != nil {
		return nil, err
	}

	filename := bundlePreffix + locale + bundleSuffix
	for _, f := range files {
		if !f.IsDir() && strings.ToLower(filename) == strings.ToLower(f.Name()) {
			filename = f.Name()
			break
		}
	}

	contents, err := ioutil.ReadFile(filepath.Join(compDirPath, filename))
	if err != nil {
		return nil, err
	}

	b := new(bundleFile)
	err = json.Unmarshal(contents, b)
	if err != nil {
		return nil, err
	}

	return &defaultComponentMsgs{b.Messages}, nil
}

type bundleFile struct {
	Component string            `json:"component"`
	Messages  map[string]string `json:"messages"`
	Locale    string            `json:"locale"`
}

//!-bundleDAO
