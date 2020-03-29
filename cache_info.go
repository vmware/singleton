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

var cacheInfoMap *sync.Map

func initCacheInfoMap() {
	cacheInfoMap = new(sync.Map)
}

func getCacheInfo(item *dataItem) *itemCacheInfo {
	t, _ := cacheInfoMap.LoadOrStore(item.id, newSingleCacheInfo())
	return t.(*itemCacheInfo)
}

//!+itemCacheInfo
const (
	idle uint32 = iota
	updating
)

type itemCacheInfo struct {
	status     uint32
	lastUpdate int64
	age        int64
	eTag       string
}

func newSingleCacheInfo() *itemCacheInfo {
	return &itemCacheInfo{0, 0, 0, ""}
}

func (i *itemCacheInfo) isExpired() bool {
	age := i.getAge()
	if age == -1 {
		return false
	}
	return time.Now().Unix()-atomic.LoadInt64(&i.lastUpdate) >= age
}
func (i *itemCacheInfo) setTime(t int64) {
	atomic.StoreInt64(&i.lastUpdate, t)
}

func (i *itemCacheInfo) setUpdating() (b bool) {
	return atomic.CompareAndSwapUint32(&i.status, idle, updating)
}
func (i *itemCacheInfo) setUpdated() {
	atomic.StoreUint32(&i.status, idle)
}

func (i *itemCacheInfo) waitUpdate() {
	for {
		if idle == atomic.LoadUint32(&i.status) {
			return
		}
		time.Sleep(time.Microsecond * 10)
	}
}
func (i *itemCacheInfo) setETag(t string) {
	i.eTag = t
}
func (i *itemCacheInfo) getETag() string {
	return i.eTag
}
func (i *itemCacheInfo) setAge(d int64) {
	atomic.StoreInt64(&i.age, d)
}
func (i *itemCacheInfo) getAge() int64 {
	return atomic.LoadInt64(&i.age)
}

//!-itemCacheInfo
