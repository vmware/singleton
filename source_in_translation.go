/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

type sourceInTranslation struct {
	messageOrigin
}

func (s *sourceInTranslation) Get(item *dataItem) (err error) {
	switch item.id.iType {
	case itemComponent:
		defer func() { item.id.Locale = inst.cfg.GetSourceLocale() }()
		item.id.Locale = localeLatest
		err = s.messageOrigin.Get(item)
	// case itemLocales:
		// this case is impossible because locales will come from translation.
		// item.data = []string{inst.cfg.GetSourceLocale()}
	case itemComponents:
		err = s.messageOrigin.Get(item)
	}

	if err == nil {
		item.origin = s
	}
	return
}
