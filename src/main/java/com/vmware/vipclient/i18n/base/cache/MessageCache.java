/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vmware.vipclient.i18n.messages.api.url.URLUtils;

public class MessageCache implements Cache {
    private String                           id                  = "cache-default";

    private long                             expiredTime         = 864000000;                                       // 240hr
    private long                             lastClean           = System.currentTimeMillis();

    private final Map<String, Map<String, String>> cachedComponentsMap = new LinkedHashMap<String, Map<String, String>>();
    private final Map<String, Map<String, Object>> cacheProperties = new LinkedHashMap<String, Map<String, Object>>();
    
    public Map<String, Map<String, Object>> getCacheProperties() {
        return cacheProperties;
    }
    
    public Map<String, Map<String, String>> getCachedTranslationMap() {
        return cachedComponentsMap;
    }

    public Map<String, Integer> getHitMap() {
        return hitMap;
    }

    private Map<String, Integer> hitMap = new LinkedHashMap<String, Integer>();

    public MessageCache() {
        super();
    }

    public MessageCache(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> get(String cacheKey) {
    	Map<String, Object> cache = new HashMap<String, Object>();
    	Map<String,Object> cacheProps = this.cacheProperties.get(cacheKey);
    	if (cacheProps != null) {
    		cache.put(CACHE_PROPERTIES, cacheProps);
    	}
    	
        Integer i = hitMap.get(cacheKey);
        if (i != null) {
            hitMap.put(cacheKey, i.intValue() + 1);
        }
        Object cachedObject = cachedComponentsMap.get(cacheKey);
        if (cachedObject != null) {
        	cache.put(MESSAGES,  (Map<String, String>) cachedObject);
        }
        return cache;
    }
    
    public boolean isExpired(String cacheKey) {
    	Map<String,Object> cacheProps = this.cacheProperties.get(cacheKey);
    	if (cacheProps == null || cacheProps.isEmpty()) {
    		return true;
    	}
    	Long responseTimeStamp = (Long) cacheProps.get(URLUtils.RESPONSE_TIMESTAMP);
    	if (responseTimeStamp == null) {
    		return true;
    	}
    	Long maxAgeMillis = Long.MAX_VALUE;
    	Long maxAgeFromConfig = (Long) cacheProps.get(URLUtils.MAX_AGE); 
    	if (maxAgeFromConfig != null) {
    		 // override response header max age
    		maxAgeMillis = maxAgeFromConfig; 
    	} else { 
    		//gets max age from response header
    		Map<String, Object> headers = (Map<String, Object>) cacheProps.get(URLUtils.HEADERS);
        	if (headers == null) {
        		return true;
        	}
        	List<String> cacheCtrlString = (List<String>) headers.get(URLUtils.CACHE_CONTROL);
        	if (cacheCtrlString == null || cacheCtrlString.isEmpty()) {
        		return true;
        	}
    		for (String ccs : cacheCtrlString) { 
        		String[] cacheCtrlDirectives = ccs.split(",");
        		for (String ccd: cacheCtrlDirectives) {
        			String[] ccdString = ccd.split("=");
        			if (ccdString[0].equals(URLUtils.MAX_AGE)) {
        				maxAgeMillis = Long.parseLong(ccdString[1]) * 1000l;
        			}
        		}	
        	}
    	}	  	
    	return System.currentTimeMillis() - responseTimeStamp > maxAgeMillis;
    }

    public String getRemovedKeyFromHitMap() {
        String key = "";
        Set<String> s = this.hitMap.keySet();
        if (s.size() == 0) {
            Set<String> tt = cachedComponentsMap.keySet();
            if (tt.size() > 0) {
                key = tt.iterator().next();
                return key;
            }
        }
        int t = 0;
        for (String k : s) {
            int i = this.hitMap.get(k).intValue();
            if (t == 0 || i < t) {
                t = i;
                key = k;
            }
        }
        return key;
    }

    public synchronized boolean put(String cacheKey, Map<String, String> dataToCache, Map<String, Object> cacheProps) {
        if (this.isFull()) {
            String k = getRemovedKeyFromHitMap();
            this.remove(k);
            hitMap.remove(k);
        } 
        if (!this.isFull()) {
        	if (dataToCache != null) {
	        	Map<String, String> cachedData = cachedComponentsMap.get(cacheKey);
	        	if (cachedData == null) {
	        		cachedComponentsMap.put(cacheKey, dataToCache);
	        	} else {
	        		cachedData.putAll(dataToCache);
	        	}
        	}
        	// a map of properties associated to this cache key (e.g. etag and cache control)
        	cacheProperties.put(cacheKey, cacheProps);
        }
        return cachedComponentsMap.containsKey(cacheKey);
    }

    public synchronized boolean remove(String cacheKey) {
        Object o1 = cachedComponentsMap.get(cacheKey);
        Object o2 = hitMap.get(cacheKey);
        Object o3 = cacheProperties.get(cacheKey);
        cachedComponentsMap.remove(cacheKey);
        hitMap.remove(cacheKey);
        cacheProperties.remove(cacheKey);
        if (o1 != null)
            o1 = null;
        if (o2 != null)
            o2 = null;
        if (o3 != null)
            o3 = null;
        return !cachedComponentsMap.containsKey(cacheKey);
    }

    public synchronized boolean clear() {
        Set<String> s = cachedComponentsMap.keySet();
        for (String key : s) {
            Object o = cachedComponentsMap.get(key);
            if (o != null) {
                o = null;
            }
        }
        cachedComponentsMap.clear();
        Set<String> s2 = hitMap.keySet();
        for (String key : s2) {
            Object o = hitMap.get(key);
            if (o != null) {
                o = null;
            }
        }
        hitMap.clear();
        cacheProperties.clear();
        return cachedComponentsMap.isEmpty();
    }

    public int size() {
        return cachedComponentsMap.size();
    }

    public Set<String> keySet() {
        return cachedComponentsMap.keySet();
    }

    public synchronized long getExpiredTime() {
        return this.expiredTime;
    }

    public synchronized void setExpiredTime(long millis) {
        this.expiredTime = millis;
    }

    public synchronized long getLastClean() {
        return this.lastClean;
    }

    public synchronized void setLastClean(long millis) {
        this.lastClean = millis;
    }

    private int capacityByKey = -1;
    private int capacityX     = -1;

    public int getCachedKeySize() {
        Set<String> s = this.getCachedTranslationMap().keySet();
        int size = 0;
        for (String key : s) {
            Object o = this.getCachedTranslationMap().get(key);
            if (o != null) {
                Map<String, String> m = (Map<String, String>) o;
                size = size + m.keySet().size();
            }
        }
        return size;
    }

    public void setCapacityByKey(int capacityByKey) {
        this.capacityByKey = capacityByKey;
    }

    public int getCapacityByKey() {
        return this.capacityByKey;
    }

    public void setXCapacity(int capacityX) {
        this.capacityX = capacityX;
    }

    public int getXCapacity() {
        return this.capacityX;
    }

    public boolean isFull() {
        boolean f = false;
        if (this.getXCapacity() >= 0 && this.capacityX <= this.size()) {
            f = true;
        }
        int keysize = this.getCapacityByKey();
        if (keysize >= 0 && keysize <= this.getCachedKeySize()) {
            f = true;
        }
        return f;
    }

    public boolean isExpired() {
        boolean f = false;
        long expired = this.getExpiredTime();
        long lastClean = this.getLastClean();
        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastClean) > expired) {
            f = true;
        }
        return f;
    }

    private String dropId;

    public String getDropId() {
        return dropId;
    }

    public void setDropId(String dropId) {
        this.dropId = dropId;
    }
}
