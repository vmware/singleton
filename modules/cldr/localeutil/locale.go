/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package localeutil

import (
	"context"
	"strings"

	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/coreutil"
	"sgtnserver/modules/cldr/dao"
	"sgtnserver/modules/cldr/localeutil/cache"
)

var (
	EnableCache = true

	dataOrigin localeDataOrigin
)

type localeDataOrigin interface {
	GetLocaleData(ctx context.Context, locale, dataType string, data interface{}) error
}

func GetLocaleData(ctx context.Context, locale, dataType string, data interface{}) (err error) {
	cldrLocale, ok := coreutil.AvailableLocalesMap[strings.ToLower(locale)]
	if !ok {
		err := sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidLocale, locale)
		logger.FromContext(ctx).Error(err.Error())
		return err
	}

	return dataOrigin.GetLocaleData(ctx, cldrLocale, dataType, data)
}

func init() {
	if EnableCache {
		dataOrigin = cache.GetCache()
	} else {
		dataOrigin = dao.GetDAO()
	}
}
