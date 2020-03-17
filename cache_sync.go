/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"sync"
	"sync/atomic"
	"time"
)

type (
	cacheSyncInfo struct {
		updateInfo           *sync.Map
		localesUpdateInfo    *updateInfo
		componentsUpdateInfo *updateInfo
	}
	updateInfo struct {
		status      uint32
		uTime       int64
		ageDuration int64
		eTag        string
	}
)

const (
	idle uint32 = iota
	updating
)

func newCacheSyncInfo() *cacheSyncInfo {
	syncInfo := cacheSyncInfo{
		new(sync.Map),
		newCacheUpdateInfo(),
		newCacheUpdateInfo(),
	}

	return &syncInfo
}

func newCacheUpdateInfo() *updateInfo {
	ui := updateInfo{0, 0, 0, ""}

	return &ui
}

func (uInfo *updateInfo) isExpired(cacheExpiredTime int64) bool {
	return time.Now().Unix()-atomic.LoadInt64(&uInfo.uTime) >= cacheExpiredTime
}
func (uInfo *updateInfo) setTime(t int64) {
	atomic.StoreInt64(&uInfo.uTime, t)
}

func (uInfo *updateInfo) setUpdating() (b bool) {
	return atomic.CompareAndSwapUint32(&uInfo.status, idle, updating)
}
func (uInfo *updateInfo) setUpdated() {
	atomic.StoreUint32(&uInfo.status, idle)
}

func (uInfo *updateInfo) waitUpdate() {
	for {
		if idle == atomic.LoadUint32(&uInfo.status) {
			return
		}
		time.Sleep(time.Microsecond * 10)
	}
}

func (cs *cacheSyncInfo) getCompUpdateInfo(name, version, locale, component string) *updateInfo {
	components, _ := cs.updateInfo.LoadOrStore(locale, &sync.Map{})

	t, _ := components.(*sync.Map).LoadOrStore(component, newCacheUpdateInfo())

	return t.(*updateInfo)
}

func (cs *cacheSyncInfo) getLocalesUpdateInfo(name, version string) *updateInfo {
	return cs.localesUpdateInfo
}

func (cs *cacheSyncInfo) getComponentsUpdateInfo(name, version string) *updateInfo {
	return cs.componentsUpdateInfo
}
