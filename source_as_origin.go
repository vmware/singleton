/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

// For local source and registered source
type sourceAsOrigin struct {
	source
}

func (o sourceAsOrigin) Get(item *dataItem) (err error) {
	id := item.id
	switch id.iType {
	case itemComponent:
		item.data, err = o.source.GetComponentMessages(id.Name, id.Version, id.Component)
	case itemLocales:
		item.data, err = []string{inst.cfg.SourceLocale}, nil
	case itemComponents:
		item.data, err = o.source.GetComponentList(id.Name, id.Version)
	}

	if err == nil {
		item.origin = o
		item.attrs = newSingleCacheInfo()
	}
	return
}

func (o sourceAsOrigin) IsExpired(item *dataItem) bool {
	return false
}
