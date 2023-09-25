/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.CacheItem;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

import java.util.*;

public class CacheService {
    private MessagesDTO dto;

    public CacheService(MessagesDTO dto) {
        this.dto = dto;
    }

    public MessageCacheItem getCacheOfComponent() {
        String cacheKey = dto.getCompositStrAsCacheKey();
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

    public MessageCacheItem getCacheOfLocales(DataSourceEnum dataSource) {
        String cacheKey = dto.getLocalesCacheKey(dataSource);
        return (MessageCacheItem) this.getCacheItem(cacheKey);
    }
    public void addCacheOfLocales(MessageCacheItem itemToCache, DataSourceEnum dataSource) {
        String cacheKey = dto.getLocalesCacheKey(dataSource);
        addCacheItem(cacheKey, itemToCache);
    }

    public MessageCacheItem getCacheOfMultiVersionKey() {
        String cacheKey = dto.getCompositStrAsCacheKey() + ConstantsKeys.UNDERLINE + dto.getKey();
        return (MessageCacheItem) this.getCacheItem(cacheKey);
    }

    public void addCacheOfMultiVersionKey(MessageCacheItem itemToCache) {
        String cacheKey = dto.getCompositStrAsCacheKey() + ConstantsKeys.UNDERLINE + dto.getKey();
        this.addCacheItem(cacheKey, itemToCache);
    }

    private void addCacheItem(String key, CacheItem cacheItem) {
        if (key != null && cacheItem != null) {
            Cache c = TranslationCacheManager.getInstance().getCache(VIPCfg.CACHE_L3);
            if (c != null) {
                c.put(key, cacheItem);
            }
        }
    }

    private CacheItem getCacheItem(String key) {
        Cache c = TranslationCacheManager.getInstance().getCache(VIPCfg.CACHE_L3);
        if (c == null)
            return null;
        return c.get(key);
    }
}
