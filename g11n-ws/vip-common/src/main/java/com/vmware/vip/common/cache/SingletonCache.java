/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.cache;

import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

import java.util.List;

public interface SingletonCache {

    /**
     * add a object to the translation cache.
     *
     * @param key
     *            the key for cached object
     * @param object
     *            the cached object
     */
    public void addCachedObject(CacheName cachename, String key, ComponentSourceDTO object) throws VIPCacheException;
    public <V> void addCachedObject(CacheName cachename, String key, Class<V> valueType, V object) throws VIPCacheException;

    /**
     * get cached object by the key.
     *
     * @param key
     *            the key for cached object
     * @return cached object from translation cache
     */
    public ComponentSourceDTO getCachedObject(CacheName cachename, String key) throws VIPCacheException;
    public <V>  V getCachedObject(CacheName cachename, String key, Class<V> valueType) throws VIPCacheException;

    /**
     * update the cache object in translation cache.
     *
     * @param key
     *            the key for cached object
     * @param object
     *            the object used to update cache's object
     */
    public void updateCachedObject(CacheName cachename, String key, ComponentSourceDTO object) throws VIPCacheException;
    public <V> void updateCachedObject(CacheName cachename, String key, Class<V> valueType, V object) throws VIPCacheException;

    /**
     * delete the cache object in translation cache by the key.
     *
     * @param key
     *            the key for cached object
     */
    public <V> void deleteCachedObject(CacheName cachename, String key, Class<V> valueType) throws VIPCacheException;

    /**
     * get a cache's all keys
     * @param cachename
     * @param valueType
     * @return
     * @throws VIPCacheException
     */
    public <V> List<String> getKeys(CacheName cachename, Class<V> valueType) throws VIPCacheException;

    /**
     * Remove all cached objects.
     */
    public <V> void removeAll(CacheName cachename,  Class<V> valueType) throws VIPCacheException;

}
