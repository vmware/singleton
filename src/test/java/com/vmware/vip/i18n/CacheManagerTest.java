/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.CacheService;

public class CacheManagerTest extends BaseTestClass {

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
        Cache c = gc.createTranslationCache(MessageCache.class);
        c.setExpiredTime(3600);
        cacheDTO = new MessagesDTO();
        cacheDTO = new MessagesDTO();
        cacheDTO.setProductID("dragon");
        cacheDTO.setVersion("1.0.0");
        cacheDTO.setComponent("JAVA");
        cacheDTO.setLocale("zh_CN");
        cacheService = new CacheService(cacheDTO);
    }

    // @Test
    // public void testLookForComponentTranslationInCache() {
    // Map<String, String> msgObj = new HashMap<String, String>();
    // msgObj.put("book", "@zh_CN@book");
    // cacheService.addCacheOfComponent(msgObj);
    // Assert.assertNotNull("L3 Cache is null!", VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3));
    // Map<String, String> messageMap = cacheService
    // .getCacheOfComponent();
    // StringBuilder sb = new StringBuilder("The contents in cache is:\n");
    // for ( Entry<String, String> entry:messageMap.entrySet()) {
    // sb.append(entry.toString()).append(", ");
    // }
    // logger.debug(sb.toString());
    // Assert.assertTrue(messageMap.size() == 1);
    // VIPCfg.getInstance().getCacheManager().clearCache();
    // }

    // @Test
    // public void testLookForTranslationInCache() {
    // Map<String, String> msgObj = new HashMap<String, String>();
    // msgObj.put("book", "@zh_CN@book");
    // cacheService.addCacheOfComponent(msgObj);
    // Assert.assertNotNull("L3 Cache is null!", VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L3));
    // Map<String, String> result = cacheService
    // .getCacheOfComponent();
    // printCache(result);
    // Assert.assertTrue(result.size() > 0);
    // VIPCfg.getInstance().getCacheManager().clearCache();
    // }

    void printCache(Map<String, String> messageMap) {
        if (messageMap == null) {
            return;
        }
        StringBuilder sb = new StringBuilder(
                String.format("The size of cache is %d \nThe contents in cache is:\n", messageMap.size()));
        for (Entry<String, String> entry : messageMap.entrySet()) {
            sb.append(entry.toString()).append(", ");
        }
        logger.debug(sb.toString());
    }
}
