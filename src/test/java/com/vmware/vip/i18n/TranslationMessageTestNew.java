/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.VIPCfgFactory;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.ProductService;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TranslationMessageTestNew extends BaseTestClassNew {
    TranslationMessage translation;
    MessagesDTO        dto;

    // all posted string are in these components
    String             component1 = "sourcecollection1";
    String             component2 = "sourcecollection2";

    VIPCfg vipCfg = null;
    @Before
    public void init() {
        try {
            vipCfg = VIPCfgFactory.initialize("vipconfig", true);
            vipCfg.createTranslationCache(MessageCache.class);
            vipCfg.createFormattingCache(FormattingCache.class);
        } catch (VIPClientInitException e) {
            e.printStackTrace();
        }
        if (TranslationCacheManager.getInstance() != null)
            TranslationCacheManager.getInstance().clearCache();

        translation = new TranslationMessage();
        translation.setCfg(vipCfg);
        dto = new MessagesDTO();
    }

    @Test
    public void testGetMessageWithBundle_() {
        // this.init(); //don't need to call again, this has been called in Before.
        vipCfg.setPseudo(false);
        String component = "JAVA";
        String key = "LeadTest";
        Object[] args = { "a" };

        this.init();
        vipCfg.setPseudo(false);
        Locale locale2 = new Locale("de");
        
        String message2 = translation.getMessage(locale2, component, key, args);
       
        Assert.assertEquals("[a] Testwarnung", message2);

        this.init();
        vipCfg.setPseudo(false);
        Locale locale3 = Locale.forLanguageTag("zh-Hans");
        String message3 = translation.getMessage(locale3, component, key, args);
        
        Assert.assertEquals("[a] 测试警示", message3);

        Locale locale4 = Locale.forLanguageTag("zh-Hant");
        String message4 = translation.getMessage(locale4, component, key, args);
        Assert.assertEquals("[a] 測試警示", message4);

        Locale locale5 = Locale.forLanguageTag("zh-Hans-CN");
        String message5 = translation.getMessage(locale5, component, key, args);
       
        Assert.assertEquals("[a] 测试警示", message5);

        Locale locale6 = Locale.forLanguageTag("zh-Hant-TW");
        String message6 = translation.getMessage(locale6, component, key, args);
        Assert.assertEquals("[a] 測試警示", message6);

    }

    @Test
    public void testGetComponentMessagesLocaleNotSupported() {
    	String component = "JAVA";
    	String message_en_US = "User name";
    	String key = "global_text_username";
    	// When requested locale is not supported, the default locale messages will be returned.
        Map<String, String> localeNotSupported = translation.getMessages(Locale.forLanguageTag("fil-PH"), component);
        Assert.assertEquals(message_en_US, localeNotSupported.get(key));
    }
    @Test
    public void testGetComponentMessages() {
        vipCfg.setPseudo(false);

        String component = "JAVA";
        String key = "global_text_username";
        String message_en_US = "User name";
        String message_de = "Benutzername";
        String message_zh_CN = "用户名";
        String message_zh_TW = "使用者名稱";
        Map<String, String> retMap1 = translation.getMessages(new Locale("en", "US"), component);
        Assert.assertEquals(message_en_US, retMap1.get(key));

        Map<String, String> retMap2 = translation.getMessages(new Locale("de", ""), component);
        Assert.assertEquals(message_de, retMap2.get(key));

        Map<String, String> retMap3 = translation.getMessages(Locale.forLanguageTag("zh-Hans"), component);
        logger.debug(retMap3.get(key));
        logger.debug(message_zh_CN);
        Assert.assertEquals(message_zh_CN, retMap3.get(key));

        Map<String, String> retMap4 = translation.getMessages(Locale.forLanguageTag("zh-Hant"), component);
        Assert.assertEquals(message_zh_TW, retMap4.get(key));

        Map<String, String> retMap5 = translation.getMessages(Locale.forLanguageTag("zh-Hans-CN"), component);
        Assert.assertEquals(message_zh_CN, retMap5.get(key));

        Map<String, String> retMap6 = translation.getMessages(Locale.forLanguageTag("zh-Hant-TW"), component);
        Assert.assertEquals(message_zh_TW, retMap6.get(key));
    }

    @Test
    public void testGetAllComponentTranslation() {
        List<Map> list = new ProductService(dto).getAllComponentTranslation();
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void testGetAllComponentTranslationMixedMode() {
        String offlineResourcesBaseUrlOrig = vipCfg.getOfflineResourcesBaseUrl();
        vipCfg.setOfflineResourcesBaseUrl("offlineBundles/");
        List<DataSourceEnum> msgOriginsQueueOrig = vipCfg.getMsgOriginsQueue();
        vipCfg.setMsgOriginsQueue(new LinkedList<>(Arrays.asList(DataSourceEnum.VIP, DataSourceEnum.Bundle)));
        String vipServerOrig = vipCfg.getVipServer();
        vipCfg.setVipServer("http://1.1.1.1:80");

        List<Map> list = new ProductService(dto).getAllComponentTranslation();
        Assert.assertTrue(list.size() > 0);

        vipCfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
        vipCfg.setMsgOriginsQueue(msgOriginsQueueOrig);
        vipCfg.setVipServer(vipServerOrig);
    }

    @Test
    public void testGetPseudoTranslation_Collected_() {
        Locale zhLocale = new Locale("zh", "Hans");
        String comp = "JAVA";
        String key = "table.host";
        String expected = "#@Host#@";

        VIPCfg vc = VIPCfgFactory.globalCfg;
        boolean existing_pseudo = vc.isPseudo();
        vc.setPseudo(true);

        String pseudoTrans1 = translation.getMessage(zhLocale, comp, key, "");

        vc.setPseudo(existing_pseudo);

        logger.debug("pseudoTrans1: " + pseudoTrans1);
        Assert.assertEquals(expected, pseudoTrans1);
    }

    @Test
    public void testGetSourcesOfMCompAndMLoc() {
        clearTranslationCache();

        String component1 = "JAVA";
        String component2 = "USER";

        Locale locale2 = Locale.forLanguageTag("fr");
        Locale locale3 = Locale.forLanguageTag("zh-Hans");
        Locale locale4 = Locale.forLanguageTag("zh-CN");

        // Get 1 component and 1 locale
        Map<Locale, Map<String, Map<String, String>>> result = translation.getStrings(Sets.newHashSet(locale2),
                Sets.newHashSet(component1));
        Assert.assertSame(locale2, result.keySet().iterator().next());
        Assert.assertEquals(1, result.size()); // 1 locale
        Assert.assertEquals(1, result.get(locale2).size()); // 1 component
        Assert.assertEquals("H\u00F4te", result.get(locale2).get(component1).get("table.host"));

        // Get 2 components and 1 locale
        result = translation.getStrings(Sets.newHashSet(locale2), Sets.newHashSet(component1, component2));
        Assert.assertEquals(2, result.get(locale2).size()); // 2 components
        Assert.assertEquals(2, result.get(locale2).get(component2).size()); // 2 messages
        Assert.assertEquals("valeur-1", result.get(locale2).get(component2).get("user-1"));

        // Get with a null locale
        result = translation.getStrings((Set<Locale>) null, Sets.newHashSet(component1));
        Assert.assertEquals(0, result.size());

        // Get with a null component
        result = translation.getStrings(Sets.newHashSet(locale2), (Set<String>) null);
        Assert.assertEquals(0, result.size());

        // Get with an empty component list
        result = translation.getStrings(Sets.newHashSet(locale2), new HashSet<String>());
        Assert.assertEquals(0, result.size());

        // Get 2 components and 2 locales
        clearTranslationCache();
        Map<Locale, Map<String, Map<String, String>>> result2 = translation.getStrings(
                Stream.of(locale2, locale3).collect(Collectors.toSet()), Sets.newHashSet(component1, component2));
        Assert.assertEquals(2, result2.size()); // 2 locales
        Assert.assertEquals(2, result2.get(locale3).size()); // 2 components
        Assert.assertEquals(2, result2.get(locale3).get(component2).size()); // 2 messages
        Assert.assertEquals("valeur-1", result2.get(locale2).get(component2).get("user-1"));

        // Get 2 components and 2 locales. One is zh-CN to test locale fallback.
        // zh-CN falls back to zh-Hans.
        clearTranslationCache();
        result2 = translation.getStrings(
                Stream.of(locale2, locale4).collect(Collectors.toSet()), Sets.newHashSet(component1, component2));
        Assert.assertEquals(2, result2.size()); // 2 locales
        Assert.assertEquals(2, result2.get(locale4).size()); // 2 components
        Assert.assertEquals(2, result2.get(locale4).get(component2).size()); // 2 messages
        Assert.assertEquals("值-1", result2.get(locale4).get(component2).get("user-1"));

        // more cases to test cache
        // more cases to test the message sending to server
    }
}
