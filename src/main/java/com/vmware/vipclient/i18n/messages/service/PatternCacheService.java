/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.Map;

import org.json.simple.JSONObject;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;

public class PatternCacheService {

    public PatternCacheService() {
    }

    public void addPatterns(String key, JSONObject o) {
        if (null != key && null != o) {
            Cache c = VIPCfg.getInstance().getCacheManager()
                    .getCache(VIPCfg.CACHE_L2);
            if (c != null) {
            	// TODO pass map of cache properties such as etag and cache control headers
                Map<String, Object> cacheProps = null;
                c.put(key, o, cacheProps);
            }
        }
    }

    public JSONObject lookForPatternsFromCache(String key) {
        JSONObject o = null;
        Cache c = VIPCfg.getInstance().getCacheManager()
                .getCache(VIPCfg.CACHE_L2);
        if (c != null) {
            o = (JSONObject) c.get(key);
        }
        if (null == o) {
            return null;
        }
        return o;
    }

}
