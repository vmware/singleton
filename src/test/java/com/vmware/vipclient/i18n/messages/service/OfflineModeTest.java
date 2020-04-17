/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Locale;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vip.i18n.BaseTestClass;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;

public class OfflineModeTest extends BaseTestClass {

	String component = "JAVA";
    String key = "LeadTest";
    String source = "[{0}] Test alert";
    Locale locale = new Locale("fil");
    String comment = "comment";
    String messageFil = "[{0}] Alerto sa pagsusuri";
    String messageFr ="[{0}] Alerte de test";
    Object[] args = { "a" };

    MessagesDTO dto = new MessagesDTO();
    
    @Before
    public void init() {
        dto.setComponent(component);
        dto.setKey(key);
        dto.setSource(source);
        dto.setLocale(locale.toLanguageTag());
        VIPCfg.resetInstance();
        I18nFactory.resetInstance();
    }
    
    @Test
    public void testGetMsgsOfflineMode() {
    	VIPCfg cfg = VIPCfg.getInstance();
        try {
            cfg.initialize("vipconfig-offline");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
    	
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        
    	CacheService cs = new CacheService(dto);
    	
    	translation.getString(locale, component, key, source, comment, args);
    	
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
    	assertNotNull(cacheItem);
    	assertEquals(messageFil, cacheItem.cachedData.get(key));	
    }
    
    @Test
    public void testGetMsgsOfflineModeBundleNotExist() {
    	VIPCfg cfg = VIPCfg.getInstance();
        try {
            cfg.initialize("vipconfig-offline");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
    	
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);

        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        
        // Bundle does not exist locally
        Locale newLocale = new Locale("es");
        dto.setLocale(newLocale.toLanguageTag());
        
    	CacheService cs = new CacheService(dto);
    	
    	translation.getString(newLocale, component, key, source, comment, args);
    	
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
    	assertNull(cacheItem);
    }
    
    @Test
    public void testGetMsgsOfflineModeAfterOnlineError() {
    	VIPCfg cfg = VIPCfg.getInstance();
        try {
            cfg.initialize("vipconfig-online-offline");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
    	cfg.initializeVIPService();
    	
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        
    	CacheService cs = new CacheService(dto);
    	
    	translation.getString(locale, component, key, source, comment, args);
    	
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
    	assertNotNull(cacheItem);
    	assertEquals(messageFil, cacheItem.cachedData.get(key));
    	
    }
    
    @Test
    public void testGetMsgsOnlineModePriority() {
    	VIPCfg cfg = VIPCfg.getInstance();
        try {
            cfg.initialize("vipconfig-online-offline");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
    	cfg.initializeVIPService();
    	
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        
        // This bundle is only available online (remote).
        Locale newLocale = new Locale("fr");
        dto.setLocale(newLocale.toLanguageTag());
        
    	CacheService cs = new CacheService(dto);
    	
    	translation.getString(newLocale, component, key, source, comment, args);
    	
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
    	assertNotNull(cacheItem);
    	assertEquals(messageFr, cacheItem.cachedData.get(key));
    }
    
    @Test
    public void testGetMsgsBothOfflineAndOnlineFailed() {
    	VIPCfg cfg = VIPCfg.getInstance();
        try {
            cfg.initialize("vipconfig-online-offline");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
    	cfg.initializeVIPService();
    	
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        
        // This bundle is available neither online (remote) nor offline (local).
        Locale newLocale = Locale.ITALIAN;
        dto.setLocale(newLocale.toLanguageTag());
        
    	CacheService cs = new CacheService(dto);
    	
    	translation.getString(newLocale, component, key, source, comment, args);
    	
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
    	assertNotNull(cacheItem);
    	assertEquals(source, cacheItem.cachedData.get(key));
    }
    
    @AfterClass
    public void after() {
        VIPCfg.resetInstance();
        I18nFactory.resetInstance();
    }
}
