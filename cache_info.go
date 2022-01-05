/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"sync"
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
type itemCacheInfo struct {
	lastUpdate int64
	age        int64
	eTag       string
	sync.RWMutex
}

func newSingleCacheInfo() *itemCacheInfo {
	return &itemCacheInfo{0, cacheDefaultExpires, "", sync.RWMutex{}}
}

func (i *itemCacheInfo) setTime(t int64) {
	i.Lock()
	defer i.Unlock()
	i.lastUpdate = t
}

func (i *itemCacheInfo) setAge(d int64) {
	i.Lock()
	defer i.Unlock()
	i.age = d
}

func (i *itemCacheInfo) isExpired() bool {
	i.RLock()
	defer i.RUnlock()

	return time.Now().Unix()-i.lastUpdate >= i.age
}

func (i *itemCacheInfo) setETag(t string) {
	i.eTag = t
}
func (i *itemCacheInfo) getETag() string {
	return i.eTag
}

//!-itemCacheInfo
