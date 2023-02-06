/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

type InTranslationSource struct {
	messageOrigin
}

func (s *InTranslationSource) Get(item *dataItem) (err error) {
	switch item.id.iType {
	case itemComponent:
		item.id.Locale = localeLatest
		err = s.messageOrigin.Get(item)
		item.id.Locale = inst.cfg.GetSourceLocale()
	case itemLocales:
		// usually locale list is from translation. This is used when only source exists.
		// cache information never expires because source locale is the only one locale.
		item.data = []string{inst.cfg.GetSourceLocale()}
	case itemComponents:
		err = s.messageOrigin.Get(item)
	}

	item.origin = s
	return
}
