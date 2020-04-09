/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.vmware.vip.i18n.BaseTestClass;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.CacheItem;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;

public class CacheServiceTest extends BaseTestClass {

	String component = "JAVA";
    String key = "LeadTest";
    String source = "[{0}] Test alert";
    Locale locale = new Locale("de");
    String comment = "comment";
    Object[] args = { "a" };

    MessagesDTO dto = new MessagesDTO();
    
    @Before
    public void init() {
        dto.setComponent(component);
        dto.setKey(key);
        dto.setSource(source);
        dto.setLocale(locale.toLanguageTag());
    }
    
    @Test
    public void testExpireUsingCacheControlMaxAge() {
    	VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
    	gc.initializeVIPService();
    	
    	// Explicitly set this config to the default which is -1, as if the config property was not set.
        // This is done so that the cache-control max age form the server response is used instead.
        VIPCfg.getInstance().setCacheExpiredTime(VIPCfg.CACHE_EXP_TIME_NOT_SET);
        
        Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        CacheService cs = new CacheService(dto);
        
        // Cache is considered as "expired" for the very first HTTP call
        assertTrue(cs.isExpired());
        
        // This triggers the first http call
    	translation.getString(locale, component, key, source, comment, args);

    	CacheItem cacheItem = cs.getCacheOfComponent();
        Long responseTime = (Long) cacheItem.getTimestamp();
        
        // TODO Store response code in cache if we want to test this
        //int responseCode = cacheItem.getResponseCode();
        //assertEquals(new Integer(200), responseCode);
        
        // Set max age to 0 to explicitly expire the cache for testing purposes.
        cacheItem.setMaxAgeMillis(0l);
        
        // Second request for the same message.
        // This should trigger another HTTP request because cache had been explicitly expired above.
        // The http request includes If-None-Match header that is set to the previously received eTag value.
        translation.getString(locale, component, key, source, comment, args);
        
        // Because nothing has changed on the server and If-None-Match request header was properly set, 
        // the server responds with a 304 Not Modified.
        // However, cache update happens in a separate thread, and the previously cached item 
        // was immediately returned in the main thread for optimal performance.
        // This means no changes yet in the cached response code nor the response time.
        Long responseTime2 = cacheItem.getTimestamp();
        assertTrue(responseTime2.equals(responseTime)); 
        assertTrue(cacheItem.getMaxAgeMillis() == 0l);
        
        // TODO Store response code in cache if we want to test this
        //responseCode = cacheItem.getResponseCode();
        //assertEquals(new Integer(200), responseCode);
        
        // Give time for the separate thread to finish.
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Third request for the same message.
        // This should fetch messages and properties from cache 
        translation.getString(locale, component, key, source, comment, args);
        
        // TODO Store response code in cache if we want to test 
        //responseCode = cacheItem.getResponseCode();        
        //assertEquals(new Integer(304), responseCode);
        
        // The cached response time had been updated by the separate thread 
        // to the timestamp of the second response.  
        // This, in effect, extends the cache expiration.
        Long responseTime3 = cacheItem.getTimestamp();
        assertTrue(responseTime3 > responseTime); 
        assertTrue(cacheItem.getMaxAgeMillis() > 0l);
        
    }
    
    @Test
    @Deprecated
    public void testExpireUsingCacheExpiredTimeConfig() { 
    	VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
    	gc.initializeVIPService();
    	
    	// If cacheExpiredTime config is set, it means  that the value of this config will be used 
    	// to indicate cache expiration. Cache control max age from http response will be ignored.
    	long cacheExpiredTime = VIPCfg.getInstance().getCacheExpiredTime();
    	assertNotEquals(cacheExpiredTime, VIPCfg.CACHE_EXP_TIME_NOT_SET);
    	
    	Cache c = VIPCfg.getInstance().createTranslationCache(MessageCache.class);
    	TranslationCacheManager.cleanCache(c);
        I18nFactory i18n = I18nFactory.getInstance(VIPCfg.getInstance());
        TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        
        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());
        CacheService cs = new CacheService(dto);
        
        // Cache is considered as "not expired" for the very first HTTP call
        assertFalse(cs.isExpired());
        
        // This triggers the first http call
    	translation.getString(locale, component, key, source, comment, args);
    	CacheItem cacheItem = cs.getCacheOfComponent();
    	Long responseTime = cacheItem.getTimestamp();
         
    	//Explicitly expire the cache
    	c.setExpiredTime(0l);
        TranslationCacheManager.getCache(VIPCfg.CACHE_L3);
        // Second request for the same message.
        // This should trigger another HTTP request because cache had been explicitly expired above.
        translation.getString(locale, component, key, source, comment, args);
        
        // Because nothing has changed on the server and If-None-Match request header was properly set, 
        // the server responds with a 304 Not Modified.
        Long responseTime2 = cacheItem.getTimestamp();
        assertTrue(responseTime2.equals(responseTime)); 
        
        // Second request for the same message.
        // This should fetch messages and properties from cache 
        translation.getString(locale, component, key, source, comment, args);
        
        // TODO Store response code in cache if we want to test this
        //responseCode = cacheItem.getResponseCode();  
        //assertEquals(new Integer(200), responseCode);
        
        // Timestamp remains the same because no http request was made.
        Long responseTime3 = cacheItem.getTimestamp();
        assertTrue(responseTime3.equals(responseTime2)); 
    }  
}
