/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package localeutil

import (
	"context"

	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/coreutil"

	jsoniter "github.com/json-iterator/go"
)

type LocaleTerritories struct {
	Language          string       `json:"language"`
	DefaultRegionCode string       `json:"defaultRegionCode"`
	Territories       jsoniter.Any `json:"territories"`
}

func GetPatternData(ctx context.Context, locale string, catg string) (data jsoniter.Any, err error) {
	err = GetLocaleData(ctx, locale, catg, &data)
	return data, err
}

func GetContextTransforms(ctx context.Context, locale string) (map[string]interface{}, error) {
	var data map[string]interface{}
	err := GetLocaleData(ctx, locale, cldr.ContextTransform, &data)
	return data, err
}

func GetLocaleDefaultRegion(ctx context.Context, locale string) (string, error) {
	territories, err := GetLocaleTerritories(ctx, locale)
	if err != nil {
		return "", err
	}

	return territories.DefaultRegionCode, nil
}

func GetLocaleLanguages(ctx context.Context, locale string) (map[string]string, error) {
	var data map[string]string
	err := GetLocaleData(ctx, locale, cldr.LocaleLanguages, &data)
	return data, err
}

func GetLocaleTerritories(ctx context.Context, locale string) (data *LocaleTerritories, err error) {
	data = new(LocaleTerritories)
	err = GetLocaleData(ctx, locale, cldr.LocaleTerritories, data)
	return data, err
}

func GetTerritoriesOfMultipleLocales(ctx context.Context, locales []string) (territoryList []*LocaleTerritories, err error) {
	var returnErr *sgtnerror.MultiError

	for _, locale := range locales {
		var territories *LocaleTerritories
		cldrLocale := coreutil.GetCLDRLocale(locale)
		if cldrLocale == "" {
			err = sgtnerror.StatusNotFound.WithUserMessage(cldr.InvalidLocale, locale)
		} else {
			territories, err = GetLocaleTerritories(ctx, cldrLocale)
			if err == nil {
				territories.Language = cldrLocale
				territoryList = append(territoryList, territories)
			}
		}

		returnErr = sgtnerror.Append(returnErr, err)
	}

	return territoryList, returnErr.ErrorOrNil()
}
