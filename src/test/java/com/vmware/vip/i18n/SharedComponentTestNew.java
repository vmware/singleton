/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.Locale;
import java.util.Map;

import com.vmware.vipclient.i18n.VIPCfgFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;

public class SharedComponentTestNew extends BaseTestClassNew {
    TranslationMessage mainTranslation;
    TranslationMessage subTranslation;
    String             mainProductName = "JavaclientTest";
    String             subProductName  = "JavaclientTest1";

    @Before
    public void init() {
        VIPCfg mainCfg = null;
        try {
            mainCfg = VIPCfgFactory.initialize("vipconfig", true);
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }

        if (TranslationCacheManager.getInstance() != null)
            TranslationCacheManager.getInstance().clearCache();
        mainCfg.createTranslationCache(MessageCache.class);
        mainCfg.createFormattingCache(FormattingCache.class);
        mainTranslation = new TranslationMessage();
        mainTranslation.setCfg(mainCfg);

        VIPCfg subCfg = null;
        try {
            subCfg = VIPCfgFactory.initialize("vipconfig-child");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
        subTranslation = new TranslationMessage();
        subTranslation.setCfg(subCfg);

    }

    @Test
    public void testGetSharedModuleTranslation() {
        Locale zhLocale = new Locale("zh", "Hans");
        String comp = "JAVA";
        String key = "table.host";
        String source = "Host";
        String trans1 = mainTranslation.getString(zhLocale, comp, key, source, "");
        logger.debug("pseudoTrans1: " + trans1);

        Locale zhLocale2 = new Locale("de", "");
        String comp2 = "JSP";
        String key2 = "table.head";
        String source2 = "VM";
        String trans2 = subTranslation.getString(zhLocale2, comp2, key2, source2, "");
        logger.debug("pseudoTrans1: " + trans2);
        Assert.assertTrue(VIPCfgFactory.globalCfg.getProductName().equals(mainProductName));
        Assert.assertTrue(subTranslation.getCfg().getProductName().equals(subProductName));

        Cache c = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
        Map<String, MessageCacheItem> m = ((MessageCache) c).getCachedTranslationMap();

        Assert.assertTrue(m.size() == 2);
        // TODO Null values are not allowed to be stored in the cache anymore.
        // The following key must have non-null values to be stored.
        //Assert.assertTrue(m.containsKey("JavaclientTest1_2.0.0_JSP_false_#de"));
        Assert.assertTrue(m.containsKey("JavaclientTest_1.0.0_JAVA_false_#en"));
    }
}
