/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.CacheItem;
import com.vmware.vipclient.i18n.base.cache.FormatCacheItem;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.LocaleUtility;

import java.util.*;

public class CacheService {
    private MessagesDTO dto;

    public CacheService(MessagesDTO dto) {
        this.dto = dto;
    }
    
    public MessageCacheItem getCacheOfComponent(List<Locale> supportedLocales) {
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c == null)
            return null;
        String cacheKey = dto.getCompositStrAsCacheKey();
        MessageCacheItem cacheItem = (MessageCacheItem) c.get(cacheKey);
        if (cacheItem != null)
            return cacheItem;
        Locale matchedLocale = LocaleUtility.pickupLocaleFromList(supportedLocales,
                this.getLocaleByCachedKey(cacheKey));
        cacheKey = cacheKey.substring(0,
                cacheKey.indexOf(ConstantsKeys.UNDERLINE_POUND) + 2)
                + matchedLocale.toLanguageTag();
        return (MessageCacheItem) c.get(cacheKey);
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

    public boolean isContainComponent(List<Locale> supportedLocales) {
        String cacheKey = dto.getCompositStrAsCacheKey();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c == null)
            return false;

        if (c.get(cacheKey) != null)
            return true;
        Locale matchedLocale = LocaleUtility.pickupLocaleFromList(supportedLocales,
                this.getLocaleByCachedKey(cacheKey));
        cacheKey = cacheKey.substring(0,
                cacheKey.indexOf(ConstantsKeys.UNDERLINE_POUND) + 2)
                + matchedLocale.toLanguageTag();
        return c.get(cacheKey) != null;
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

    private Locale getLocaleByCachedKey(String key) {
        String locale = key.substring(
                key.indexOf(ConstantsKeys.UNDERLINE_POUND) + 2, key.length());
        return Locale.forLanguageTag(locale.replace("_", "-"));
    }
    
}
