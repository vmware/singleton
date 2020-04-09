/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheItem {
	public CacheItem() {
		
	}
	
	public CacheItem (Map<String, String> dataMap, String etag, long timestamp, Long maxAgeMillis) {
		super();
		if (dataMap != null) 
			this.addCachedData(dataMap);
		this.etag = etag;
		this.timestamp = timestamp;
		this.maxAgeMillis = maxAgeMillis;
	}
	
	public CacheItem (Map<String, String> dataMap) {
		super();
		if (dataMap != null)
			this.addCachedData(dataMap);
	}
	
	private final Map<String, String> cachedData = new HashMap<String, String>();
	private String etag;
	private long timestamp;
	private Long maxAgeMillis;
	
	public void addCachedData(Map<String, String> cachedData) {
		this.cachedData.putAll(cachedData);
	}
	
	public void addCacheItem (CacheItem cacheItem) {
		this.addCachedData(cacheItem.getCachedData());
		this.etag = cacheItem.etag;
		this.timestamp = cacheItem.timestamp;
		this.maxAgeMillis = cacheItem.maxAgeMillis;
	}
		
	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
    
    public Map<String, String> getCachedData() {
		return cachedData;
	}

	public Long getMaxAgeMillis() {
		return maxAgeMillis;
	}

	public void setMaxAgeMillis(Long maxAgeMillis) {
		this.maxAgeMillis = maxAgeMillis;
	}
	
}