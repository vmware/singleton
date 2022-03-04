/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.HashMap;
import java.util.UUID;

import org.junit.Before;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.CacheMode;
import com.vmware.vipclient.i18n.base.cache.MessageCache2;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.cache.persist.CacheSyncThreadPool;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.CacheService;

public class PersistantCacheTest extends BaseTestClass {

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
        Cache c = gc.createTranslationCache(MessageCache2.class);
        c.setExpiredTime(3600000);
        c.setXCapacity(2);
        ((MessageCache2) c).setYCapacity(2);
        gc.setCacheMode(CacheMode.DISK);
        gc.setCachePath("C:\\");
        cacheDTO = new MessagesDTO();
        cacheDTO = new MessagesDTO();
        cacheDTO.setProductID("dragon");
        cacheDTO.setVersion("1.0.0");
        cacheDTO.setLocale("zh_CN");
        cacheService = new CacheService(cacheDTO);
    }

    // @Test
    public void testLookForComponentTranslationInCache() {
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map2 = new HashMap<String, String>();
            String component = UUID.randomUUID().toString();
            cacheDTO.setComponent(component);
            for (int j = 0; j < 2; j++) {
                String key = new Integer(j).toString();
                String source = key;
                cacheDTO.setKey(key);
                cacheDTO.setSource(source);
                map2.put(key, source);
            }
            MessageCacheItem cacheItem = new MessageCacheItem(map2);
            cacheService.addCacheOfComponent(cacheItem);
            Cache c = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
            logger.debug(String.valueOf(c.size()));
        }
        CacheSyncThreadPool t = new CacheSyncThreadPool();
        t.run();
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
        	Thread.currentThread().interrupt();
        }
    }
}
