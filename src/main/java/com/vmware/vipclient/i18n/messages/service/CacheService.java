/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.CacheItem;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class CacheService {
    private MessagesDTO dto;

    public CacheService(MessagesDTO dto) {
        this.dto = dto;
    }
    
    public boolean isExpired() {
    	String cacheKey = dto.getCompositStrAsCacheKey();
    	Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
    	return c.isExpired(cacheKey);
    }
    
    public CacheItem getCacheOfComponent() {
        String cacheKey = dto.getCompositStrAsCacheKey();
        Locale matchedLocale = LocaleUtility.pickupLocaleFromList(
                this.getSupportedLocalesFromCache(),
                this.getLocaleByCachedKey(cacheKey));
        cacheKey = cacheKey.substring(0,
                cacheKey.indexOf(ConstantsKeys.UNDERLINE_POUND) + 2)
                + matchedLocale.toLanguageTag();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c == null) {
            return null;
        } else {
            return c.get(cacheKey);
        }
    }

    public void addCacheOfComponent(CacheItem itemToCache) {
        String cacheKey = dto.getCompositStrAsCacheKey();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c != null) {
            c.put(cacheKey, itemToCache);
        }
    }

    public void updateCacheOfComponent(CacheItem itemToCache) {
        String cacheKey = dto.getCompositStrAsCacheKey();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c != null) {
            CacheItem cacheItem = c.get(cacheKey);
            if (cacheItem == null) {
            	cacheItem = new CacheItem();
            	c.put(cacheKey, cacheItem);
            }
            cacheItem.addCacheDataAndProperties(itemToCache);
        }
    }

    public boolean isContainComponent() {
        boolean f = false;
        String cacheKey = dto.getCompositStrAsCacheKey();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c != null) {
            f = c.keySet().contains(cacheKey);
        }
        return f;
    }

    public boolean isContainStatus() {
        boolean f = false;
        String cacheKey = dto.getTransStatusAsCacheKey();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c != null) {
            f = c.keySet().contains(cacheKey);
        }
        return f;
    }

    public Map<String, String> getCacheOfStatus() {
        String cacheKey = dto.getTransStatusAsCacheKey();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c != null) {
        	CacheItem cacheItem = c.get(cacheKey);
        	if (cacheItem != null) {
        		return (Map<String, String>) cacheItem.getCachedData();
        	}
        }
        return null;
    }

    public void addCacheOfStatus(Map<String, String> dataMap, Map<String, Object> cacheProps) {
        String cacheKey = dto.getTransStatusAsCacheKey();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c != null) {
            c.put(cacheKey, new CacheItem(dataMap, cacheProps));
        }
    }

    public List<Locale> getSupportedLocalesFromCache() {
        List<Locale> locales = new ArrayList<Locale>();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c == null) {
            return locales;
        }
        Set<String> keySet = c.keySet();
        Object[] keys = keySet.toArray();
        Map<String, Object> tempMap = new HashMap<String, Object>();
        for (Object key : keys) {
            String ckey = (String) key;
            String locale = ckey.substring(
                    ckey.indexOf(ConstantsKeys.UNDERLINE_POUND) + 2,
                    ckey.length());
            if (!tempMap.containsKey(locale)) {
                locales.add(Locale.forLanguageTag(locale.replace("_", "-")));
                tempMap.put(locale, locale);
            }
        }
        return locales;
    }

    private Locale getLocaleByCachedKey(String key) {
        String locale = key.substring(
                key.indexOf(ConstantsKeys.UNDERLINE_POUND) + 2, key.length());
        return Locale.forLanguageTag(locale.replace("_", "-"));
    }
    
}
