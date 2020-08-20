/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import org.json.simple.JSONObject;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormatCacheItem;

public class PatternCacheService {

    public PatternCacheService() {
    }

    public void addPatterns(String key, JSONObject o) {
        if (null != key && null != o) {
            Cache c = VIPCfg.getInstance().getCacheManager()
                    .getCache(VIPCfg.CACHE_L2);
            if (c != null) {
                c.put(key, new FormatCacheItem(o));
            }
        }
    }

    public JSONObject lookForPatternsFromCache(String key) {
        JSONObject o = null;
        Cache c = VIPCfg.getInstance().getCacheManager()
                .getCache(VIPCfg.CACHE_L2);
        if (c != null) {
        	FormatCacheItem cacheItem = (FormatCacheItem) c.get(key);  
        	if (cacheItem != null)
        		o = new JSONObject (cacheItem.getCachedData());
        }
        return o;
    }

}
