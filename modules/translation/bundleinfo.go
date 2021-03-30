/*
 * Copyright 2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package translation

import (
	"sync"
	"sync/atomic"

	"github.com/emirpasic/gods/sets"
	"github.com/emirpasic/gods/sets/hashset"
	"github.com/emirpasic/gods/sets/linkedhashset"
)

type (
	BundleInfo struct {
		productInfos *sync.Map
	}

	ProductInfo struct {
		name         string
		releaseNames atomic.Value
		releaseInfos *sync.Map
	}

	ReleaseInfo struct {
		name                string
		version             string
		AvailableBundles    sets.Set
		AvailableLocales    sets.Set
		AvailableComponents sets.Set
	}
)

func NewBundleInfo() *BundleInfo {
	return &BundleInfo{productInfos: &sync.Map{}}
}

func newProductInfo(name string) *ProductInfo {
	return &ProductInfo{name: name, releaseInfos: &sync.Map{}}
}

func (i *BundleInfo) GetProductInfo(name string) (*ProductInfo, bool) {
	if product, ok := i.productInfos.Load(name); ok {
		return product.(*ProductInfo), true
	}
	return nil, false
}
func (i *BundleInfo) AddProductInfo(name string, info *ProductInfo) (*ProductInfo, bool) {
	product, loaded := i.productInfos.LoadOrStore(name, info)
	return product.(*ProductInfo), loaded
}
func (i *BundleInfo) GetReleaseInfo(name, version string) (*ReleaseInfo, bool) {
	if product, ok := i.GetProductInfo(name); ok {
		if release, ok := product.releaseInfos.Load(version); ok {
			return release.(*ReleaseInfo), true
		}
	}
	return nil, false
}
func (i *BundleInfo) AddReleaseInfo(name, version string, info *ReleaseInfo) {
	product, _ := i.AddProductInfo(name, newProductInfo(name))
	product.releaseInfos.Store(version, info)

	existingRelNames := product.GetReleaseNames()
	if !existingRelNames.Contains(version) {
		newRelNames := hashset.New(existingRelNames.Values()...)
		newRelNames.Add(version)
		product.releaseNames.Store(newRelNames)
	}
}
func (i *BundleInfo) AddBundle(id *BundleID) {
	releaseInfo, _ := i.GetReleaseInfo(id.Name, id.Version)
	newReleaseInfo := &ReleaseInfo{name: id.Name, version: id.Version}
	if releaseInfo == nil {
		newReleaseInfo.AvailableBundles = linkedhashset.New(CompactBundleID{id.Locale, id.Component})
		newReleaseInfo.AvailableLocales = linkedhashset.New(id.Locale)
		newReleaseInfo.AvailableComponents = linkedhashset.New(id.Component)
	} else {
		bundleID := CompactBundleID{id.Locale, id.Component}
		if releaseInfo.AvailableBundles.Contains(bundleID) {
			return
		}
		newReleaseInfo.AvailableBundles = linkedhashset.New(releaseInfo.AvailableBundles.Values()...)
		newReleaseInfo.AvailableBundles.Add(bundleID)
		newReleaseInfo.AvailableLocales = linkedhashset.New(releaseInfo.AvailableLocales.Values()...)
		newReleaseInfo.AvailableLocales.Add(id.Locale)
		newReleaseInfo.AvailableComponents = linkedhashset.New(releaseInfo.AvailableComponents.Values()...)
		newReleaseInfo.AvailableComponents.Add(id.Component)
	}
	i.AddReleaseInfo(id.Name, id.Version, newReleaseInfo)
}

func (i *BundleInfo) DeleteBundle(id *BundleID) {
	releaseInfo, _ := i.GetReleaseInfo(id.Name, id.Version)
	if releaseInfo == nil {
		return
	}

	compactID := CompactBundleID{id.Locale, id.Component}
	if !releaseInfo.AvailableBundles.Contains(compactID) {
		return
	}

	newReleaseInfo := &ReleaseInfo{name: id.Name, version: id.Version}

	newReleaseInfo.AvailableBundles = linkedhashset.New(releaseInfo.AvailableBundles.Values()...)
	newReleaseInfo.AvailableBundles.Remove(compactID)

	newReleaseInfo.AvailableLocales = linkedhashset.New()
	newReleaseInfo.AvailableComponents = linkedhashset.New()
	for _, v := range newReleaseInfo.AvailableBundles.Values() {
		bundleID := v.(CompactBundleID)
		newReleaseInfo.AvailableLocales.Add(bundleID.Locale)
		newReleaseInfo.AvailableComponents.Add(bundleID.Component)
	}

	i.AddReleaseInfo(id.Name, id.Version, newReleaseInfo)
}

func (i *BundleInfo) AddBundles(name, version string, ids []CompactBundleID) {
	releaseInfo, _ := i.GetReleaseInfo(name, version)
	newReleaseInfo := &ReleaseInfo{name: name, version: version}
	if releaseInfo == nil {
		newReleaseInfo.AvailableBundles = linkedhashset.New()
		newReleaseInfo.AvailableLocales = linkedhashset.New()
		newReleaseInfo.AvailableComponents = linkedhashset.New()
	} else {
		newReleaseInfo.AvailableBundles = linkedhashset.New(releaseInfo.AvailableBundles.Values()...)
		newReleaseInfo.AvailableLocales = linkedhashset.New(releaseInfo.AvailableLocales.Values()...)
		newReleaseInfo.AvailableComponents = linkedhashset.New(releaseInfo.AvailableComponents.Values()...)
	}

	for _, id := range ids {
		newReleaseInfo.AvailableBundles.Add(CompactBundleID{id.Locale, id.Component})
		newReleaseInfo.AvailableLocales.Add(id.Locale)
		newReleaseInfo.AvailableComponents.Add(id.Component)
	}

	i.AddReleaseInfo(name, version, newReleaseInfo)
}

func (i *ProductInfo) GetReleaseNames() sets.Set {
	v := i.releaseNames.Load()
	if v == nil {
		return hashset.New()
	}
	return v.(sets.Set)
}
