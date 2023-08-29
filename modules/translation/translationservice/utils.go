/*
 * Copyright 2022-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translationservice

import (
	"context"
	"strconv"
	"strings"

	"sgtnserver/internal/bindata"
	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"
	"sgtnserver/modules/cldr"
	"sgtnserver/modules/cldr/coreutil"
	"sgtnserver/modules/translation/bundleinfo"
	"sgtnserver/modules/translation/dao"

	"github.com/emirpasic/gods/sets"
	jsoniter "github.com/json-iterator/go"
	"go.uber.org/zap"
)

var json = jsoniter.ConfigDefault

var localeMap map[string]string

func PickupLocales(name, version string, locales []string) []string {
	localeCandidates, ok := bundleinfo.GetLocaleNames(name, version)
	if !ok {
		return locales
	}

	pickedLocales := make([]string, len(locales))
	for i, locale := range locales {
		if pickedLocale := PickupLocaleFromList(localeCandidates, locale); pickedLocale != "" {
			pickedLocales[i] = pickedLocale
		} else {
			pickedLocales[i] = locale
		}
	}
	return pickedLocales
}

func PickupLocaleFromList(locales sets.Set, preferredLocale string) string {
	if locales.Contains(preferredLocale) {
		return preferredLocale
	}

	originalLocale := strings.ReplaceAll(preferredLocale, "_", cldr.LocalePartSep)
	if v, ok := localeMap[strings.ToLower(originalLocale)]; ok {
		originalLocale = v
	}

	normalizedOriginalLocale := coreutil.ParseLocale(originalLocale)
	if normalizedOriginalLocale == nil {
		return ""
	}

	chosenLocale := ""
	for _, locale := range locales.Values() {
		inLoopLocale := locale.(string)
		if strings.EqualFold(originalLocale, inLoopLocale) {
			return inLoopLocale
		}

		normalizedInLoopLocale := coreutil.ParseLocale(inLoopLocale)
		if normalizedInLoopLocale == nil {
			continue
		}
		if strings.EqualFold(normalizedInLoopLocale.Language, normalizedOriginalLocale.Language) {
			chosenLocale = normalizedInLoopLocale.Language
			script := normalizedInLoopLocale.Scripts
			if script != "" && strings.EqualFold(script, normalizedOriginalLocale.Scripts) {
				chosenLocale = normalizedOriginalLocale.Language + cldr.LocalePartSep + script
			}
			region := normalizedInLoopLocale.Region
			if region != "" && strings.EqualFold(region, normalizedOriginalLocale.Region) {
				chosenLocale = chosenLocale + cldr.LocalePartSep + region
			}
		}
	}

	return chosenLocale
}

// PickupVersion version fallback
func PickupVersion(name, desiredVersion string) string {
	versions, ok := bundleinfo.GetReleaseNames(name)
	if !ok {
		return desiredVersion
	}
	if versions.Contains(desiredVersion) {
		return desiredVersion
	}

	desiredVersionObj := newVersion(desiredVersion)
	var chosen *Version
	for _, v := range versions.Values() {
		vo := newVersion(v.(string))
		if vo.Less(desiredVersionObj) <= 0 {
			if chosen == nil {
				chosen = vo
			} else if chosen.Less(vo) < 0 {
				chosen = vo
			}
		}
	}
	if chosen != nil {
		return chosen.version
	}
	return desiredVersion
}

var allowList = map[string]interface{}{}

func InitAllowList() {
	if config.Settings.AllowListFile == "" {
		return
	}

	err := dao.GetInst().ReadJSONFile(context.TODO(), config.Settings.AllowListFile, &allowList)
	if err != nil {
		logger.Log.Fatal("fail to read allowlist file", zap.String("path", config.Settings.AllowListFile), zap.Error(err))
	}
}

func IsProductAllowed(name string) (found bool) {
	_, found = allowList[name]
	return
}

func initLocaleMap() {
	logger.Log.Debug("Initialize locale mapping")

	const localeMapFile = "localemap.json"
	bts, err := bindata.Asset(localeMapFile)
	if err == nil {
		err = json.Unmarshal(bts, &localeMap)
	}
	if err != nil {
		logger.Log.Error("Failed to read locale mapping file", zap.String("file", localeMapFile), zap.Error(err))
	}
}

func convertSetToList(s sets.Set) (result []string) {
	for _, e := range s.Values() {
		result = append(result, e.(string))
	}

	return
}

type Version struct {
	version     string
	parts       []string
	numberParts []int
}

func newVersion(s string) *Version {
	parts := strings.Split(s, ".")
	numberParts := make([]int, len(parts))
	for i, p := range parts {
		numberParts[i], _ = strconv.Atoi(p)
	}

	return &Version{s, parts, numberParts}
}

func (v *Version) Less(another *Version) int {
	minNumber := len(v.numberParts)
	if minNumber > len(another.numberParts) {
		minNumber = len(another.numberParts)
	}

	for k := 0; k < minNumber; k++ {
		if v.numberParts[k] == another.numberParts[k] {
			continue
		}
		return v.numberParts[k] - another.numberParts[k]
	}

	for i := minNumber; i < len(v.numberParts); i++ {
		if v.numberParts[i] > 0 {
			return 1
		}
	}
	for i := minNumber; i < len(another.numberParts); i++ {
		if another.numberParts[i] > 0 {
			return -1
		}
	}

	return 0
}
