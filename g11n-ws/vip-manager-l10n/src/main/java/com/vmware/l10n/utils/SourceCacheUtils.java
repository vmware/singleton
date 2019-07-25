/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

public class SourceCacheUtils {
	
	private SourceCacheUtils() {}
	private static Logger logger = LoggerFactory.getLogger(SourceCacheUtils.class);
	public static synchronized Object getSourceCache(String ehcacheKey) throws VIPCacheException {
		logger.debug("get the source catche by ehcatchKey----{}", ehcacheKey);
		return TranslationCache3.getCachedObject(CacheName.SOURCE, ehcacheKey);
	}
	
	public static synchronized ComponentSourceDTO getSourceCacheWithDel(String ehcacheKey) throws VIPCacheException {
		logger.debug("Get and Del the source catche by ehcatchKey----{}", ehcacheKey);
		ComponentSourceDTO cachedComDTO  = (ComponentSourceDTO) TranslationCache3.getCachedObject(CacheName.SOURCE, ehcacheKey);
		if (!StringUtils.isEmpty(cachedComDTO)) {
			TranslationCache3.deleteCachedObject(CacheName.SOURCE, ehcacheKey, ComponentSourceDTO.class);
		}
		return cachedComDTO;
	}
	
	
	public static synchronized void updateSourceCache(String ehcacheKey, ComponentSourceDTO cacheComSourceDTO) throws VIPCacheException {
		logger.debug("update the source catche by ehcatchKey----{}", ehcacheKey);
		TranslationCache3.updateCachedObject(CacheName.SOURCE, ehcacheKey,cacheComSourceDTO);
	}
	
	public static synchronized void addSourceCache(String ehcacheKey, ComponentSourceDTO cacheComSourceDTO) throws VIPCacheException {
		logger.debug("add the new key of the source catche by ehcatchKey----{}", ehcacheKey);
		TranslationCache3.addCachedObject(CacheName.SOURCE, ehcacheKey, cacheComSourceDTO);
	}
	
	public static synchronized void delSourceCacheByKey(String ehcacheKey) throws VIPCacheException {
		logger.debug("delete the source catche by ehcatchKey----{}", ehcacheKey);
		TranslationCache3.deleteCachedObject(CacheName.SOURCE, ehcacheKey, ComponentSourceDTO.class);
	}

}
