/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FormattingCache implements Cache {
    private long                             expiredTime   = 86400000;                                        // 24hr
    private long                             lastClean     = System.currentTimeMillis();

    private Map<String, Map<String, String>> formattingMap = new LinkedHashMap<String, Map<String, String>>();

    public FormattingCache() {
        super();
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> get(String cacheKey) {
        Object cachedObject = formattingMap.get(cacheKey);
        return cachedObject == null ? null : (Map<String, String>) cachedObject;
    }

    public synchronized boolean put(String cacheKey, Map<String, String> map) {
        formattingMap.put(cacheKey, map);
        return formattingMap.get(cacheKey) != null;
    }

    public synchronized boolean remove(String cacheKey) {
        formattingMap.remove(cacheKey);
        return !formattingMap.containsKey(cacheKey);
    }

    public synchronized boolean clear() {
        formattingMap.clear();
        return formattingMap.isEmpty();
    }

    public int size() {
        return formattingMap.size();
    }

    public Set<String> keySet() {
        return formattingMap.keySet();
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

    private int capacityByKey;
    private int capacityX;

    public synchronized void setCapacityByKey(int keysize) {
        this.capacityByKey = keysize;
    }

    public synchronized int getCapacityByKey() {
        return this.capacityByKey;
    }

    public synchronized void setXCapacity(int capacityX) {
        this.capacityX = capacityX;
    }

    public synchronized int getXCapacity() {
        return this.capacityX;
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
