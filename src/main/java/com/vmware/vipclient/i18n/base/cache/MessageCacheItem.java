/*

 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import com.vmware.vipclient.i18n.VIPCfg;

public class MessageCacheItem extends CacheItem {

	private Semaphore sem = new Semaphore(1);

	public Semaphore getSem() {
		return sem;
	}

	public void setSem(Semaphore sem) {
		this.sem = sem;
	}
	
	public MessageCacheItem() {
		
	}

	public MessageCacheItem(Map<String, String> dataMap) {
		if (dataMap != null)
			this.cachedData.putAll(dataMap);
	}

	public MessageCacheItem (Map<String, String> dataMap, String etag, long timestamp, Long maxAgeMillis) {
		this.setCacheItem(dataMap, etag, timestamp, maxAgeMillis);
	}
	
	private final Map<String, String> cachedData = new HashMap<>();

	public synchronized void setCacheItem(Map<String, String> dataToCache, String etag, long timestamp, Long maxAgeMillis) {
		if (dataToCache != null)
			this.cachedData.putAll(dataToCache);
		this.setCacheItem(etag, timestamp, maxAgeMillis);
	}
	public synchronized void setCacheItem(String etag, long timestamp, Long maxAgeMillis) {
		if (etag != null && !etag.isEmpty())
			setEtag(etag);
		setTimestamp(timestamp);
		if (maxAgeMillis != null)
			setMaxAgeMillis(maxAgeMillis);
	}

	public synchronized void setCacheItem (MessageCacheItem cacheItem) {
		this.setCacheItem(cacheItem.getCachedData(), cacheItem.getEtag(), cacheItem.getTimestamp(), cacheItem.getMaxAgeMillis());
	}
    
    public Map<String, String> getCachedData() {
		return cachedData;
	}
}