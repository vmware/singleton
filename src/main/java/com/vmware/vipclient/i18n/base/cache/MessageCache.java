/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MessageCache implements Cache {
    private String                           id                  = "cache-default";
 
    private long                             expiredTime         = 864000000;                                       // 240hr
    private long                             lastClean           = System.currentTimeMillis();

    private final Map<String, MessageCacheItem> cachedComponentsMap = new LinkedHashMap<String, MessageCacheItem>();
    
    public Map<String, MessageCacheItem> getCachedTranslationMap() {
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
    public CacheItem get(String cacheKey) {
        Integer i = hitMap.get(cacheKey);
        if (i != null) {
            hitMap.put(cacheKey, i.intValue() + 1);
        }
        return cachedComponentsMap.get(cacheKey);
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
    
    @Override
    public synchronized boolean put(String cacheKey, CacheItem itemToCache) {
    	if (itemToCache != null) {
	        if (this.isFull()) {
	            String k = getRemovedKeyFromHitMap();
	            this.remove(k);
	            hitMap.remove(k);
	        } 
	        if (!this.isFull()) {
	    		MessageCacheItem cacheItem = cachedComponentsMap.get(cacheKey);
	    		if (cacheItem == null) {
	    			cachedComponentsMap.put(cacheKey, (MessageCacheItem) itemToCache);
	    		} else {
	    			cacheItem.setCacheItem((MessageCacheItem) itemToCache);
	    		}
	        }
    	}
        return cachedComponentsMap.containsKey(cacheKey);
    }

    public synchronized boolean remove(String cacheKey) {
        Object o1 = cachedComponentsMap.get(cacheKey);
        Object o2 = hitMap.get(cacheKey);
        cachedComponentsMap.remove(cacheKey);
        hitMap.remove(cacheKey);
        if (o1 != null)
            o1 = null;
        if (o2 != null)
            o2 = null;
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
        	MessageCacheItem cacheItem = this.getCachedTranslationMap().get(key);
            if (cacheItem != null) {              
                size += cacheItem.getCachedData().size();
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
    
    @Deprecated
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
