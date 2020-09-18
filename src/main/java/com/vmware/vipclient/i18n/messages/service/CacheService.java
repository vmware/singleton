/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.CacheItem;
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

    public MessageCacheItem getCacheOfComponent() {
        String cacheKey = dto.getCompositStrAsCacheKey();
        Locale matchedLocale = LocaleUtility.pickupLocaleFromList(this.getLocalesOfCachedMsgs(),
                this.getLocaleByCachedKey(cacheKey));
        cacheKey = cacheKey.substring(0,
                cacheKey.indexOf(ConstantsKeys.UNDERLINE_POUND) + 2)
                + matchedLocale.toLanguageTag();
        return (MessageCacheItem) this.getCacheItem(cacheKey);
    }

    public void addCacheOfComponent(MessageCacheItem itemToCache) {
        String cacheKey = dto.getCompositStrAsCacheKey();
        this.addCacheItem(cacheKey, itemToCache);
    }

    public void updateCacheOfComponent(MessageCacheItem itemToCache) {
        String cacheKey = dto.getCompositStrAsCacheKey();
        MessageCacheItem cacheItem = (MessageCacheItem) this.getCacheItem(cacheKey);
        if (cacheItem == null) {
            cacheItem = new MessageCacheItem();
            this.addCacheItem(cacheKey, cacheItem);
        }
        cacheItem.setCacheItem(itemToCache);
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
        String cacheKey = dto.getTransStatusAsCacheKey();
        return this.getCacheItem(cacheKey) != null;
    }

    public Map<String, String> getCacheOfStatus() {
        String cacheKey = dto.getTransStatusAsCacheKey();
        MessageCacheItem cacheItem = (MessageCacheItem) this.getCacheItem(cacheKey);
        if (cacheItem != null) {
            return cacheItem.getCachedData();
        }
        return null;
    }

    public void addCacheOfStatus(Map<String, String> dataMap) {
        String cacheKey = dto.getTransStatusAsCacheKey();
        addCacheItem(cacheKey, new MessageCacheItem(dataMap));
    }

    public List<Locale> getLocalesOfCachedMsgs() {
        List<Locale> locales = new LinkedList<>();
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c != null) {
            Set<String> cacheKeys = c.keySet();
            for (String key : cacheKeys) {
                Locale locale = getLocaleByCachedKey(key);
                if (locale != null)
                    locales.add(locale);
            }
        }
        return locales;
    }

    private Locale getLocaleByCachedKey(String key) {
        if (key.startsWith(ConstantsKeys.DISPNS_PREFIX))
            return null;
        String locale = key.substring(
                key.indexOf(ConstantsKeys.UNDERLINE_POUND) + 2, key.length());
        return Locale.forLanguageTag(locale.replace("_", "-"));
    }

    private void addCacheItem(String key, CacheItem cacheItem) {
        if (key != null && cacheItem != null) {
            Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
            if (c != null) {
                c.put(key, cacheItem);
            }
        }
    }

    private CacheItem getCacheItem(String key) {
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3);
        if (c == null)
            return null;
        CacheItem cacheItem = c.get(key);
        return cacheItem;
    }
}
