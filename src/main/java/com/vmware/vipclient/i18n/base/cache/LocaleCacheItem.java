/*

 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.HashMap;
import java.util.Map;

public class LocaleCacheItem extends FormatCacheItem implements CacheItem {

    public LocaleCacheItem() {

    }

    public LocaleCacheItem (Map<String, String> dataMap) {
        super();
        this.addCachedData(dataMap);
    }

    public final Map<String, String> cachedData = new HashMap<String, String>();

    public void addCachedData(Map<String, String> cachedData) {
        if (cachedData != null)
            this.cachedData.putAll(cachedData);
    }

    public Map<String, String> getCachedData() {
        return cachedData;
    }

}