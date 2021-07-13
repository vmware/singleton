/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package formatting

import (
	"context"
	"time"

	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/coreutil"

	"github.com/xiaochao8/format/date"
	"golang.org/x/text/language"
)

// SimpleFormatDateTime
func SimpleFormatDateTime(ctx context.Context, tm time.Time, pattern, locale string) (string, error) {
	cldrLocale := coreutil.GetCLDRLocale(locale)
	if len(cldrLocale) == 0 {
		err := sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidLocale, locale)
		logger.FromContext(ctx).Error(err.Error())
		return "", err
	}

	return date.Format(language.Make(cldrLocale), tm, pattern), nil
}
