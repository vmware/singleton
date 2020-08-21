/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormatCacheItem;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.LocaleUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class CacheService {
    private MessagesDTO dto;

    public CacheService(MessagesDTO dto) {
        this.dto = dto;
    }
    
    public MessageCacheItem getCacheOfComponent() {
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
            return (MessageCacheItem) c.get(cacheKey);
        }
    }

    public void addCacheOfComponent(MessageCacheItem itemToCache) {
        String cacheKey = dto.getCompositStrAsCacheKey();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c != null) {
            c.put(cacheKey, itemToCache);
        }
    }

    public void updateCacheOfComponent(MessageCacheItem itemToCache) {
        String cacheKey = dto.getCompositStrAsCacheKey();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c != null) {
            MessageCacheItem cacheItem = (MessageCacheItem) c.get(cacheKey);
            if (cacheItem == null) {
            	cacheItem = new MessageCacheItem();
            	c.put(cacheKey, cacheItem);
            }
            cacheItem.setCacheItem(itemToCache);
        }
    }

    public boolean isContainComponent() {
        boolean f = false;
        String cacheKey = dto.getCompositStrAsCacheKey();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c != null) {
            f = c.get(cacheKey) != null;
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
        	MessageCacheItem cacheItem = (MessageCacheItem) c.get(cacheKey);
        	if (cacheItem != null) {
        		return (Map<String, String>) cacheItem.getCachedData();
        	}
        }
        return null;
    }

    public void addCacheOfStatus(Map<String, String> dataMap) {
        String cacheKey = dto.getTransStatusAsCacheKey();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c != null) {
            c.put(cacheKey, new MessageCacheItem(dataMap));
        }
    }

    public List<Locale> getSupportedLocalesFromCache() {
        List<Locale> locales = new ArrayList<Locale>();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L2);
        if (c == null) {
            return locales;
        }
        Set<String> keySet = c.keySet();
       
        for (String key : keySet) {
        	if (key.startsWith(ConstantsKeys.DISPNS_PREFIX)) {
        		FormatCacheItem cacheItem = (FormatCacheItem) c.get(key);
        		Map<String, String> langTagToDisplayNameMap = cacheItem.getCachedData();
        		for (String languageTag : langTagToDisplayNameMap.keySet()) {
        			locales.add(Locale.forLanguageTag(languageTag));
        		}
        		break;
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
