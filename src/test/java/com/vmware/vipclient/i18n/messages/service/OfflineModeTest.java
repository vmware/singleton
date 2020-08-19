/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.*;

import com.vmware.vip.i18n.BaseTestClass;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class OfflineModeTest extends BaseTestClass {

	String component = "JAVA";
    String key = "LeadTest";
    String source = "[{0}] Test alert";
    Locale locale = Locale.forLanguageTag("fil-PH");
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
    public void testGetAllComponentTranslationOfflineMode() {
        String offlineResourcesBaseUrlOrig = cfg.getOfflineResourcesBaseUrl();
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        List<DataSourceEnum> msgOriginsQueueOrig = cfg.getMsgOriginsQueue();
        cfg.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(Arrays.asList(DataSourceEnum.Bundle)));
        
        dto.setProductID(cfg.getProductName());
        dto.setVersion(cfg.getVersion());

        List<Map> list = new ProductService(dto).getAllComponentTranslation();
        Assert.assertTrue(list.size() > 0);

        cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
        cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
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

    @Test
    public void testGetMsgsOfflineModeCacheInitialized() {
        String offlineResourcesBaseUrlOrig = cfg.getOfflineResourcesBaseUrl();
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        List<DataSourceEnum> msgOriginsQueueOrig = cfg.getMsgOriginsQueue();
        cfg.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(Arrays.asList(DataSourceEnum.Bundle)));
        boolean initializeCacheOrig = cfg.isInitializeCache();
        cfg.setInitializeCache(true);

        Cache c = cfg.createTranslationCache(MessageCache.class);
        assertTrue(c.size() > 0);

        cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
        cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
        cfg.setInitializeCache(initializeCacheOrig);
    }
    
    @Test
    public void testGetMsgsFailedKeyNotFound() { 
    	// Offline mode only; message key does not exist
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
        Locale newLocale = new Locale("en");
        dto.setLocale(newLocale.toLanguageTag());
    	
        VIPJavaClientException e = assertThrows(VIPJavaClientException.class, () -> {
        	translation.getMessage(newLocale, component, key, args);
        });
        
    	// Throw an exception because message key does not exist anywhere
    	assertEquals(FormatUtils.format(ConstantsMsg.GET_MESSAGE_FAILED, key, component, newLocale), e.getMessage());
    	
    	cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
    	cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
    }
    
    @Test
    public void testGetMsgsFailedUseDefault() {
    	// Offline mode only; target locale bundle does not exist
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
    	// Returns the message in the default locale
    	assertEquals(FormatUtils.format(source, args), message);
    	
    	// Cache for "es" locale is now pointing to the cache for the default locale
    	MessageCacheItem cacheItem = cs.getCacheOfComponent();   	
    	assertEquals(source, cacheItem.getCachedData().get(key));
    	
    	cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
    	cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
    }
    
    @Test
    public void testGetMsgsFailedNewSource() { 
    	// Offline mode only; Message is new and hasn't been collected
    	String key = "new.key";
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
    	
    	String message = translation.getMessage(locale, component, key, args);
    	// Returns the source message because message hasn't been collected
    	assertEquals("Not yet collected", message);
    	
    	cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
    	cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
    }
    
    @Test
    public void testGetMsgsFailedUpdatedSource() { 
    	// Offline mode only; Message has been updated but hasn't been collected
    	String offlineResourcesBaseUrlOrig = cfg.getOfflineResourcesBaseUrl();
    	cfg.setOfflineResourcesBaseUrl("offlineBundles2/");
    	List<DataSourceEnum> msgOriginsQueueOrig = cfg.getMsgOriginsQueue();
    	cfg.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(Arrays.asList(DataSourceEnum.Bundle)));
    	
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);

        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
    	
    	String message = translation.getMessage(locale, component, key, args);
    	// Returns the source message because message hasn't been collected
    	assertEquals(FormatUtils.format(source, args).concat(" - updated"), message);
    	
    	cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
    	cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
    }
    
    @Test
    public void testGetMsgsOfflineModeAfterOnlineError() {
    	String offlineResourcesBaseUrlOrig = cfg.getOfflineResourcesBaseUrl();
    	cfg.setOfflineResourcesBaseUrl("offlineBundles/");
    	List<DataSourceEnum> msgOriginsQueueOrig = cfg.getMsgOriginsQueue();
    	cfg.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(
    			Arrays.asList(DataSourceEnum.VIP, DataSourceEnum.Bundle)));
    	cfg.initializeVIPService();
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
    			Arrays.asList(DataSourceEnum.VIP, DataSourceEnum.Bundle)));
    	
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
    			Arrays.asList(DataSourceEnum.VIP, DataSourceEnum.Bundle)));
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
				dto.getKey(), dto.getSource(), LocaleUtility.getDefaultLocale().toLanguageTag(), null);
    	CacheService csDefault = new CacheService(defaultLocaleDTO);
    	MessageCacheItem cacheItemDefaultLocale = csDefault.getCacheOfComponent();
    	
    	// Cache of default locale and cache of Locale.ITALIAN refer to the same object
    	assertEquals(cacheItemDefaultLocale, cacheItem);
    	
    	cfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
    	cfg.setMsgOriginsQueue(msgOriginsQueueOrig);
    }
}
