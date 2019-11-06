/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageCache2 implements Cache {
    private long               expiredTime      = 864000000;                     // 240hr
    private long               lastClean        = System.currentTimeMillis();

    private List<MessageCache> messageCacheList = new LinkedList<MessageCache>();

    public MessageCache2() {
    }

    public Map<String, String> get(String cacheKey) {
        Map<String, String> r = null;
        for (MessageCache m : messageCacheList) {
            Object o = m.getCachedTranslationMap().get(cacheKey);
            if (o != null) {
                r = (Map<String, String>) o;
                break;
            }
        }
        return r;
    }

    public synchronized boolean put(String cacheKey, Map<String, String> map) {
        boolean created = true;
        for (int i = 0; i < messageCacheList.size(); i++) {
            MessageCache m = messageCacheList.get(i);
            if (!m.isFull()) {
                created = false;
                break;
            }
        }
        if (created) {
            MessageCache m = new MessageCache(cacheKey);
            m.setXCapacity(this.getXCapacity());
            m.setDropId(this.getDropId());
            m.setExpiredTime(this.getExpiredTime());
            m.setId(new Integer(messageCacheList.size()).toString());
            if (this.isFull() && messageCacheList.size() > 0) {
                messageCacheList.remove(0);
            }
            if (!this.isFull()) {
                messageCacheList.add(m);
            }
        }
        int targetIndex = -1;
        int minSize = 0;
        for (int i = 0; i < messageCacheList.size(); i++) {
            MessageCache m = messageCacheList.get(i);
            if (minSize == 0) {
                minSize = m.size();
                targetIndex = 0;
            } else if (m.size() < minSize) {
                minSize = m.size();
                targetIndex = i;
            }
        }
        if (targetIndex == -1) {
            return false;
        }
        MessageCache mc = messageCacheList.get(targetIndex);
        if (mc != null) {
            return mc.put(cacheKey, map);
        } else {
            return false;
        }
    }

    public synchronized boolean remove(String cacheKey) {
        boolean f = false;
        for (MessageCache m : messageCacheList) {
            f = m.remove(cacheKey);
        }
        return f;
    }

    public synchronized boolean clear() {
        boolean f = false;
        for (MessageCache m : messageCacheList) {
            f = m.clear();
        }
        messageCacheList.clear();
        return f;
    }

    public int sizeOfComponent() {
        int s = 0;
        for (MessageCache m : messageCacheList) {
            s = s + m.size();
        }
        return s;
    }

    public int size() {
        return messageCacheList.size();
    }

    public Set<String> keySet() {
        Set<String> kset = new HashSet<String>();
        for (MessageCache m : messageCacheList) {
            kset.addAll(m.getCachedTranslationMap().keySet());
        }
        return kset;
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

    private int capacityX = -1;
    private int capacityY = -1;

    public int getCacheKeySize() {
        int s = 0;
        for (MessageCache m : messageCacheList) {
            s = s + m.size();
        }
        return s;
    }

    public synchronized void setXCapacity(int capacityX) {
        this.capacityX = capacityX;
    }

    public synchronized int getXCapacity() {
        return this.capacityX;
    }

    public int getYCapacity() {
        return capacityY;
    }

    public void setYCapacity(int capacityY) {
        this.capacityY = capacityY;
    }

    public boolean isFull() {
        boolean f = false;
        if (this.getYCapacity() >= 0 && this.capacityY <= this.size()) {
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
