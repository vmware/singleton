/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package localeutil

import (
	"context"
	"strings"

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
	data = &LocaleTerritories{}
	err = GetLocaleData(ctx, locale, cldr.LocaleTerritories, data)
	return data, err
}

func GetTerritoriesOfMultipleLocales(ctx context.Context, locales []string) ([]*LocaleTerritories, error) {
	var returnErr *sgtnerror.MultiError

	var err error
	var territoryList []*LocaleTerritories = make([]*LocaleTerritories, 0, len(locales))
	for _, locale := range locales {
		var territories *LocaleTerritories
		cldrLocale := coreutil.GetCLDRLocale(locale)
		if cldrLocale == "" {
			err = sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidLocale, locale)
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

func GetLocaleCities(ctx context.Context, locale string, regions []string) (data map[string]jsoniter.Any, err error) {
	cldrLocale := coreutil.GetCLDRLocale(locale)
	if cldrLocale == "" {
		err = sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidLocale, locale)
		return
	}

	data = map[string]jsoniter.Any{}
	err = GetLocaleData(ctx, locale, cldr.LocaleCities, &data)
	if err != nil || len(regions) == 0 {
		return
	}

	newData := make(map[string]jsoniter.Any, len(regions))
	for _, region := range regions {
		newData[region] = data[strings.ToUpper(region)]
	}
	return newData, err
}
