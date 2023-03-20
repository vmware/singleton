/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.cache;


import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;

import com.vmware.vip.common.exceptions.VIPCacheException;


/**
 * EhCache Manager, manage and provide the Cache instance by the name.
 * 
 */
public class EhCache3Manager{
  /** The manager is used for multiple caches. */
	private static Configuration xmlConf = new XmlConfiguration(EhCache3Manager.class.getResource("/ehcache3.xml"));
	
	private CacheManager manager;
    /** Create singleton instance for EhCache3Manager. */
    
    private EhCache3Manager() {
    	this.manager= CacheManagerBuilder.newCacheManager(xmlConf);
    	this.manager.init();
    }
    
    private static class EhCache3ManagerHandler{
    	public static EhCache3Manager ehCacheManager = new EhCache3Manager();
    }
    
    

    public static EhCache3Manager getInstance() throws VIPCacheException {
    	return EhCache3ManagerHandler.ehCacheManager;
    }
   
    /**
     * Get the cache object by its name defined in ehcache.xml
     * @param cacheName the name used for looking cache in CacheManager.
     * @return a Cache found in the cache manager.
     
     * @throws VIPCacheException
     */
    public  <K, V> Cache<K, V> getCacheByName(CacheName cachename, Class<K> keyType, Class<V> valueType) throws VIPCacheException {
    	if(manager == null) {
    		throw new VIPCacheException("Unable to get cache from manager.");
    	}
        return manager.getCache(cachename.name(), keyType, valueType);
    }
    
    
    public void closeCacheManager() {
    	 manager.close();
    }
}
