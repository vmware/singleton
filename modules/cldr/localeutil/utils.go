/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package localeutil

import (
	"context"
	"net/http"
	"strings"
	"sync"

	jsoniter "github.com/json-iterator/go"

	"sgtnserver/internal/logger"
	"sgtnserver/internal/sgtnerror"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/cldrcache"
	"sgtnserver/modules/cldr/coreutil"
	"sgtnserver/modules/cldr/dao"
)

var localeDao cldrLocaleDAO

var EnableCache = false

var nonexistentMap sync.Map

type cldrLocaleDAO interface {
	GetLocaleData(ctx context.Context, locale, dataType string, data interface{}) error
}

func GetLocaleData(ctx context.Context, locale, dataType string, data interface{}) (err error) {
	cldrLocale, ok := coreutil.AvailableLocalesMap[strings.ToLower(locale)]
	if !ok {
		err := sgtnerror.StatusNotFound.WithUserMessage(cldr.InvalidLocale, locale)
		logger.FromContext(ctx).Error(err.Error())
		return err
	}

	cacheKey := dataType + ":" + cldrLocale
	if _, ok := nonexistentMap.Load(cacheKey); ok {
		return sgtnerror.StatusNotFound.WithUserMessage("Locale is '%s', type is %v", locale, dataType)
	}

	err = localeDao.GetLocaleData(ctx, cldrLocale, dataType, data)
	if sgtnerror.GetCode(err) == http.StatusNotFound {
		nonexistentMap.Store(cacheKey, nil) // this is for contextTransforms, only part of locales have this data. Save result to avoid querying from storage repeatedly
	}
	return err
}

func GetContextTransforms(ctx context.Context, locale string) (map[string]interface{}, error) {
	var data map[string]interface{}
	err := GetLocaleData(ctx, locale, cldr.ContextTransform, &data)
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
	err := GetLocaleData(ctx, locale, cldr.LocaleLanguages, &data)
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
	err = GetLocaleData(ctx, cldrLocale, cldr.LocaleTerritories, data)
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
	err = GetLocaleData(ctx, locale, catg, &data)
	return data, err
}

func init() {
	if EnableCache {
		localeDao = cldrcache.GetCache()
	} else {
		localeDao = dao.GetDAO()
	}
}
