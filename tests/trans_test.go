/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package tests

import (
	"context"
	"fmt"
	"os"
	"path"
	"testing"

	"sgtnserver/internal/cache"
	"sgtnserver/internal/config"
	"sgtnserver/modules/translation"
	"sgtnserver/modules/translation/bundleinfo"
	"sgtnserver/modules/translation/translationservice"

	"github.com/emirpasic/gods/sets/linkedhashset"
	jsoniter "github.com/json-iterator/go"
	"github.com/pkg/fileutils"
	"github.com/stretchr/testify/assert"
)

var l3Service = translationservice.GetService()

func TestVersionFallbackFunc(t *testing.T) {
	for _, d := range []struct{ input, wanted string }{
		{".", "."},
		{"a", "a"},
		{"0", "0"},
		{"0.0", "0.0"},
		{"0.0.1.0", "0.0.1.0"},
		{"1", "1.0.0"},
		{"1.0.", "1.0.0"},
		{"1.0", "1.0.0"},
		{"1.0.0", "1.0.0"},
		{"1.0.1", "1.0.1"},
		{"1.0.1.0", "1.0.1"},
		{"1.0.1.1", "1.0.1"},
		{"1.0.2", "1.0.1"},
		{"1.1.0", "1.0.1"},
		{"1.1", "1.0.1"},
		{"1.1.1", "1.1.1"},
		{"2.1.0", "1.1.1"},
		{"1.0.0.0", "1.0.0"},
		{"1.0.0.1", "1.0.0"},
	} {
		d := d

		t.Run(d.input, func(t *testing.T) {
			actual := translationservice.PickupVersion(Name, d.input)
			assert.Equal(t, d.wanted, actual)
		})
	}
}

func TestVersionFallbackFuncInvalidProduct(t *testing.T) {
	version := "2.1.1"
	actual := translationservice.PickupVersion("invalid", version)
	assert.Equal(t, version, actual)
}

func TestTransCache(t *testing.T) {
	locale := "vi"
	viFilePath := path.Join(config.Settings.LocalBundle.BasePath, Name, Version, Component, translation.GetBundleFilename(locale))
	zhFilePath := path.Join(config.Settings.LocalBundle.BasePath, Name, Version, Component, translation.GetBundleFilename("zh-Hans"))

	l3Service.ClearCache(context.TODO())

	c, ok := cache.GetCache("translation")
	assert.True(t, ok)

	// Query first to make sure vi doesn't exist
	id := &translation.BundleID{Name: Name, Version: Version, Locale: locale, Component: Component}
	_, err := l3Service.GetBundle(context.TODO(), id)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), "Fail to get translation for")

	err = fileutils.CopyFile(viFilePath, zhFilePath)
	assert.Nil(t, err)
	defer func() {
		os.Remove(viFilePath)
		l3Service.ClearCache(context.TODO())
	}()

	err = l3Service.ClearCache(context.TODO())
	assert.Nil(t, err)

	// Query again to populate cache
	_, err = l3Service.GetBundle(context.TODO(), id)
	assert.Nil(t, err)

	// query from cache to check entry exists
	_, err = c.Get(fmt.Sprintf("%s:%s:%s:%s", id.Name, id.Version, id.Locale, id.Component))
	assert.Nil(t, err)
}

func TestCacheMaxEntities(t *testing.T) {
	c, ok := cache.GetCache("translation")
	assert.True(t, ok)

	realCache := c.(*cache.RistrettoCache).Cache
	oldMax := realCache.MaxCost()
	realCache.UpdateMaxCost(1)
	defer func() { realCache.UpdateMaxCost(oldMax) }()

	id := ID

	// Query to populate cache
	_, err := l3Service.GetBundle(context.TODO(), &id)
	assert.Nil(t, err)

	// query from cache to check entry exists
	oldKey := fmt.Sprintf("%s:%s:%s:%s", id.Name, id.Version, id.Locale, id.Component)
	_, err = c.Get(oldKey)
	assert.Nil(t, err)

	newID := ID
	newID.Locale = "zh-Hans"
	newKey := fmt.Sprintf("%s:%s:%s:%s", newID.Name, newID.Version, newID.Locale, newID.Component)

	// update cache to save newID
	_, err = l3Service.GetBundle(context.TODO(), &newID)
	assert.Nil(t, err)

	// new ID should exist
	_, err = c.Get(newKey)
	assert.Nil(t, err)

	// old ID should not exist
	_, err = c.Get(oldKey)
	assert.NotNil(t, err)
}

func TestTransExceptionArgs(t *testing.T) {
	var err error
	invalidName, invalidVersion, invalidLocale, invalidComponent := "product_invalid", "version_invalid", "locale_invalid", "component_invalid"
	invalidKey := "invalid_key"
	normalBundleID := translation.BundleID{Name: invalidName, Version: Version, Locale: Locale, Component: Component}

	msgID := translation.MessageID{Name: Name, Version: Version, Locale: Locale, Component: Component, Key: invalidKey}
	_, err = translationservice.GetService().GetString(context.TODO(), &msgID)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidKey)

	// invalid product name
	id := normalBundleID
	id.Name = invalidName
	_, err = translationservice.GetService().GetBundle(context.TODO(), &id)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidName)

	_, err = translationservice.GetService().GetAvailableBundles(context.TODO(), id.Name, id.Version)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidName)

	_, err = translationservice.GetService().GetAvailableComponents(context.TODO(), id.Name, id.Version)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidName)

	_, err = translationservice.GetService().GetAvailableLocales(context.TODO(), id.Name, id.Version)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidName)

	_, err = translationservice.GetService().GetMultipleBundles(context.TODO(), id.Name, id.Version, id.Locale, id.Component)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidName)

	_, err = translationservice.GetService().GetMultipleBundles(context.TODO(), id.Name, id.Version, "", "") // both locales and components are empty
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidName)

	_, err = translationservice.GetService().GetMultipleBundles(context.TODO(), id.Name, id.Version, "", id.Component) // locales are empty
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidName)

	_, err = translationservice.GetService().GetMultipleBundles(context.TODO(), id.Name, id.Version, id.Locale, "") // components are empty
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidName)

	msgID = translation.MessageID{Name: id.Name, Version: id.Version, Locale: id.Locale, Component: id.Component, Key: ""}
	_, err = translationservice.GetService().GetString(context.TODO(), &msgID)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidName)

	// Invalid version
	id = normalBundleID
	id.Version = invalidVersion
	_, err = translationservice.GetService().GetBundle(context.TODO(), &id)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidVersion)

	_, err = translationservice.GetService().GetAvailableBundles(context.TODO(), id.Name, id.Version)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidVersion)

	_, err = translationservice.GetService().GetAvailableComponents(context.TODO(), id.Name, id.Version)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidVersion)

	_, err = translationservice.GetService().GetAvailableLocales(context.TODO(), id.Name, id.Version)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidVersion)

	_, err = translationservice.GetService().GetMultipleBundles(context.TODO(), id.Name, id.Version, id.Locale, id.Component)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidVersion)

	msgID = translation.MessageID{Name: id.Name, Version: id.Version, Locale: id.Locale, Component: id.Component, Key: ""}
	_, err = translationservice.GetService().GetString(context.TODO(), &msgID)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidVersion)

	// Invalid locale
	id = normalBundleID
	id.Locale = invalidLocale
	_, err = translationservice.GetService().GetBundle(context.TODO(), &id)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidLocale)

	_, err = translationservice.GetService().GetMultipleBundles(context.TODO(), id.Name, id.Version, id.Locale, id.Component)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidLocale)

	msgID = translation.MessageID{Name: id.Name, Version: id.Version, Locale: id.Locale, Component: id.Component, Key: ""}
	_, err = translationservice.GetService().GetString(context.TODO(), &msgID)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidLocale)

	// Invalid component
	id = normalBundleID
	id.Component = invalidComponent
	_, err = translationservice.GetService().GetBundle(context.TODO(), &id)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidComponent)

	_, err = translationservice.GetService().GetMultipleBundles(context.TODO(), id.Name, id.Version, id.Locale, id.Component)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidComponent)

	msgID = translation.MessageID{Name: id.Name, Version: id.Version, Locale: id.Locale, Component: id.Component, Key: ""}
	_, err = translationservice.GetService().GetString(context.TODO(), &msgID)
	assert.NotNil(t, err)
	assert.Contains(t, err.Error(), invalidComponent)
}

func TestLocaleMapping(t *testing.T) {
	id := translation.BundleID{Name: Name, Version: Version, Locale: "zh-CN", Component: Component}
	bundle, err := translationservice.GetService().GetBundle(context.TODO(), &id)
	assert.Nil(t, err)
	assert.Equal(t, bundle.ID.Locale, "zh-Hans")
}

func TestPickupLocaleFromListFunc(t *testing.T) {
	localList := linkedhashset.New("en", "en-US", "nonexistent", "zh-Hans", "zh_Hans_MO", "de", "es", "ko-KR", "ja")

	tests := []struct {
		testName        string
		input, expected string
	}{
		{testName: "Normal:zh-Hans", input: "zh-Hans", expected: "zh-Hans"},
		{testName: "AddRegion:zh-Hans-CN", input: "zh-Hans-CN", expected: "zh-Hans"},
		{testName: "lowercase:zh-hans", input: "zh-hans", expected: "zh-Hans"},
		{testName: "Underscore:zh_Hans", input: "zh_Hans", expected: "zh-Hans"},
		{testName: "invalid:xxx-xxx", input: "xxx-xxx", expected: ""},
		{testName: "invalid:zh-xxx", input: "zh-xxx", expected: ""},
		{testName: "zh-Hant", input: "zh-Hant", expected: "zh"},
		{testName: "zh-Hant-HK", input: "zh-Hant-HK", expected: "zh"},
		{testName: "zh-Hans-MO", input: "zh-Hans-MO", expected: "zh-Hans-MO"},
	}

	for _, tt := range tests {
		tt := tt

		t.Run(tt.testName, func(t *testing.T) {
			actual := translationservice.PickupLocaleFromList(localList, tt.input)
			assert.Equal(t, tt.expected, actual)
		})
	}
}

func TestBundleInfoByGetBundle(t *testing.T) {
	l3Service.ClearCache(context.TODO())

	id := &translation.BundleID{Name: Name, Version: Version, Locale: Locale, Component: Component}
	compactID := translation.CompactBundleID{Locale: Locale, Component: Component}
	bundleinfo.DeleteBundle(id)

	availableBundles, ok := bundleinfo.GetAvailableBundles(Name, Version)
	assert.True(t, ok)
	assert.NotContains(t, availableBundles.Values(), compactID)

	_, err := l3Service.GetBundle(context.TODO(), id)
	assert.Nil(t, err)

	availableBundles, ok = bundleinfo.GetAvailableBundles(Name, Version)
	assert.True(t, ok)
	assert.Contains(t, availableBundles.Values(), compactID)
}

func TestBundleInfoByPutBundle(t *testing.T) {
	component := RandomString(6)
	locale := "ar"
	id := translation.BundleID{Name: Name, Version: Version, Locale: locale, Component: component}
	compactID := translation.CompactBundleID{Locale: locale, Component: component}

	var bundles []*translation.Bundle
	var msg jsoniter.Any
	json.UnmarshalFromString(`{"one.arg": "test one argument {0}"}`, &msg)
	bundles = append(bundles, &translation.Bundle{ID: id, Messages: msg})

	err := l3Service.PutBundles(context.TODO(), bundles)
	assert.Nil(t, err)
	defer func() {
		os.RemoveAll(path.Join(config.Settings.LocalBundle.BasePath, Name, Version, component))
		l3Service.ClearCache(context.TODO())
	}()

	// Check bundle information is updated
	availableBundles, ok := bundleinfo.GetAvailableBundles(Name, Version)
	assert.True(t, ok)
	assert.Contains(t, availableBundles.Values(), compactID)
	components, ok := bundleinfo.GetComponentNames(Name, Version)
	assert.True(t, ok)
	assert.Contains(t, components.Values(), component)
	locales, ok := bundleinfo.GetLocaleNames(Name, Version)
	assert.True(t, ok)
	assert.Contains(t, locales.Values(), locale)
}
