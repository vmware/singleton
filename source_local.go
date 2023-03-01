/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

type localSource struct {
	*bundleDAO
	root string
}

func newLocalSource(root string) *localSource {
	return &localSource{bundleDAO: &bundleDAO{root}, root: root}
}

func (s *localSource) GetComponentMessages(name, version, component string) (ComponentMsgs, error) {
	return s.bundleDAO.GetComponentMessages(name, version, inst.cfg.SourceLocale, component)
}
