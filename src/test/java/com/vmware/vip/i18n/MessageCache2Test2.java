/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.MessageCache2;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.CacheService;

public class MessageCache2Test2 extends BaseTestClass {

    private CacheService cacheService;

    private MessagesDTO  cacheDTO;

    @Before
    public void init() {
        VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
        gc.initializeVIPService();
        if (gc.getCacheManager() != null)
            gc.getCacheManager().clearCache();
        Cache c = gc.createTranslationCache(MessageCache2.class);
        MessageCache2 c2 = (MessageCache2) c;
        c2.setExpiredTime(3600);
        c2.setXCapacity(2);
        c2.setYCapacity(2);
        cacheDTO = new MessagesDTO();
        cacheDTO.setProductID("dragon");
        cacheDTO.setVersion("1.0.0");
        cacheDTO.setComponent("JAVA");
        cacheDTO.setLocale("zh_CN");
        cacheService = new CacheService(cacheDTO);
    }

    @Test
    public void testDisableCache() {
        VIPCfg gc = VIPCfg.getInstance();
        Cache c = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
        c.setXCapacity(0);
        ((MessageCache2) c).setYCapacity(0);
        Map data = new HashMap();
        String k = "com.vmware.test";
        String v = "It's a test";
        data.put(k, v);
        String cachedKey = "key";
        c.put(cachedKey, new MessageCacheItem(data));
        long expired = 60000;
        c.setExpiredTime(expired);
        MessageCacheItem cacheItem = (MessageCacheItem) TranslationCacheManager.getCache(VIPCfg.CACHE_L3).get(cachedKey);
        Assert.assertNull(cacheItem);
    }
}
