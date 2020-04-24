/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.api.opt.SourceOpt;
import com.vmware.vipclient.i18n.messages.api.opt.source.ResourceBundleSrcOpt;

public class SharedComponentTest extends BaseTestClass {
    TranslationMessage mainTranslation;
    TranslationMessage subTranslation;
    String             mainProductName = "JavaclientTest";
    String             subProductName  = "JavaclientTest1";

    @Before
    public void init() {
        VIPCfg mainCfg = VIPCfg.getInstance();
        try {
            mainCfg.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
        mainCfg.initializeVIPService();
        if (mainCfg.getCacheManager() != null)
            mainCfg.getCacheManager().clearCache();
        mainCfg.createTranslationCache(MessageCache.class);
        mainCfg.createFormattingCache(FormattingCache.class);
        I18nFactory i18n = I18nFactory.getInstance(mainCfg);
        mainTranslation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);

        VIPCfg subCfg = VIPCfg.getSubInstance(subProductName);
        try {
            subCfg.initialize("vipconfig-child");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
        subTranslation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class, subCfg);
    }

    @Test
    public void testGetSharedModuleTranslation_() {
        Locale zhLocale = new Locale("zh", "Hans");
        String comp = "JAVA";
        String key = "table.host";
        String source = "Host";
        String trans1 = mainTranslation.getString(zhLocale, comp, key, source, "");
        logger.debug("pseudoTrans1: " + trans1);

        Locale zhLocale2 = new Locale("de", "");
        String comp2 = "JSP";
        String key2 = "table.head";
        SourceOpt srcOpt = new ResourceBundleSrcOpt("messages", Locale.ENGLISH);
        String trans2 = subTranslation.getMessage(zhLocale2, comp2, srcOpt, key2, "", null);
        		
        logger.debug("pseudoTrans1: " + trans2);
        Assert.assertTrue(VIPCfg.getInstance().getProductName().equals(mainProductName));
        Assert.assertTrue(subTranslation.getCfg().getProductName().equals(subProductName));

        VIPCfg gc = VIPCfg.getInstance();
        Cache c = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
        Map<String, MessageCacheItem> m = ((MessageCache) c).getCachedTranslationMap();
        
        Assert.assertTrue(m.size() == 2);
        // TODO Null values are not allowed to be stored in the cache anymore. 
        // The following keys must have non-null values to be stored. 
        //Assert.assertTrue(m.containsKey("JavaclientTest_1.0.0_JAVA_false_#zh"));
        //Assert.assertTrue(m.containsKey("JavaclientTest1_2.0.0_JSP_false_#de"));
        
        Assert.assertTrue(m.containsKey("JavaclientTest_1.0.0_JAVA_false_#en-US"));
    }
    
    @Test
    @Deprecated
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
        Assert.assertTrue(VIPCfg.getInstance().getProductName().equals(mainProductName));
        Assert.assertTrue(subTranslation.getCfg().getProductName().equals(subProductName));

        VIPCfg gc = VIPCfg.getInstance();
        Cache c = TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
        Map<String, MessageCacheItem> m = ((MessageCache) c).getCachedTranslationMap();
        
        Assert.assertTrue(m.size() == 2);
        // TODO Null values are not allowed to be stored in the cache anymore. 
        // The following keys must have non-null values to be stored. 
        //Assert.assertTrue(m.containsKey("JavaclientTest_1.0.0_JAVA_false_#zh"));
        //Assert.assertTrue(m.containsKey("JavaclientTest1_2.0.0_JSP_false_#de"));
        
        Assert.assertTrue(m.containsKey("JavaclientTest_1.0.0_JAVA_false_#en-US"));
    }
}
