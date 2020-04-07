package com.vmware.vipclient.i18n.base.cache;

import java.util.HashMap;
import java.util.Map;

public class CacheItem {
	public CacheItem() {
		
	}
	
	public CacheItem (Map<String, String> dataMap, final Map<String, Object> cacheProps) {
		super();
		if (dataMap != null) 
			this.addCachedData(dataMap);
		this.addCacheProperties(cacheProps);
	}
	
	public CacheItem (Map<String, String> dataMap) {
		super();
		if (dataMap != null)
			this.addCachedData(dataMap);
	}
	
	private final Map<String, String> cachedData = new HashMap<String, String>();

	/*
	 * A map of properties associated to the cachedData (e.g. etag and cache control)
	 * This map is optional and will not be instantiated if not needed.
	 */
	private Map<String, Object> cacheProperties;
    
    public Map<String, String> getCachedData() {
		return cachedData;
	}
    
    public void addCachedData(Map<String, String> cachedData) {
		this.cachedData.putAll(cachedData);
	}
    
	public Map<String, Object> getCacheProperties() {
		return cacheProperties;
	}
	
	public void addCacheProperties(Map<String, Object> cacheProperties) {
		synchronized(this) {
			if (this.cacheProperties == null) {
				this.cacheProperties = new HashMap<String, Object>();
			}
		}
		if (cacheProperties != null) {
			this.cacheProperties.putAll(cacheProperties);
		}
		
	}
	
	public void addCacheDataAndProperties (CacheItem cacheItem) {
		this.addCacheProperties(cacheItem.getCacheProperties());
		this.addCachedData(cacheItem.getCachedData());
	}
	
}