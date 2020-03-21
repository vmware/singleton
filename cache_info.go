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

//!+cacheInfo
type cacheInfo struct {
	m *sync.Map
}

var cacheInfoInst *cacheInfo

func newCacheInfo() *cacheInfo {
	return &cacheInfo{new(sync.Map)}
}

func (cs *cacheInfo) getUpdateInfo(item *dataItem) *singleCacheInfo {
	var id interface{}
	switch item.iType {
	case itemComponent:
		id = item.id.(componentID)
	case itemLocales, itemComponents:
		id = item.id.(translationID)
	}

	t, _ := cs.m.LoadOrStore(id, newCacheUpdateInfo())
	return t.(*singleCacheInfo)
}

//!-cacheInfo

//!+singleCacheInfo
type singleCacheInfo struct {
	status     uint32
	lastUpdate int64
	age        int64
	eTag       string
}

const (
	idle uint32 = iota
	updating
)

func newCacheUpdateInfo() *singleCacheInfo {
	ui := singleCacheInfo{0, 0, 0, ""}

	return &ui
}

func (uInfo *singleCacheInfo) isExpired() bool {
	age := atomic.LoadInt64(&uInfo.age)
	return time.Now().Unix()-atomic.LoadInt64(&uInfo.lastUpdate) >= age
}
func (uInfo *singleCacheInfo) setTime(t int64) {
	atomic.StoreInt64(&uInfo.lastUpdate, t)
}

func (uInfo *singleCacheInfo) setUpdating() (b bool) {
	return atomic.CompareAndSwapUint32(&uInfo.status, idle, updating)
}
func (uInfo *singleCacheInfo) setUpdated() {
	atomic.StoreUint32(&uInfo.status, idle)
}

func (uInfo *singleCacheInfo) waitUpdate() {
	for {
		if idle == atomic.LoadUint32(&uInfo.status) {
			return
		}
		time.Sleep(time.Microsecond * 10)
	}
}
func (uInfo *singleCacheInfo) setETag(t string) {
	uInfo.eTag = t
}
func (uInfo *singleCacheInfo) getETag() string {
	return uInfo.eTag
}
func (uInfo *singleCacheInfo) setAge(d int64) {
	atomic.StoreInt64(&uInfo.age, d)
}
func (uInfo *singleCacheInfo) getAge() int64 {
	return atomic.LoadInt64(&uInfo.age)
}

//!-singleCacheInfo
