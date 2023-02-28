/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.cache;

import java.util.ArrayList;
import java.util.List;

import org.ehcache.Cache;

import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

/**
 * This class represents the translation cache.
 * 
 */
public class TranslationCache3 {
	
	private TranslationCache3() {}

	public static <V> Cache<String, V> getCache(CacheName c, Class<V> valueType) throws VIPCacheException {
		 return EhCache3Manager.getInstance().getCacheByName(c, String.class, valueType);
	}


	
	 public  static <K, V> Cache<K, V> getCache(CacheName cachename, Class<K> keyType, Class<V> valueType) throws VIPCacheException{
		 return EhCache3Manager.getInstance().getCacheByName(cachename, keyType,valueType);
	 }

	
	/**
	 * add a object to the translation cache.
	 *
	 * @param key
	 *            the key for cached object
	 * @param object
	 *            the cached object
	 */
	public static void addCachedObject(CacheName cachename, String key,ComponentSourceDTO object) throws VIPCacheException {
		getCache(cachename, String.class, ComponentSourceDTO.class).put(key, object);
	}
	
	public static <V> void addCachedObject(CacheName cachename, String key, Class<V> valueType, V object) throws VIPCacheException {
		getCache(cachename, String.class, valueType).put(key, object);
	}
	

	/**
	 * get cached object by the key.
	 *
	 * @param key
	 *            the key for cached object
	 * @return cached object from translation cache
	 */
	public static ComponentSourceDTO getCachedObject(CacheName cachename, String key)
			throws VIPCacheException {
		
		return getCache(cachename, String.class, ComponentSourceDTO.class).get(key);
	}
	
	
	public static <V>  V getCachedObject(CacheName cachename, String key, Class<V> valueType)
			throws VIPCacheException {
		
		return getCache(cachename, String.class, valueType).get(key);
	}

	/**
	 * update the cache object in translation cache.
	 *
	 * @param key
	 *            the key for cached object
	 * @param object
	 *            the object used to update cached's object
	 */
	public static void updateCachedObject(CacheName cachename, String key,
			ComponentSourceDTO object) throws VIPCacheException {
		getCache(cachename, String.class, ComponentSourceDTO.class).replace(key, object);
	}
	
	public static <V> void updateCachedObject(CacheName cachename, String key, Class<V> valueType, V object) throws VIPCacheException {
		getCache(cachename, String.class, valueType).replace(key, object);
	}


	/**
	 * delete the cache object in translation cache by the key.
	 *
	 * @param key
	 *            the key for cached object
	 */
	
	public static <V> void deleteCachedObject(CacheName cachename, String key, Class<V> valueType)
			throws VIPCacheException {
		getCache(cachename, String.class, valueType).remove(key);
	}

	
	
	/**
	 * get a cache's all keys
	 * @param cachename
	 * @param valueType
	 * @return
	 * @throws VIPCacheException
	 */

	public static <V> List<String> getKeys(CacheName cachename, Class<V> valueType)
			throws VIPCacheException {
		
		List<String> keys = new ArrayList<String>();
		getCache(cachename, String.class, valueType).forEach((action)->{
			 keys.add(action.getKey());
		 });

	    return keys;
	}

	/**
	 * Remove all cached objects.
	 */
	public static <V> void removeAll(CacheName cachename,  Class<V> valueType) throws VIPCacheException {
		getCache(cachename, String.class, valueType).clear();
	}

	public static void copy(Cache<String, ComponentSourceDTO> source, Cache<String, ComponentSourceDTO> target) {
		source.forEach((action)->target.put(action.getKey(), action.getValue()));
		
	}
}
