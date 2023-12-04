/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.cache;

import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class SingletonCacheImpl implements SingletonCache{

    private boolean enable;

    private CacheManager manager;

    /** Create singleton instance for EhCache3Manager. */

    public SingletonCacheImpl(boolean enable){
        this.enable = enable;
        if (this.enable){
            Configuration xmlConf = new XmlConfiguration(EhCache3Manager.class.getResource("/ehcache3.xml"));
            this.manager= CacheManagerBuilder.newCacheManager(xmlConf);
            this.manager.init();
        }
    }

    private <K, V> Cache<K, V> getCache(CacheName cachename, Class<K> keyType, Class<V> valueType) throws VIPCacheException{
        return this.manager.getCache(cachename.name(), keyType,valueType);
    }

    @Override
    public void addCachedObject(CacheName cachename, String key, ComponentSourceDTO object) throws VIPCacheException {
     if (enable){
         getCache(cachename, String.class, ComponentSourceDTO.class).put(key, object);
     }
    }

    @Override
    public <V> void addCachedObject(CacheName cachename, String key, Class<V> valueType, V object) throws VIPCacheException {
        if (enable){
            getCache(cachename, String.class, valueType).put(key, object);
        }
    }

    @Override
    public ComponentSourceDTO getCachedObject(CacheName cachename, String key) throws VIPCacheException {
        if (enable){
            return getCache(cachename, String.class, ComponentSourceDTO.class).get(key);
        }
        return null;
    }

    @Override
    public <V> V getCachedObject(CacheName cachename, String key, Class<V> valueType) throws VIPCacheException {
        if (enable){
            return getCache(cachename, String.class, valueType).get(key);
        }
        return null;
    }

    @Override
    public void updateCachedObject(CacheName cachename, String key, ComponentSourceDTO object) throws VIPCacheException {
        if (enable){
            getCache(cachename, String.class, ComponentSourceDTO.class).replace(key, object);
        }
    }

    @Override
    public <V> void updateCachedObject(CacheName cachename, String key, Class<V> valueType, V object) throws VIPCacheException {
        if (enable){
            getCache(cachename, String.class, valueType).replace(key, object);
        }
    }

    @Override
    public <V> void deleteCachedObject(CacheName cachename, String key, Class<V> valueType) throws VIPCacheException {
       if (enable){
           getCache(cachename, String.class, valueType).remove(key);
       }
    }

    @Override
    public <V> List<String> getKeys(CacheName cachename, Class<V> valueType) throws VIPCacheException {
        List<String> keys = new ArrayList<String>();
        if(enable) {
            getCache(cachename, String.class, valueType).forEach((action) -> {
                keys.add(action.getKey());
            });
        }
        return keys;
    }

    @Override
    public <V> void removeAll(CacheName cachename, Class<V> valueType) throws VIPCacheException {
       if(enable){
           getCache(cachename, String.class, valueType).clear();
       }
    }

    public void closeCacheManager() {
        if (this.enable){
            this.manager.close();
        }
    }
}
