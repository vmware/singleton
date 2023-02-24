/*
 * Copyright 2020-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"io/ioutil"
	"os"
	"path/filepath"
	"strings"

	json "github.com/json-iterator/go"
	"github.com/pkg/errors"
)

const (
	bundlePrefix = "messages_"
	bundleSuffix = ".json"
)

type bundleFile struct {
	Component string            `json:"component"`
	Messages  map[string]string `json:"messages"`
	Locale    string            `json:"locale"`
}

//!+bundleDAO
type bundleDAO struct {
	root string
}

func (d *bundleDAO) Get(item *dataItem) (err error) {
	id := item.id
	switch id.iType {
	case itemComponent:
		item.data, err = d.GetComponentMessages(id.Name, id.Version, id.Locale, id.Component)
	case itemLocales:
		item.data, err = d.GetLocaleList(id.Name, id.Version)
	case itemComponents:
		item.data, err = d.GetComponentList(id.Name, id.Version)
	default:
		err = errors.Errorf(invalidItemType, item.id.iType)
	}

	if err == nil {
		item.origin = d
		item.attrs = newSingleCacheInfo()
	}
	return
}

func (d *bundleDAO) IsExpired(*dataItem) bool {
	return false
}

func (d *bundleDAO) GetComponentList(name, version string) ([]string, error) {
	fis, err := ioutil.ReadDir(d.getReleasePath(name, version))
	if err != nil {
		return nil, errors.WithStack(err)
	}

	comps := make([]string, 0, len(fis))
	for _, fi := range fis {
		if fi.IsDir() {
			comps = append(comps, fi.Name())
		}
	}

	return comps, nil
}

func (d *bundleDAO) GetLocaleList(name, version string) ([]string, error) {
	fileNames := map[string]struct{}{}
	err := filepath.Walk(d.getReleasePath(name, version), func(path string, info os.FileInfo, err error) error {
		if err != nil {
			return err
		}
		if !info.IsDir() {
			fileNames[info.Name()] = struct{}{}
		}
		return nil
	})
	if err != nil {
		return nil, errors.WithStack(err)
	}

	lSlice := make([]string, 0, len(fileNames))
	for fileName := range fileNames {
		if strings.HasPrefix(fileName, bundlePrefix) && strings.HasSuffix(fileName, bundleSuffix) {
			localeToSave := fileName[len(bundlePrefix) : len(fileName)-len(bundleSuffix)]
			if !strings.EqualFold(localeToSave, localeLatest) {
				lSlice = append(lSlice, localeToSave)
			}
		}
	}

	return lSlice, nil
}

func (d *bundleDAO) GetComponentMessages(name, version, locale, component string) (ComponentMsgs, error) {
	compDirPath := filepath.Join(d.root, name, version, component)
	files, err := ioutil.ReadDir(compDirPath)
	if err != nil {
		return nil, errors.WithStack(err)
	}

	filename := bundlePrefix + locale + bundleSuffix
	for _, f := range files {
		if !f.IsDir() && strings.EqualFold(filename, f.Name()) {
			filename = f.Name()
			break
		}
	}

	contents, err := ioutil.ReadFile(filepath.Join(compDirPath, filename))
	if err != nil {
		return nil, errors.WithStack(err)
	}

	b := new(bundleFile)
	err = json.Unmarshal(contents, b)
	if err != nil {
		return nil, errors.WithStack(err)
	}
	if len(b.Messages) == 0 {
		return nil, errors.New("Wrong data from local bundle file")
	}

	return &MapComponentMsgs{messages: b.Messages, locale: convertLocale(locale), component: component}, nil
}

func (d *bundleDAO) getReleasePath(name, version string) string {
	return filepath.Join(d.root, name, version)
}

//!-bundleDAO
