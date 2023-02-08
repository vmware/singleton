/*
 * Copyright 2020-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package sgtn

import (
	"time"
)

//!+itemCacheInfo
type itemCacheInfo struct {
	lastUpdate   int64
	age          int64
	eTag         string
}

func newSingleCacheInfo() *itemCacheInfo {
	return &itemCacheInfo{time.Now().Unix(), cacheDefaultExpires, ""}
}

func (i *itemCacheInfo) setTime(t int64) {
	i.lastUpdate = t
}

func (i *itemCacheInfo) setAge(d int64) {
	i.age = d
}

func (i *itemCacheInfo) isExpired() bool {
	if i == nil {
		return false
	}

	return time.Now().Unix()-i.lastUpdate >= i.age
}

func (i *itemCacheInfo) setETag(t string) {
	i.eTag = t
}
func (i *itemCacheInfo) getETag() string {
	if i == nil {
		return ""
	} else {
		return i.eTag
	}
}

//!-itemCacheInfo
