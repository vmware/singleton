/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.vmware.vip.i18n.BaseTestClass;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;

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
    VIPCfg cfg;
    
    @Before
    public void init() {
        dto.setComponent(component);
        dto.setKey(key);
        dto.setSource(source);
        dto.setLocale(locale.toLanguageTag());
        cfg = VIPCfg.getInstance();
  	  
        try {
            cfg.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
       
    }
    
    @Test
    public void testGetMsgsOfflineMode() {
    	String offlineResourcesBaseUrlOrig = cfg.getOfflineResourcesBaseUrl();
    	cfg.setOfflineResourcesBaseUrl("offlineBundles/");
    	List<DataSourceEnum> msgOriginsQueueOrig = cfg.getMsgOriginsQueue();
    	cfg.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(Arrays.asList(DataSourceEnum.Bundle)));
    	
        Cache c = cfg.createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(cfg);
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        
    	CacheService cs = new CacheService(dto);
    	String message = translation.getMessage(locale, component, key, args);
    	assertEquals(FormatUtils.format(messageFil, locale, args), message);
    	
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
    	assertEquals(messageFil, cacheItem.getCachedData().get(key));	
    	
    	cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
    	cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
    }
    
    // TODO Enable source message fallback @Test
//    public void testGetMsgsFailedUseSourceMessage() { 
//    	// Offline mode only; neither target locale bundle nor default locale bundle exists 
//    	// SourceOpt is defined to use the source message
//    	VIPCfg cfg = VIPCfg.getInstance();
//    	
//    	// SourceOpt is identified 
//    	// This allows fallback to source message when all else fails. 
//    	Source srcOpt = new ResourceBundleSrc("messages", LocaleUtility.defaultLocale);
//        try {
//            cfg.initialize("vipconfig-offline", srcOpt);
//        } catch (VIPClientInitException e) {
//            logger.error(e.getMessage());
//        }
//    	
//        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
//        TranslationCacheManager.cleanCache(c);
//        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
//        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
//
//        dto.setProductID(VIPCfg.getInstance().getProductName());
//        dto.setVersion(VIPCfg.getInstance().getVersion());
//        
//        // Bundle does not exist locally
//        Locale newLocale = new Locale("es");
//        dto.setLocale(newLocale.toLanguageTag());
//        
//    	CacheService cs = new CacheService(dto);
//    	
//    	String message = translation.getMessage(newLocale, component,  key, args);
//    	// Returns source message
//    	assertEquals(FormatUtils.format(srcOpt.getMessage(key), srcOpt.getLocale(), args), message);
//    	
//    	// Nothing is stored in cache
//    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
//    	assertNull(cacheItem);
//    }
    
    @Test
    public void testGetMsgsFailedNoSourceOpt() { 
    	// Offline mode only; neither target locale bundle nor default locale bundle exists
    	// SourceOpt is not defined so return the key
    	String key = "does.not.exist";
    	String offlineResourcesBaseUrlOrig = cfg.getOfflineResourcesBaseUrl();
    	cfg.setOfflineResourcesBaseUrl("offlineBundles/");
    	List<DataSourceEnum> msgOriginsQueueOrig = cfg.getMsgOriginsQueue();
    	cfg.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(Arrays.asList(DataSourceEnum.Bundle)));
    	
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
    	
    	
    	String message = translation.getMessage(newLocale, component, key, args);
    	// Returns key
    	assertEquals(key, message);
    	
    	// Nothing is stored in cache
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
    	assertNull(cacheItem);
    	
    	cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
    	cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
    }
    
 // TODO Enable source message fallback @Test
//    public void testGetMsgsFailedMissingKey() { 
//    	// Offline mode only; neither target locale bundle nor default locale bundle exists
//    	// Source message does not exist in SourceOpt so return the key
//    	String key = "does.not.exist";
//    	VIPCfg cfg = VIPCfg.getInstance();
//        try {
//        	Source srcOpt = new ResourceBundleSrc("messages", LocaleUtility.defaultLocale);
//            cfg.initialize("vipconfig-offline", srcOpt);
//        } catch (VIPClientInitException e) {
//            logger.error(e.getMessage());
//        }
//    	
//        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
//        TranslationCacheManager.cleanCache(c);
//        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
//        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
//
//        dto.setProductID(VIPCfg.getInstance().getProductName());
//        dto.setVersion(VIPCfg.getInstance().getVersion());
//        
//        // Bundle does not exist locally
//        Locale newLocale = new Locale("es");
//        dto.setLocale(newLocale.toLanguageTag());
//        
//    	CacheService cs = new CacheService(dto);
//    	
//    	
//    	String message = translation.getMessage(newLocale, component, key, args);
//    	// Returns key
//    	assertEquals(key, message);
//    	
//    	// Nothing is stored in cache
//    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
//    	assertNull(cacheItem);
//    }
    
    @Test
    public void testGetMsgsOfflineModeAfterOnlineError() {
    	String offlineResourcesBaseUrlOrig = cfg.getOfflineResourcesBaseUrl();
    	cfg.setOfflineResourcesBaseUrl("offlineBundles/");
    	List<DataSourceEnum> msgOriginsQueueOrig = cfg.getMsgOriginsQueue();
    	cfg.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(
    			Arrays.asList(DataSourceEnum.Bundle, DataSourceEnum.VIP)));
    	
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        
    	CacheService cs = new CacheService(dto);
    	
    	String message = translation.getMessage(locale, component, key, args);
    	assertEquals(FormatUtils.format(messageFil, locale, args), message);
    	
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
    	assertEquals(messageFil, cacheItem.getCachedData().get(key));
    	
    	cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
    	cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
    }
    
    @Test
    public void testGetMsgsOnlineModePriority() {
    	String offlineResourcesBaseUrlOrig = cfg.getOfflineResourcesBaseUrl();
    	cfg.setOfflineResourcesBaseUrl("offlineBundles/");
    	List<DataSourceEnum> msgOriginsQueueOrig = cfg.getMsgOriginsQueue();
    	cfg.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(
    			Arrays.asList(DataSourceEnum.Bundle, DataSourceEnum.VIP)));
    	
    	cfg.initializeVIPService();
    	
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(cfg);
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        
        // This bundle is only available online (remote).
        Locale newLocale = new Locale("fr");
        dto.setLocale(newLocale.toLanguageTag());
        
    	CacheService cs = new CacheService(dto);
    	
    	String message = translation.getMessage(newLocale, component, key, args);
    	assertEquals(FormatUtils.format(messageFr, newLocale, args), message);
    	
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
    	assertEquals(messageFr, cacheItem.getCachedData().get(key));
    	
    	cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
    	cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
    }
    
    @Test
    public void testGetMsgsBothOfflineAndOnlineFailed() {
    	String offlineResourcesBaseUrlOrig = cfg.getOfflineResourcesBaseUrl();
    	cfg.setOfflineResourcesBaseUrl("offlineBundles/");
    	List<DataSourceEnum> msgOriginsQueueOrig = cfg.getMsgOriginsQueue();
    	cfg.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(
    			Arrays.asList(DataSourceEnum.Bundle, DataSourceEnum.VIP)));
    	
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

    	translation.getMessage(newLocale, component, key, args);
    	
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();
    	assertNotNull(cacheItem);
    	
    	MessagesDTO defaultLocaleDTO = new MessagesDTO(dto.getComponent(), 
				dto.getKey(), dto.getSource(), LocaleUtility.defaultLocale.toLanguageTag(), null);
    	CacheService csDefault = new CacheService(defaultLocaleDTO);
    	MessageCacheItem cacheItemDefaultLocale = csDefault.getCacheOfComponent();
    	
    	// Cache of default locale and cache of Locale.ITALIAN refer to the same object
    	assertEquals(cacheItemDefaultLocale, cacheItem);
    	
    	cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
    	cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
    }
}
