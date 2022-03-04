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
import com.vmware.vipclient.i18n.base.cache.CacheItem;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.MessageCache2;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.CacheService;

public class MessageCache2Test1 extends BaseTestClass {

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
        c.setExpiredTime(3600);
        c.setXCapacity(2);
        ((MessageCache2) c).setYCapacity(2);
        cacheDTO = new MessagesDTO();
        cacheDTO.setProductID("dragon");
        cacheDTO.setVersion("1.0.0");
        cacheDTO.setComponent("JAVA");
        cacheDTO.setLocale("zh_CN");
        cacheService = new CacheService(cacheDTO);
    }

    @Test
    public void testLookForComponentTranslationInCache() {
        Map<String, String> msgObj = new HashMap<String, String>();
        msgObj.put("book", "@zh_CN@book");
        cacheService.addCacheOfComponent(new MessageCacheItem(msgObj));
        Map<String, String> msgObj2 = new HashMap<String, String>();
        msgObj2.put("book2", "@zh_CN@book2");
        cacheService.addCacheOfComponent(new MessageCacheItem(msgObj2));
        Map<String, String> msgObj3 = new HashMap<String, String>();
        msgObj3.put("book3", "@zh_CN@book3");
        cacheService.addCacheOfComponent(new MessageCacheItem(msgObj3));
        Map<String, String> messageMap = (Map<String, String>) cacheService
                .getCacheOfComponent().getCachedData();
        Assert.assertTrue(messageMap.size() == 3);
        VIPCfg.getInstance().getCacheManager().clearCache();
    }

    @Test
    public void testLookForTranslationInCache() {
    	Map<String, Object> cacheProps = new HashMap<String, Object>();
        Map<String, String> msgObj = new HashMap<String, String>();
        msgObj.put("book", "@zh_CN@book");
        cacheService.addCacheOfComponent(new MessageCacheItem(msgObj));
        Map<String, String> result = (Map<String, String>) cacheService
                .getCacheOfComponent().getCachedData();
        Assert.assertTrue(result.size() > 0);
        VIPCfg.getInstance().getCacheManager().clearCache();
    }

    @Test
    public void testAddCacheByComponent() {
        Map<String, String> msgObj = new HashMap<String, String>();
        msgObj.put("book", "@zh_CN@book");
        cacheService.addCacheOfComponent(new MessageCacheItem(msgObj));
        Map<String, String> msgObj2 = new HashMap<String, String>();
        msgObj2.put("book2", "@zh_CN@book3");
        cacheService.addCacheOfComponent(new MessageCacheItem(msgObj2));
        Map<String, String> msgObj3 = new HashMap<String, String>();
        msgObj3.put("book3", "@zh_CN@book3");
        cacheService.addCacheOfComponent(new MessageCacheItem(msgObj3));
        Map<String, String> msgObj4 = new HashMap<String, String>();
        msgObj4.put("book4", "@zh_CN@book4");
        cacheService.addCacheOfComponent(new MessageCacheItem(msgObj4));
        Map<String, String> messages = (Map<String, String>) cacheService.getCacheOfComponent().getCachedData();
        Assert.assertEquals("@zh_CN@book", messages.get("book"));
        VIPCfg.getInstance().getCacheManager().clearCache();
        Assert.assertNull(cacheService.getCacheOfComponent());
    }

    @SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
    // @Test
    public void testExpired() throws InterruptedException {
    	
        VIPCfg gc = VIPCfg.getInstance();
        Cache c = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
        Map data = new HashMap();
        String k = "com.vmware.test";
        String v = "It's a test";
        data.put(k, v);
        String cachedKey = "key";
        c.put(cachedKey, new MessageCacheItem(data));
        long expired = 2000;
        c.setExpiredTime(expired);
        MessageCacheItem cacheItem = (MessageCacheItem) TranslationCacheManager.getCache(VIPCfg.CACHE_L3).get(cachedKey);
        Map cachedData = cacheItem.getCachedData();
        logger.debug("cachedData: " + cachedData);
        Assert.assertNotNull(cachedData);
        Assert.assertEquals(v, cachedData.get(k));

        Thread.sleep(expired + 500);
        cacheItem = (MessageCacheItem) TranslationCacheManager.getCache(VIPCfg.CACHE_L3).get(cachedKey);
        Map cachedData2 = cacheItem.getCachedData();logger.debug("cachedData2: " + cachedData2);
        Assert.assertNull(cachedData2);
    }
}
