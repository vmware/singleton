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

var cacheInfosInst *cacheInfos

func newCacheInfo() *cacheInfos {
	return &cacheInfos{new(sync.Map)}
}

//!+cacheInfo
type cacheInfos struct {
	m *sync.Map
}

func (cs *cacheInfos) get(item *dataItem) *singleCacheInfo {
	t, _ := cs.m.LoadOrStore(item.id, newSingleCacheInfo())
	return t.(*singleCacheInfo)
}

//!-cacheInfo

//!+singleCacheInfo
const (
	idle uint32 = iota
	updating
)

type singleCacheInfo struct {
	status     uint32
	lastUpdate int64
	age        int64
	eTag       string
}

func newSingleCacheInfo() *singleCacheInfo {
	return &singleCacheInfo{0, 0, 0, ""}
}

func (uInfo *singleCacheInfo) isExpired() bool {
	return time.Now().Unix()-atomic.LoadInt64(&uInfo.lastUpdate) >= uInfo.getAge()
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
