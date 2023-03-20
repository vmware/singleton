/*
 * Copyright 2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

//stgn ...
package sgtn

import (
	"github.com/pkg/errors"
)

type sourceLocaleVerification struct {
	messageOrigin
}

func (sc *sourceLocaleVerification) Get(item *dataItem) (err error) {
	if item.id.iType == itemComponent && item.id.Locale != inst.cfg.SourceLocale {
		return errors.Errorf("unsupported locale %q", item.id.Locale)
	}

	return sc.messageOrigin.Get(item)
}
