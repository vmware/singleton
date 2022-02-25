/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.HashMap;
import java.util.Map;

public class PatternCacheItem extends FormatCacheItem {

    private final Map<String, Object> cachedData = new HashMap<String, Object>();

    public PatternCacheItem() {

	}

	public PatternCacheItem(Map<String, Object> dataMap, String etag, long timestamp, Long maxAgeMillis) {
		this.set(dataMap, etag, timestamp, maxAgeMillis);
	}

    public synchronized void set(Map<String, Object> dataMap, long timestamp) {
        this.set(dataMap, null, timestamp, null);
    }

    public synchronized void set(Map<String, Object> dataToCache, String etag, long timestamp, Long maxAgeMillis) {
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

    public Map<String, Object> getCachedData() {
		return cachedData;
	}
}