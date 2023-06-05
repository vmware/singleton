/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package localeutil

import (
	"context"

	"sgtnserver/internal/common"
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

type LocaleTimeZoneNames struct {
	Language      string                 `json:"language"`
	TimeZoneNames map[string]interface{} `json:"timeZoneNames"`
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

func GetTimeZoneNames(ctx context.Context, locale string, defaultTerritory bool) (data *LocaleTimeZoneNames, err error) {
	cldrLocale := coreutil.GetCLDRLocale(locale)
	if cldrLocale == "" {
		err = sgtnerror.StatusBadRequest.WithUserMessage(cldr.InvalidLocale, locale)
		return
	}

	data = new(LocaleTimeZoneNames)
	err = GetLocaleData(ctx, cldrLocale, cldr.TimeZoneName, data)
	if err != nil || !defaultTerritory {
		return
	}

	const metaZonesKey = "metaZones"
	metaZones := data.TimeZoneNames[metaZonesKey].([]interface{})
	newMetaZones := make([]interface{}, 0, len(metaZones))
	for _, metaZone := range metaZones {
		if _, ok := metaZone.(map[string]interface{})["mapZones"]; ok {
			newMetaZones = append(newMetaZones, metaZone)
		}
	}
	data = &LocaleTimeZoneNames{Language: data.Language, TimeZoneNames: common.CopyMap(data.TimeZoneNames)}
	data.TimeZoneNames[metaZonesKey] = newMetaZones

	return
}
