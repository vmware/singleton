/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.HashMap;
import java.util.Map;

public class LocaleCacheItem extends FormatCacheItem implements CacheItem {

    private final Map<String, String> cachedData = new HashMap<String, String>();

    public LocaleCacheItem() {

    }

    public LocaleCacheItem (Map<String, String> dataMap, String etag, long timestamp, Long maxAgeMillis) {
        this.set(dataMap, etag, timestamp, maxAgeMillis);
    }

    public synchronized void set(Map<String, String> dataMap, long timestamp) {
        this.set(dataMap, null, timestamp, null);
    }

    public synchronized void set(Map<String, String> dataToCache, String etag, long timestamp, Long maxAgeMillis) {
        if (dataToCache != null)
            this.cachedData.putAll(dataToCache);
        this.set(etag, timestamp, maxAgeMillis);
    }

    public synchronized void set(String etag, long timestamp, Long maxAgeMillis) {
        if (etag != null && !etag.isEmpty())
            setEtag(etag);
        setTimestamp(timestamp);
        if (maxAgeMillis != null)
            setMaxAgeMillis(maxAgeMillis);
    }

    public synchronized void set(LocaleCacheItem cacheItem) {
        this.set(cacheItem.getCachedData(), cacheItem.getEtag(), cacheItem.getTimestamp(), cacheItem.getMaxAgeMillis());
    }

    public synchronized void set(long timestamp) {
        setTimestamp(timestamp);
    }

    public Map<String, String> getCachedData() {
        return cachedData;
    }
}