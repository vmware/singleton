/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ibm.icu.text.DateFormat;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.ProductService;

public class TranslationMessageTest extends BaseTestClass {
    TranslationMessage translation;
    MessagesDTO        dto;

    // all posted string are in these components
    String             component1 = "sourcecollection1";
    String             component2 = "sourcecollection2";

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
        gc.createTranslationCache(MessageCache.class);
        gc.createFormattingCache(FormattingCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
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
    @Deprecated
    public void testGetMessageWithBundle() {
        // this.init(); //don't need to call again, this has been called in Before.

        vipCfg.setPseudo(false);
        String component = "JAVA", bundle = "messages";
        Locale locale1 = new Locale("en", "US");
        String key = "LeadTest";
        Object[] args = { "a" };
       /* String message1 = translation.getString2(component, bundle, locale1, key,
                args);
        Assert.assertEquals("[a] Test alert", message1);
*/
        
        Map<String, Object> msgargs = new HashMap<>();
        msgargs.put("a", 1);
        msgargs.put("b", 5);
        msgargs.put("c", 10);
        String message_namedArgs = translation.getString(locale1, component, "NamedArgs",
                "{a} - {b} of {c} customers", "", msgargs);
        Assert.assertEquals("1 - 5 of 10 customers", message_namedArgs);

        init();
        vipCfg.setPseudo(false);
        Locale locale2 = new Locale("de");
        String message2 = translation.getString2(component, bundle, locale2, key,
                args);
        String message22 = translation.getString2(component, bundle, locale2, key,
                args);
        String message23 = translation.getString2(component, bundle, locale2, key,
                args);
        String message24 = translation.getString2(component, bundle, locale2, key,
                args);
        Assert.assertEquals("[a] Testwarnung", message2);

        this.init();
        vipCfg.setPseudo(false);
        Locale locale3 = Locale.forLanguageTag("zh-Hans");
        String message3 = translation.getString2(component, bundle, locale3, key,
                args);
        Assert.assertEquals("[a] 测试警示", message3);

        Locale locale4 = Locale.forLanguageTag("zh-Hant");
        String message4 = translation.getString2(component, bundle, locale4, key,
                args);
        Assert.assertEquals("[a] 測試警示", message4);

        Locale locale5 = Locale.forLanguageTag("zh-Hans-CN");
        String message5 = translation.getString2(component, bundle, locale5, key,
                args);
        Assert.assertEquals("[a] 测试警示", message5);

        Locale locale6 = Locale.forLanguageTag("zh-Hant-TW");
        String message6 = translation.getString2(component, bundle, locale6, key,
                args);
        Assert.assertEquals("[a] 測試警示", message6);

    }

    @Test
    public void testGetPatternMessageWithBundle() {
        this.init();
        String component = "JAVA", bundle = "messages";
        Locale locale1 = new Locale("en", "US");

        //test message with plural
        String pluralKey = "sample.plural.key1";

        Object[] en_pluralArgs1 = { 0, "MyDisk" };
        String pluralMessage1 = translation.getString2(component, bundle, locale1, pluralKey,
                en_pluralArgs1);
        Assert.assertEquals("There are 0 files on disk \"MyDisk\".", pluralMessage1);

        Object[] en_pluralArgs2 = { 1, "MyDisk" };
        String pluralMessage2 = translation.getString2(component, bundle, locale1, pluralKey,
                en_pluralArgs2);
        Assert.assertEquals("There is one file on disk \"MyDisk\".", pluralMessage2);

        Object[] en_pluralArgs3 = { 345678, "MyDisk" };
        String pluralMessage3 = translation.getString2(component, bundle, locale1, pluralKey,
                en_pluralArgs3);
        Assert.assertEquals("There are 345,678 files on disk \"MyDisk\".", pluralMessage3);

        Locale locale7 = new Locale("zh", "CN");
        Object[] zh_pluralArgs1 = { 0, "我的硬盘" };
        String pluralMessage4 = translation.getString2(component, bundle, locale7, pluralKey,
                zh_pluralArgs1);
        Assert.assertEquals("\"我的硬盘\"上有0个文件。", pluralMessage4);

        Object[] zh_pluralArgs2 = { 1, "我的硬盘" };
        String pluralMessage5 = translation.getString2(component, bundle, locale7, pluralKey,
                zh_pluralArgs2);
        Assert.assertEquals("\"我的硬盘\"上有1个文件。", pluralMessage5);

        Object[] zh_pluralArgs3 = { 345678, "我的硬盘" };
        String pluralMessage6 = translation.getString2(component, bundle, locale7, pluralKey,
                zh_pluralArgs3);
        Assert.assertEquals("\"我的硬盘\"上有345,678个文件。", pluralMessage6);

        //test message with simple arg
        final long timestamp = 1511156364801l;
        Date date = new Date(timestamp);
        Object[] arguments = {
                7,
                new Date(timestamp),
                "a disturbance in the Force"
        };
        String expectedMsg = "At "+ DateFormat.getTimeInstance(DateFormat.SHORT, Locale.ENGLISH).format(date) +
                " on " + DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH).format(date) +
                ", there was a disturbance in the Force on planet 7.";
        String includeFormatMessage = translation.getMessage(bundle, locale1, component, "sample.includeFormat.message",
                arguments);
        System.out.println("includeFormatMessage result: " + includeFormatMessage);
        Assert.assertEquals(expectedMsg, includeFormatMessage);
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
    @Deprecated
    public void testGetComponentMessages_() {
        vipCfg.setPseudo(false);

        String component = "JAVA";
        String key = "global_text_username";
        String message_en_US = "User name";
        String message_de = "Benutzername";
        String message_zh_CN = "用户名";
        String message_zh_TW = "使用者名稱";
        Map<String, String> retMap1 = translation.getStrings(new Locale("en", "US"), component);
        Assert.assertEquals(message_en_US, retMap1.get(key));

        Map<String, String> retMap2 = translation.getStrings(new Locale("de", ""), component);
        Assert.assertEquals(message_de, retMap2.get(key));

        Map<String, String> retMap3 = translation.getStrings(Locale.forLanguageTag("zh-Hans"), component);
        logger.debug(retMap3.get(key));
        logger.debug(message_zh_CN);
        Assert.assertEquals(message_zh_CN, retMap3.get(key));

        Map<String, String> retMap4 = translation.getStrings(Locale.forLanguageTag("zh-Hant"), component);
        Assert.assertEquals(message_zh_TW, retMap4.get(key));

        Map<String, String> retMap5 = translation.getStrings(Locale.forLanguageTag("zh-Hans-CN"), component);
        Assert.assertEquals(message_zh_CN, retMap5.get(key));

        Map<String, String> retMap6 = translation.getStrings(Locale.forLanguageTag("zh-Hant-TW"), component);
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
        vipCfg.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(Arrays.asList(DataSourceEnum.VIP, DataSourceEnum.Bundle)));
        String vipServerOrig = vipCfg.getVipServer();
        vipCfg.setVipServer("http://1.1.1.1:80");

        List<Map> list = new ProductService(dto).getAllComponentTranslation();
        Assert.assertTrue(list.size() > 0);

        vipCfg.setOfflineResourcesBaseUrl(offlineResourcesBaseUrlOrig);
        vipCfg.setMsgOriginsQueue(msgOriginsQueueOrig);
        vipCfg.setVipServer(vipServerOrig);
    }

    @Test
    @Deprecated
    public void testPostSourceSet() {
        Locale locale = new Locale("zh", "CN");
        List<JSONObject> sources = new ArrayList<>();
        JSONObject jo1 = new JSONObject();
        jo1.put("key", "key1");
        jo1.put("source", "source1");
        jo1.put("commentForSource", "It's a comment1");
        sources.add(jo1);
        JSONObject jo2 = new JSONObject();
        jo2.put("key", "key2");
        jo2.put("source", "source2");
        jo2.put("commentForSource", "It's a comment2");
        sources.add(jo2);
        JSONObject jo3 = new JSONObject();
        jo3.put("key", "global_text_description1");
        jo3.put("source", "Description1");
        jo3.put("commentForSource", "It's a comment3");
        sources.add(jo3);
        Assert.assertTrue(translation.postStrings(locale, component1, sources));
        List<JSONObject> sources2 = new ArrayList<>();
        logger.debug(sources2.toString());
        Assert.assertFalse(translation.postStrings(locale, component1, sources2));
    }

    @Test
    @Deprecated
    public void testSendSource() {
        boolean f = translation.postString(new Locale("zh", "CN"), component1, "key", "Host", "It's a comment");
        Assert.assertTrue(f);
        boolean ff = translation.postString(new Locale("zh", "CN"), component2, "key1", "source1", "It's a comment1");
        Assert.assertTrue(ff);
    }

    @Test
    @Deprecated
    public void testGetTranslation_SingleQuota() {
        Locale zhLocale = new Locale("zh", "Hans");
        String comp = "Component1";
        String key = "single quotation marks-notcollected";
        String source = "Operator '{0}' 不支持 for property ' { 1} '";
        Object[] args = { "aaa", "bbb" };

        VIPCfg vc = VIPCfg.getInstance();
        boolean existing_collect = vc.isCollectSource();
        vc.setCollectSource(false);
        boolean existing_pseudo = vc.isPseudo();
        vc.setPseudo(true);

        String enTrans1 = translation.getString(zhLocale, comp, key, source, "", args);

        vc.setPseudo(existing_pseudo);
        vc.setCollectSource(existing_collect);

        String expected = "@@Operator 'aaa' 不支持 for property ' bbb '@@";
        logger.debug("enTrans1: " + enTrans1);
        Assert.assertArrayEquals(new Object[] { expected }, new Object[] { enTrans1 });
    }
    
    @Test
    @Deprecated
    public void testGetPseudoTranslation_NotCollected_1() {
        Locale zhLocale = new Locale("zh", "Hans");
        String comp = "Component1";
        String key = "getPseudoTranslation";
        String source = "Operator";
        String expected = "@@Operator@@";

        VIPCfg vc = VIPCfg.getInstance();
        boolean existing_collect = vc.isCollectSource();
        vc.setCollectSource(false);
        boolean existing_pseudo = vc.isPseudo();
        vc.setPseudo(true);

        String pseudoTrans1 = translation.getString(zhLocale, comp, key, source, "");
        vc.setPseudo(existing_pseudo);
        vc.setCollectSource(existing_collect);

        logger.debug("pseudoTrans1: " + pseudoTrans1);
        Assert.assertArrayEquals(new Object[] { expected }, new Object[] { pseudoTrans1 });
    }
    
    /*
     * bug: 2360553
     * cannot get pseudo translation when set 'source collection' and 'pseudo' to true for a new key
     * get translation with a new key which does not exist in VIP server.
     * should return source with pseudo tag @@
     */
    // locale: en-US. client won't contact server in any case.
    @Test
    public void testGetMessage__NotCollected_2() throws InterruptedException {
        VIPCfg gc = VIPCfg.getInstance();
        // gc.setProductName("Sample");
        // gc.setVersion("1.0.0");
        gc.setPseudo(true);
        gc.setCollectSource(true);
        gc.initializeVIPService();

        // new key and source
        String randomStr = getSaltString();
        Locale locale1 = new Locale("en", "US");
        String key1 = "MessagesNotFound" + randomStr;
        String source1 = "Some of the messages were not found" + randomStr;
        String message1 = translation.getString(locale1, component1, key1, source1, "");
        String expected1 = "@@" + source1 + "@@";
        Assert.assertEquals(expected1, message1);

        // server already collected
        String key2 = "LeadTest";
        String source2 = "It's a testing source";
        String message2 = translation.getString(locale1, component1, key2, source2, "");
        String expected2 = "@@" + source2 + "@@";
        Assert.assertEquals(expected2, message2);

        // source1 changed
        Thread.sleep(4 * 1000L);
        String source1_1 = source1 + "-new";
        String message1_1 = translation.getString(locale1, component1, key1, source1_1, "");
        String expected1_1 = "@@" + source1_1 + "@@";
        Assert.assertEquals(expected1_1, message1_1);
    }
    
    /*
     * bug: 2360553
     * cannot get pseudo translation when set 'source collection' and 'pseudo' to true for a new key
     * get translation with a new key which does not exist in VIP server.
     * should return source with pseudo tag @@
     */
    // locale: zh-CN
    @Test
    public void testGetMessage__NotCollected_3() throws InterruptedException {
        VIPCfg gc = VIPCfg.getInstance();
        gc.setPseudo(true);
        gc.setCollectSource(true);
        gc.initializeVIPService();

        String component = component1;

        // new key and source
        String randomStr = getSaltString();
        Locale locale1 = new Locale("zh", "CN");
        String key1 = "MessagesNotFound" + randomStr;
        String source1 = "Some of the messages were not found" + randomStr;
        String message1 = translation.getString(locale1, component, key1, source1, "");
        String expected1 = "@@" + source1 + "@@";
        Assert.assertEquals(expected1, message1);

        // server already collected
        String key2 = "LeadTest";
        String source2 = "[{0}] Test alert";
        String message2 = translation.getString(locale1, "JAVA", key2, "", "");
        String expected2 = "#@" + source2 + "#@";
        Assert.assertEquals(expected2, message2);

        // source1 changed
        Thread.sleep(4 * 1000L);
        String source1_1 = source1 + "-new";
        String message1_1 = translation.getString(locale1, component, key1, source1_1, "");
        String expected1_1 = "@@" + source1_1 + "@@";
        Assert.assertEquals(expected1_1, message1_1);
    }

    @Test
    public void testGetPseudoTranslation_Collected_() {
        Locale zhLocale = new Locale("zh", "Hans");
        String comp = "JAVA";
        String key = "table.host";
        String expected = "#@Host#@";

        VIPCfg vc = VIPCfg.getInstance();
        boolean existing_pseudo = vc.isPseudo();
        vc.setPseudo(true);

        String pseudoTrans1 = translation.getMessage(zhLocale, comp, key, "");

        vc.setPseudo(existing_pseudo);

        logger.debug("pseudoTrans1: " + pseudoTrans1);
        Assert.assertEquals(expected, pseudoTrans1);
    }
    
    @Test
    @Deprecated
    public void testGetPseudoTranslation_Collected() {
        Locale zhLocale = new Locale("zh", "Hans");
        String comp = "JAVA";
        String key = "table.host";
        String source = "Host";
        String expected = "#@Host#@";

        VIPCfg vc = VIPCfg.getInstance();
        boolean existing_pseudo = vc.isPseudo();
        vc.setPseudo(true);

        String pseudoTrans1 = translation.getString(zhLocale, comp, key, source, "");

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
        Locale locale5 = Locale.forLanguageTag("ar");

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

        // Get 2 components and 3 locales. One is not supported locale 'ar' to test locale fallback.
        // 'ar' falls back to 'en'.
        clearTranslationCache();
        result2 = translation.getStrings(
                Stream.of(locale2, locale4, locale5).collect(Collectors.toSet()), Sets.newHashSet(component1, component2));
        Assert.assertEquals(3, result2.size()); // 3 locales
        Assert.assertEquals(2, result2.get(locale5).size()); // 2 components
        Assert.assertEquals(2, result2.get(locale5).get(component2).size()); // 2 messages
        Assert.assertEquals("value-1", result2.get(locale5).get(component2).get("user-1"));

        // more cases to test cache
        // more cases to test the message sending to server
    }

    @Test
    public void testNamedArgs() {
        String component = "JAVA";
        String key = "NamedArgs";
        Locale locale_en = new Locale("en", "US");
        Locale locale_de = Locale.forLanguageTag("de");
        Map<String, Object> msgArgs = new HashMap<>();
        msgArgs.put("start", 1);
        msgArgs.put("to", 5);
        msgArgs.put("total", 10);

        String message_en = translation.getMessage(locale_en, component, key, msgArgs);
        Assert.assertEquals("1 - 5 of 10 customers", message_en);

        String message_de = translation.getMessage(locale_de, component, key, msgArgs);
        Assert.assertEquals("1 - 5 of 10 kunden", message_de);
    }

	@Test
	public void testPluralFallback() {
		String component = "JAVA";
		String plural_key = "sample.plural.key1";
		Locale locale_ar = Locale.forLanguageTag("ar");
		
		int argInt = 3;
		String argString = "MyDisk";
		Object[] msgArgs = new Object[] {argInt, argString}; //In ar, 3 belongs to 'few' type.

		String message_ar = translation.getMessage(locale_ar, component, plural_key, msgArgs);
		Assert.assertEquals("There are "+argInt+" files on disk \""+argString+"\".", message_ar);
	}
}
