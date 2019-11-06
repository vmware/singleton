/*
 * Copyright 2019 VMware, Inc.
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
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.CacheService;

public class MessageCacheTest1 extends BaseTestClass {

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
        Cache c = gc.createTranslationCache(MessageCache.class);
        c.setExpiredTime(3600);
        cacheDTO = new MessagesDTO();
        cacheDTO.setProductID("dragon");
        cacheDTO.setVersion("1.0.0");
        cacheDTO.setComponent("JAVA");
        cacheDTO.setLocale("zh_CN");
        cacheService = new CacheService(cacheDTO);
    }

    @Test
    public void testSetCapacityByKey() {
        VIPCfg gc = VIPCfg.getInstance();
        MessageCache c = (MessageCache) gc.createTranslationCache(MessageCache.class);
        c.setCapacityByKey(5);
        Map<String, String> msgObj = new HashMap<String, String>();
        msgObj.put("book", "@zh_CN@book");
        cacheService.addCacheOfComponent(msgObj);
        Map<String, String> msgObj2 = new HashMap<String, String>();
        msgObj2.put("book2", "@zh_CN@book2");
        cacheService.addCacheOfComponent(msgObj2);
        Map<String, String> msgObj3 = new HashMap<String, String>();
        msgObj3.put("book3", "@zh_CN@book3");
        msgObj3.put("book4", "@zh_CN@book4");
        msgObj3.put("book5", "@zh_CN@book5");
        cacheService.addCacheOfComponent(msgObj3);
        Map<String, String> msgObj4 = new HashMap<String, String>();
        msgObj4.put("book6", "@zh_CN@book6");
        msgObj4.put("book7", "@zh_CN@book7");
        msgObj4.put("book8", "@zh_CN@book8");
        msgObj4.put("book9", "@zh_CN@book9");
        cacheService.addCacheOfComponent(msgObj4);
        Map<String, String> messageMap = cacheService
                .getCacheOfComponent();
        Assert.assertTrue(messageMap.size() == 4);
        c.clear();
        Assert.assertTrue(c.size() == 0);
        VIPCfg.getInstance().getCacheManager().clearCache();
        Map<String, String> messageMap3 = cacheService
                .getCacheOfComponent();
        Assert.assertNull(messageMap3);
    }

    @Test
    public void testAddCacheByComponent() {
        this.init();
        Map<String, String> msgObj = new HashMap<String, String>();
        msgObj.put("book", "@zh_CN@book");
        cacheService.addCacheOfComponent(msgObj);
        Map<String, String> mp = cacheService.getCacheOfComponent();
        Assert.assertEquals("@zh_CN@book", mp.get("book"));
        VIPCfg.getInstance().getCacheManager().clearCache();
        Assert.assertNull(cacheService.getCacheOfComponent());
    }

    @SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
    @Test
    public void testExpired() {
        VIPCfg gc = VIPCfg.getInstance();
        Cache c = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
        Map data = new HashMap();
        String k = "com.vmware.test";
        String v = "It's a test";
        data.put(k, v);
        String cachedKey = "key";
        c.put(cachedKey, data);
        long expired = 20000;
        c.setExpiredTime(expired);
        Map cachedData = TranslationCacheManager.getCache(VIPCfg.CACHE_L3).get(cachedKey);
        Assert.assertNotNull(cachedData);
        Assert.assertEquals(v, cachedData.get(k));
        try {
            Thread.sleep(expired + 10000);
            Map cachedData2 = TranslationCacheManager.getCache(VIPCfg.CACHE_L3).get(cachedKey);
            Assert.assertNull(cachedData2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
