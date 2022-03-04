/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;

public class TranslationMessage2Test extends BaseTestClass {
    Logger             logger = LoggerFactory.getLogger(TranslationMessage2Test.class);

    TranslationMessage translation;

    @Before
    public void init() {
        VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
        gc.initializeVIPService();
        gc.createTranslationCache(MessageCache.class).setXCapacity(-1);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
    }

    @Test
    public void testIsAvailable() {
        String component = "Sample".equalsIgnoreCase(VIPCfg.getInstance()
                .getProductName()) ? "default" : "JAVA";
        Locale locale1 = new Locale("de", "DE");
        String key = "table.host";
        VIPCfg.getInstance().setPseudo(false);
        long bl = System.currentTimeMillis();
        logger.info(String.valueOf(bl));
        boolean a1 = translation.isAvailable(component, key, locale1);
        long b2 = System.currentTimeMillis();
        logger.info(String.valueOf(b2));
        Assert.assertTrue(a1);
        String key2 = "table.host";
        boolean a2 = translation.isAvailable(component, key2, locale1);
        long b3 = System.currentTimeMillis();
        logger.info(String.valueOf(b3));
        Assert.assertTrue((b3 - b2) < 10);
        Assert.assertTrue(a2);
        boolean a3 = translation.isAvailable(component, locale1);
        Assert.assertFalse(a3);
        Locale locale2 = new Locale("zh", "CN");
        boolean a4 = translation.isAvailable(component, locale2);
        Assert.assertFalse(a4);
        Cache c = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
        c.setExpiredTime(3000);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        	Thread.currentThread().interrupt();
        }
        Cache c2 = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
        Assert.assertTrue(c2.keySet().size() == 0);
        boolean a7 = translation.isAvailable(component, key, locale1);
        Assert.assertTrue(a7);
    }

    @Test
    public void testIsAvailable2() {
        String component = "Sample".equalsIgnoreCase(VIPCfg.getInstance()
                .getProductName()) ? "default" : "JAVA";
        Locale locale1 = new Locale("de", "DE");
        String key3 = "com.vmware.not.exist";
        boolean a5 = translation.isAvailable(component, key3, locale1);
        Assert.assertFalse(a5);
        boolean a6 = translation.isAvailable(component, key3, locale1);
        Assert.assertFalse(a6);
        Locale locale2 = new Locale("zh", "CN");
        String component2 = "NotExist";
        boolean a7 = translation.isAvailable(component2, locale2);
        Assert.assertFalse(a7);
        boolean a8 = translation.isAvailable(component2, locale2);
        Assert.assertFalse(a8);
        boolean a9 = translation.isAvailable(component, key3, locale1);
        Assert.assertFalse(a9);
        boolean a10 = translation.isAvailable(component, key3, locale1);
        Assert.assertFalse(a10);
    }
}
