/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.persist.CacheSnapshot;
import com.vmware.vipclient.i18n.base.cache.persist.Loader;

/**
 * singleton class, used to maintain the cache for translation.
 *
 */
public class TranslationCacheManager {
    Logger                                 logger    = LoggerFactory.getLogger(TranslationCacheManager.class);
    private static TranslationCacheManager translationCacheManager;
    private int                            size      = 5000;
    private static Map<String, Cache>      container = new HashMap<String, Cache>();

    private TranslationCacheManager() {
    }

    public static synchronized TranslationCacheManager createTranslationCacheManager() {
        if (translationCacheManager == null) {
            translationCacheManager = new TranslationCacheManager();
        }
        return translationCacheManager;
    }

    public static Cache getCache(String name) {
        Cache c = container.get(name);
        if (c != null && c.isExpired()) {
            c.clear();
            c.setLastClean(System.currentTimeMillis());
        }
        return c;
    }

    public int registerCache(String className, Cache c) {
        if (c == null) {
            return -1;
        }
        if (container.size() > size) {
            Iterator it = container.keySet().iterator();
            String key = (String) it.next();
            container.remove(key);
            logger.info("Remove a cache[key is " + key + "] since it's full!");
        }
        Object inCacheObj = container.get(className);
        if (inCacheObj != null && (inCacheObj instanceof Cache)) {
            return container.size();
        }
        container.put(className, c);

        return container.size();
    }

    public int registerCache(String className, Class c) {
        if (c == null) {
            return -1;
        }
        if (container.size() > size) {
            Iterator it = container.keySet().iterator();
            String key = (String) it.next();
            container.remove(key);
            logger.info("Remove a cache[key is " + key + "] since it's full!");
        }
        Object inCacheObj = container.get(className);
        if (inCacheObj != null && (inCacheObj instanceof Cache)) {
            return container.size();
        }
        try {
            Object obj = c.newInstance();
            if (obj instanceof Cache) {
                container.put(className, (Cache) obj);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }

        return container.size();
    }

    public int unregisterCache(String cacheName) {
        Cache c = container.get(cacheName);
        if (c != null) {
            container.remove(cacheName);
        }
        return container.size();
    }

    public void clearCache() {
        this.container.clear();
    }

    public int size() {
        return container.size();
    }

    private static CacheSnapshot cacheSnapshot = null;

    public synchronized CacheSnapshot getCacheSnapshot() {
        if (cacheSnapshot == null) {
            cacheSnapshot = new CacheSnapshot();
            VIPCfg cfg = VIPCfg.getInstance();
            cacheSnapshot.setCacheRootPath(cfg.getCachePath());
            cacheSnapshot.setProductName(cfg.getProductName());
            cacheSnapshot.setVersion(cfg.getVersion());
            cacheSnapshot.setVipServer(cfg.getVipServer());
            Cache cache = cfg.getCacheManager().getCache(VIPCfg.CACHE_L3);
            if (null != cache) {
                cacheSnapshot.setLastClean(cache.getLastClean());
                cacheSnapshot.setExpiredTime(cache.getExpiredTime());
                cacheSnapshot.setDropId(cache.getDropId());
                cacheSnapshot.setComponents(cache.keySet());
            }
        }
        return cacheSnapshot;
    }

    public Loader getLoaderInstance(Class c) {
        Loader i = null;
        try {
            Object o = c.newInstance();
            if (o instanceof Loader) {
                i = (Loader) o;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }
        return i;
    }
}
