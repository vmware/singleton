/*
 * Copyright 2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package bundleinfo

import (
	"context"
	"sync/atomic"
	"time"

	"sgtnserver/internal/config"
	"sgtnserver/internal/logger"
	"sgtnserver/modules/translation"

	"github.com/emirpasic/gods/sets"
	"go.uber.org/zap"
)

var (
	msgOrigin  translation.MessageOrigin
	bundleInfo atomic.Value
)

func InitBundleInfo(origin translation.MessageOrigin) {
	logger.Log.Debug("Initialize bundle information")
	msgOrigin = origin
	if err := RefreshBundleInfo(context.TODO()); err != nil {
		panic(err)
	}

	if tick := time.Tick(config.Settings.RefreshBundleInterval); tick != nil {
		go func() {
			defer func() {
				if err := recover(); err != nil {
					logger.Log.Error("Panic when refreshing bundle info", zap.Any("error", err))
				}
			}()

			logger.Log.Debug("Start bundle information refresher")
			for {
				<-tick
				RefreshBundleInfo(context.TODO())
			}
		}()
	}
}

func RefreshBundleInfo(ctx context.Context) error {
	info, err := msgOrigin.GetBundleInfo(ctx)
	if err == nil {
		bundleInfo.Store(info)
	}

	return err
}

func getBundleInfo() *translation.BundleInfo {
	return bundleInfo.Load().(*translation.BundleInfo)
}
func getReleaseInfo(name, version string) (*translation.ReleaseInfo, bool) {
	return getBundleInfo().GetReleaseInfo(name, version)
}

func AddBundle(id *translation.BundleID) {
	getBundleInfo().AddBundle(id)
}

func DeleteBundle(id *translation.BundleID) {
	getBundleInfo().DeleteBundle(id)
}

func GetAvailableBundles(name, version string) (data sets.Set, ok bool) {
	if r, _ := getReleaseInfo(name, version); r != nil {
		return r.AvailableBundles, true
	}
	return nil, false
}

func GetComponentNames(name, version string) (data sets.Set, ok bool) {
	if r, _ := getReleaseInfo(name, version); r != nil {
		return r.AvailableComponents, true
	}
	return nil, false
}

func GetLocaleNames(name, version string) (data sets.Set, ok bool) {
	if r, _ := getReleaseInfo(name, version); r != nil {
		return r.AvailableLocales, true
	}
	return nil, false
}
func GetReleaseNames(name string) (data sets.Set, ok bool) {
	p, ok := getBundleInfo().GetProductInfo(name)
	if ok {
		return p.GetReleaseNames(), true
	}
	return
}

func IsBundleExist(id *translation.BundleID) bool {
	if bundles, _ := GetAvailableBundles(id.Name, id.Version); bundles != nil {
		return bundles.Contains(translation.CompactBundleID{Locale: id.Locale, Component: id.Component})
	}
	return false
}

func IsProductExist(name string) bool {
	_, ok := getBundleInfo().GetProductInfo(name)
	return ok
}
