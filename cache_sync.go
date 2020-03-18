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
		compDataUpdateInfo   *sync.Map
		localesUpdateInfo    *sync.Map
		componentsUpdateInfo *sync.Map
	}
	updateInfo struct {
		status     uint32
		lastUpdate int64
		age        int64
		eTag       atomic.Value
	}
)

const (
	idle uint32 = iota
	updating
)

func newCacheSyncInfo() *cacheSyncInfo {
	syncInfo := cacheSyncInfo{
		new(sync.Map), new(sync.Map), new(sync.Map),
	}

	return &syncInfo
}

func newCacheUpdateInfo() *updateInfo {
	ui := updateInfo{0, 0, 0, atomic.Value{}}

	return &ui
}

func (uInfo *updateInfo) isExpired() bool {
	age := atomic.LoadInt64(&uInfo.age)
	return time.Now().Unix()-atomic.LoadInt64(&uInfo.lastUpdate) >= age
}
func (uInfo *updateInfo) setTime(t int64) {
	atomic.StoreInt64(&uInfo.lastUpdate, t)
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
func (uInfo *updateInfo) setETag(t string) {
	uInfo.eTag.Store(t)
}
func (uInfo *updateInfo) getETag() string {
	return uInfo.eTag.Load().(string)
}
func (uInfo *updateInfo) setAge(d int64) {
	atomic.StoreInt64(&uInfo.age, d)
}
func (uInfo *updateInfo) getAge() int64 {
	return atomic.LoadInt64(&uInfo.age)
}

func (cs *cacheSyncInfo) getCompUpdateInfo(name, version, locale, component string) *updateInfo {
	t, _ := cs.compDataUpdateInfo.LoadOrStore(componentID{name, version, locale, component}, newCacheUpdateInfo())

	return t.(*updateInfo)
}

func (cs *cacheSyncInfo) getLocalesUpdateInfo(name, version string) *updateInfo {
	t, _ := cs.localesUpdateInfo.LoadOrStore(translationID{name, version}, newCacheUpdateInfo())

	return t.(*updateInfo)
}

func (cs *cacheSyncInfo) getComponentsUpdateInfo(name, version string) *updateInfo {
	t, _ := cs.componentsUpdateInfo.LoadOrStore(translationID{name, version}, newCacheUpdateInfo())

	return t.(*updateInfo)
}
