/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface Cache {
	
    /**
     * get a component's cached data by key
     * 
     * @param key
     * @return CacheItem object instance that holds the cached data (messages and associated properties)
     */
    public CacheItem get(String key);

    /**
     * check if the cache is expired
     * 
     * @param key
     * @return false if expired
     */
    public boolean isExpired(String key);
    
    /**
     * put strings to cache by key
     * 
     * @param key cache key
     * @param cacheItem item to be stored in the cache 
     * @return false if failed to put
     */
    public boolean put(String key, CacheItem cacheItem);

    /**
     * remove a component from cache by key
     * 
     * @param key
     * @return false if failed to remove
     */
    public boolean remove(String key);

    /**
     * clear all components in the cache
     * 
     * @return false if failed to clear
     */
    public boolean clear();

    /**
     * get count of current cached components
     * 
     * @return count of cached components
     */
    public int size();

    /**
     * get the set of cached keys
     * 
     * @return set of cached keys
     */
    public Set<String> keySet();

    /**
     * get expired time
     * 
     * @return long time
     */
    public long getExpiredTime();

    /**
     * set expired time
     * 
     * @param millis
     */
    public void setExpiredTime(long millis);

    /**
     * get time of last clean
     * 
     * @return long time
     */
    public long getLastClean();

    /**
     * set time of last clean
     * 
     * @param millis
     */
    public void setLastClean(long millis);

    /**
     * set the cache's capacity by specifying the count of components
     * 
     * @param capacityX
     */
    public void setXCapacity(int capacityX);

    /**
     * get the cache capacity
     * 
     * @return cache capacity
     */
    public int getXCapacity();

    /**
     * check if the cache is expired
     * 
     * @return false if expired
     */
    public boolean isExpired();

    /**
     * get a id of translation drop
     * 
     * @return a drop id
     */
    public String getDropId();
    
    public class CacheItem {
    	public CacheItem() {
    		
    	}
    	
    	public CacheItem (Map<String, String> dataMap, final Map<String, Object> cacheProps) {
    		super();
    		this.addCachedData(dataMap);
    		this.addCacheProperties(cacheProps);
    	}
    	
    	public CacheItem (Map<String, String> dataMap) {
    		super();
    		this.addCachedData(dataMap);
    	}
    	
    	private final Map<String, String> cachedData = new HashMap<String, String>();

		/*
    	 * A map of properties associated to the cachedData (e.g. etag and cache control)
    	 */
    	private final Map<String, Object> cacheProperties = new HashMap<String, Object>();
        
        public Map<String, String> getCachedData() {
			return cachedData;
		}
        
        public void addCachedData(Map<String, String> cachedData) {
			if (cachedData != null) {
				this.cachedData.putAll(cachedData);
			}
		}
        
		public Map<String, Object> getCacheProperties() {
			return cacheProperties;
		}
		
		public void addCacheProperties(Map<String, Object> cacheProperties) {
			if (cacheProperties != null) {
				this.cacheProperties.putAll(cacheProperties);
			}
			
		}
		
		public void addCacheDataAndProperties (CacheItem cacheItem) {
			this.addCacheProperties(cacheItem.getCacheProperties());
			this.addCachedData(cacheItem.getCachedData());
		}
		
    }

}
