/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.HashMap;
import java.util.Map;

public class PatternCacheItem extends FormatCacheItem implements CacheItem {
	public PatternCacheItem() {

	}

	public PatternCacheItem(Map<String, Object> dataMap) {
		super();
		this.addCachedData(dataMap);
	}
	
	public final Map<String, Object> cachedData = new HashMap<String, Object>();
	
	public void addCachedData(Map<String, Object> cachedData) {
		if (cachedData != null)
			this.cachedData.putAll(cachedData);
	}
		
    public Map<String, Object> getCachedData() {
		return cachedData;
	}
	
}