/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.*;

import com.vmware.vip.i18n.BaseTestClassNew;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.VIPCfgFactory;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OfflineModeTestNew extends BaseTestClassNew {

    String component = "JAVA";
    String key = "LeadTest";
    String source = "[{0}] Test alert";
    Locale locale = Locale.forLanguageTag("fil-PH");
    String messageFil = "[{0}] Alerto sa pagsusuri";
    String messageFr ="[{0}] Alerte de test";
    String messageEn ="[{0}] Test alert";
    Object[] args = { "a" };

    MessagesDTO dto = new MessagesDTO();
    VIPCfg cfg;
    TranslationMessage translation = null;
    @Before
    public void init() {
        dto.setComponent(component);
        dto.setKey(key);
        dto.setSource(source);
        dto.setLocale(locale.toLanguageTag());
        try {
            cfg = VIPCfgFactory.initialize("vipconfig", true);
            translation = new TranslationMessage();
            translation.setCfg(cfg);
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
;    }

    @Test
    public void testGetAllComponentTranslationOfflineMode() {
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        cfg.setMsgOriginsQueue(new LinkedList<>(Arrays.asList(DataSourceEnum.Bundle)));

        dto.setProductID(cfg.getProductName());
        dto.setVersion(cfg.getVersion());

        List<Map> list = new ProductService(dto).getAllComponentTranslation();
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void testGetMsgsOfflineMode() {
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        cfg.setMsgOriginsQueue(new LinkedList<>(Arrays.asList(DataSourceEnum.Bundle)));

        Cache c = cfg.createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);

        dto.setProductID(VIPCfgFactory.globalCfg.getProductName());
        dto.setVersion(VIPCfgFactory.globalCfg.getVersion());

        // fil-PH cacheItem does not exist
        CacheService cs = new CacheService(dto);
        assertNull(cs.getCacheOfComponent());

        // fil-PH matches fil cacheItem
        String messageFilPh = translation.getMessage(locale, component, key, args);
        assertEquals(FormatUtils.format(messageFil, locale, args), messageFilPh);
    }

    @Test
    public void testGetMsgsOfflineModeCacheInitialized() {
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        cfg.setMsgOriginsQueue(new LinkedList<>(Arrays.asList(DataSourceEnum.Bundle)));
        boolean initializeCacheOrig = cfg.isInitializeCache();
        cfg.setInitializeCache(true);

        Cache c = cfg.createTranslationCache(MessageCache.class);
        assertTrue(c.size() > 0);

        cfg.setInitializeCache(initializeCacheOrig);
    }

    @Test
    public void testGetMsgsFailedKeyNotFound() {
        // Offline mode only; message key does not exist
        String key = "does.not.exist";
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        cfg.setMsgOriginsQueue(new LinkedList<>(Arrays.asList(DataSourceEnum.Bundle)));

        Cache c = VIPCfgFactory.globalCfg.createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);

        dto.setProductID(VIPCfgFactory.globalCfg.getProductName());
        dto.setVersion(VIPCfgFactory.globalCfg.getVersion());

        // Bundle does not exist locally
        Locale newLocale = new Locale("en");
        dto.setLocale(newLocale.toLanguageTag());

        VIPJavaClientException e = assertThrows(VIPJavaClientException.class, () -> {
            translation.getMessage(newLocale, component, key, args);
        });

        // Throw an exception because message key does not exist anywhere
        assertEquals(FormatUtils.format(ConstantsMsg.GET_MESSAGE_FAILED, key, component, newLocale), e.getMessage());
    }

    @Test
    public void testGetMsgsFailedUseDefault() {
        // Offline mode only; target locale bundle does not exist
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        cfg.setMsgOriginsQueue(new LinkedList<>(Arrays.asList(DataSourceEnum.Bundle)));

        Cache c = VIPCfgFactory.globalCfg.createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);

        dto.setProductID(VIPCfgFactory.globalCfg.getProductName());
        dto.setVersion(VIPCfgFactory.globalCfg.getVersion());

        // Bundle does not exist locally
        Locale newLocale = new Locale("es");
        dto.setLocale(newLocale.toLanguageTag());

        CacheService cs = new CacheService(dto);

        String message = translation.getMessage(newLocale, component, key, args);
        // Returns the message in the default locale
        assertEquals(FormatUtils.format(source, args), message);

        MessageCacheItem cacheItem = cs.getCacheOfComponent();
        assertNull(cacheItem);
        assertEquals(FormatUtils.format(messageEn, args), message);
    }

    @Test
    public void testGetMsgsFailedNewSource() {
        // Offline mode only; Message is new and hasn't been collected
        String key = "new.key";
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        cfg.setMsgOriginsQueue(new LinkedList<>(Arrays.asList(DataSourceEnum.Bundle)));

        Cache c = VIPCfgFactory.globalCfg.createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);

        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());

        String message = translation.getMessage(locale, component, key, args);
        // Returns the source message because message hasn't been collected
        assertEquals("Not yet collected", message);
    }

    @Test
    public void testGetMsgsFailedUpdatedSource() {
        // Offline mode only; Message has been updated but hasn't been collected
        cfg.setOfflineResourcesBaseUrl("offlineBundles2/");
        cfg.setMsgOriginsQueue(new LinkedList<>(Arrays.asList(DataSourceEnum.Bundle)));

        Cache c = VIPCfgFactory.globalCfg.createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);

        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());

        String message = translation.getMessage(locale, component, key, args);
        // Returns the source message because message hasn't been collected
        assertEquals(FormatUtils.format(source, args).concat(" - updated"), message);
    }

    @Test
    public void testGetMsgsOfflineModeAfterOnlineError() {
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        cfg.setMsgOriginsQueue(new LinkedList<>(
                Arrays.asList(DataSourceEnum.VIP, DataSourceEnum.Bundle)));
        Cache c = VIPCfgFactory.globalCfg.createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);

        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());

        String message = translation.getMessage(locale, component, key, args);
        assertEquals(FormatUtils.format(messageFil, locale, args), message);

        // fil-PH does not exist in cache
        assertEquals(FormatUtils.format(messageFil, args), message);
    }

    @Test
    public void testGetMsgsOnlineModePriority() {
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        cfg.setMsgOriginsQueue(new LinkedList<>(
                Arrays.asList(DataSourceEnum.VIP, DataSourceEnum.Bundle)));

        Cache c = VIPCfgFactory.globalCfg.createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);

        dto.setProductID(VIPCfgFactory.globalCfg.getProductName());
        dto.setVersion(VIPCfgFactory.globalCfg.getVersion());

        // This bundle is only available online (remote).
        Locale newLocale = new Locale("fr");
        dto.setLocale(newLocale.toLanguageTag());

        CacheService cs = new CacheService(dto);

        String message = translation.getMessage(newLocale, component, key, args);
        assertEquals(FormatUtils.format(messageFr, newLocale, args), message);

        MessageCacheItem cacheItem = cs.getCacheOfComponent();
        assertEquals(messageFr, cacheItem.getCachedData().get(key));
    }

    @Test
    public void testGetSupportedLocalesOfflineBundles() throws ParseException {
        //Enable offline mode
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        cfg.setMsgOriginsQueue(new LinkedList<>(Arrays.asList(DataSourceEnum.Bundle)));

        MessagesDTO msgsDTO = new MessagesDTO(component, "fil-PH", cfg.getProductName(), cfg.getVersion());
        CacheService cs = new CacheService(msgsDTO);

        Cache c = cfg.createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);
        cfg.createTranslationCache(MessageCache.class);

        BaseDTO dto = new BaseDTO();
        dto.setVersion(cfg.getVersion());
        dto.setProductID(cfg.getProductName());
        ProductService ps = new ProductService(dto);
        ps.getAllComponentTranslation();

        String messageFilPh = translation.getMessage(locale, component, key, args);

        // fil-PH cacheItem does not exist
        assertNull(cs.getCacheOfComponent());

        // fil-PH matches fil cacheItem
        assertEquals(FormatUtils.format(messageFil, locale, args), messageFilPh);
    }

    @Test
    public void testGetMsgsBothOfflineAndOnlineFailed() {
        cfg.setOfflineResourcesBaseUrl("offlineBundles/");
        cfg.setMsgOriginsQueue(new LinkedList<>(
                Arrays.asList(DataSourceEnum.VIP, DataSourceEnum.Bundle)));

        Cache c = VIPCfgFactory.globalCfg.createTranslationCache(MessageCache.class);
        TranslationCacheManager.cleanCache(c);

        dto.setProductID(VIPCfg.getInstance().getProductName());
        dto.setVersion(VIPCfg.getInstance().getVersion());

        // This bundle is available neither online (remote) nor offline (local).
        Locale newLocale = Locale.ITALIAN;
        dto.setLocale(newLocale.toLanguageTag());

        CacheService cs = new CacheService(dto);

        String message = translation.getMessage(newLocale, component, key, args);

        MessageCacheItem cacheItem = cs.getCacheOfComponent();
        assertNull(cacheItem);

        MessagesDTO defaultLocaleDTO = new MessagesDTO(dto.getComponent(),
                dto.getKey(), dto.getSource(), LocaleUtility.getDefaultLocale().toLanguageTag(), null);
        CacheService csDefault = new CacheService(defaultLocaleDTO);
        assertEquals(message, FormatUtils.format(csDefault.getCacheOfComponent().getCachedData().get(key), args));
    }
}
