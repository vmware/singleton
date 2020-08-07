/*

 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.HashMap;
import java.util.Map;

import com.vmware.vipclient.i18n.VIPCfg;

public class MessageCacheItem implements CacheItem {
	public MessageCacheItem() {
		
	}

	public MessageCacheItem(Map<String, String> dataMap) {
		if (dataMap != null)
			this.cachedData.putAll(dataMap);
	}
	
	public MessageCacheItem (String locale, Map<String, String> dataMap, String etag, long timestamp, Long maxAgeMillis) {
		this.setCacheItem(locale, dataMap, etag, timestamp, maxAgeMillis);
	}

	private String locale;
	private String etag;
	private long timestamp;
	private Long maxAgeMillis = 86400000l;
	
	private final Map<String, String> cachedData = new HashMap<>();

	public synchronized void setCacheItem(String locale, Map<String, String> dataToCache, String etag, long timestamp, Long maxAgeMillis) {
		if (dataToCache != null)
			this.cachedData.putAll(dataToCache);
		this.setCacheItem(locale, etag, timestamp, maxAgeMillis);
	}
	public synchronized void setCacheItem(String locale, String etag, long timestamp, Long maxAgeMillis) {
		this.locale = locale;
		if (etag != null && !etag.isEmpty())
			this.etag = etag;
		this.timestamp = timestamp;
		if (maxAgeMillis != null)
			this.maxAgeMillis = maxAgeMillis;
	}

	public synchronized void setCacheItem (MessageCacheItem cacheItem) {
		this.setCacheItem(cacheItem.getLocale(), cacheItem.getCachedData(), cacheItem.getEtag(), cacheItem.getTimestamp(), cacheItem.getMaxAgeMillis());
	}
		
	public String getEtag() {
		return etag;
	}

	public long getTimestamp() {
		return timestamp;
	}
    
    public Map<String, String> getCachedData() {
		return cachedData;
	}

	public Long getMaxAgeMillis() {
		return maxAgeMillis;
	}

	public String getLocale() { return locale; }

	public synchronized boolean isExpired() {
		// If offline mode only, cache never expires.
		if (VIPCfg.getInstance().getVipServer() == null) {
			return false;
		}
    	// If maxAgeFromConfig is present, it means it is using the old way 
    	// of caching expiration, so do not expire individual CacheItem object
    	if (VIPCfg.getInstance().getCacheExpiredTime() != 0) {
    		return false;
    	}
    	
    	Long responseTimeStamp = this.getTimestamp();
    	if (responseTimeStamp == null) {
    		return true;
    	}
    	
    	Long maxAgeResponse = this.getMaxAgeMillis();
    	if (maxAgeResponse != null) {
    		maxAgeMillis = maxAgeResponse;
    	}
    		  	
    	return System.currentTimeMillis() - responseTimeStamp > maxAgeMillis;
    }

}