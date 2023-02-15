/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"io/ioutil"
	"path/filepath"

	json "github.com/json-iterator/go"
	"github.com/pkg/errors"
)

type localSource struct {
	*bundleDAO
	root string
}

func newLocalSource(root string) *localSource {
	return &localSource{bundleDAO: &bundleDAO{root}, root: root}
}

func (s *localSource) GetComponentMessages(name, version, component string) (ComponentMsgs, error) {
	fp := filepath.Join(s.root, name, version, component, "messages.json")
	contents, err := ioutil.ReadFile(fp)
	if err != nil {
		return nil, err
	}

	b := new(bundleFile)
	err = json.Unmarshal(contents, b)
	if err != nil {
		return nil, err
	}
	if len(b.Messages) == 0 {
		return nil, errors.Errorf("Wrong data from local bundle file %s", fp)
	}

	return &MapComponentMsgs{messages: b.Messages, locale: inst.cfg.GetSourceLocale(), component: component}, nil
}
