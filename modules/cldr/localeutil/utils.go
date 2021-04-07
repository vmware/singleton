/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package localeutil

import (
	"context"

	jsoniter "github.com/json-iterator/go"

	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/cldrcache"
	"sgtnserver/modules/cldr/coreutil"
	"sgtnserver/modules/cldr/dao"
)

func GetContextTransforms(ctx context.Context, locale string) (map[string]interface{}, error) {
	var data map[string]interface{}
	err := cldrcache.GetLocaleData(ctx, locale, dao.ContextTransform, &data)
	return data, err
}

func GetLocaleDefaultRegion(ctx context.Context, locale string) (string, error) {
	_, territories, err := GetLocaleTerritories(ctx, locale)
	if err != nil {
		return "", err
	}

	return territories.DefaultRegionCode, nil
}

// GetLocaleLanguages ...
func GetLocaleLanguages(ctx context.Context, locale string) (map[string]string, error) {
	var data map[string]string
	err := cldrcache.GetLocaleData(ctx, locale, dao.LocaleLanguages, &data)
	return data, err
}

type LocaleTerritories struct {
	Language          string       `json:"language"`
	DefaultRegionCode string       `json:"defaultRegionCode"`
	Territories       jsoniter.Any `json:"territories"`
}

func GetLocaleTerritories(ctx context.Context, locale string) (cldrLocale string, data *LocaleTerritories, err error) {
	cldrLocale = coreutil.GetCLDRLocale(locale)
	if cldrLocale == "" {
		err = sgtnerror.StatusNotFound.WithUserMessage(cldr.InvalidLocale, locale)
		logger.FromContext(ctx).Error(err.Error())
		return
	}

	data = new(LocaleTerritories)
	err = cldrcache.GetLocaleData(ctx, cldrLocale, dao.LocaleTerritories, data)
	return cldrLocale, data, err
}

func GetTerritoriesOfMultipleLocales(ctx context.Context, locales []string) (territoryList []*LocaleTerritories, err error) {
	var returnErr *sgtnerror.MultiError
	for _, locale := range locales {
		cldrLocale, territories, err := GetLocaleTerritories(ctx, locale)
		if err == nil {
			territories.Language = cldrLocale
			territoryList = append(territoryList, territories)
		}

		returnErr = sgtnerror.Append(returnErr, err)
	}

	return territoryList, returnErr.ErrorOrNil()
}

func GetPatternData(ctx context.Context, locale string, catg string) (data jsoniter.Any, err error) {
	err = cldrcache.GetLocaleData(ctx, locale, catg, &data)
	return data, err
}
