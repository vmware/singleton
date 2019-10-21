/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.messages.service.ProductService;

public class TranslationMessageTest extends BaseTestClass {
	TranslationMessage translation;
	MessagesDTO dto;

	// all posted string are in these components
	String component1 = "sourcecollection1";
	String component2 = "sourcecollection2";

	@Before
	public void init() {
        VIPCfg gc = VIPCfg.getInstance();
        gc.initialize("vipconfig");
        gc.initializeVIPService();
        if(gc.getCacheManager() != null) gc.getCacheManager().clearCache();
        gc.createTranslationCache(MessageCache.class);
        gc.createFormattingCache(FormattingCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        translation = (TranslationMessage)i18n.getMessageInstance(TranslationMessage.class);
        dto = new MessagesDTO();
	}
	
    @Test
    public void testGetMessageWithBundle() {
//	    this.init(); //don't need to call again, this has been called in Before.
    	
    	vipCfg.setPseudo(false);
        String component = "JAVA", bundle = "messages";
        Locale locale1 = new Locale("en", "US");
        String key = "LeadTest";
        Object[] args = { "a" };
        String message1 = translation.getString2(component, bundle, locale1, key,
                args);
        Assert.assertEquals("[a] Test alert", message1);
        
		this.init();
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

    //@Test
    public void testGetPatternMessageWithBundle() {
        this.init();
        String component = "JAVA", bundle = "messages";
        Locale locale1 = new Locale("en", "US");

        String pluralKey = "sample.plural.key1";

        Object[] en_pluralArgs1 = {0, "MyDisk"};
        String pluralMessage1 = translation.getString2(component, bundle, locale1, pluralKey,
                en_pluralArgs1);
        Assert.assertEquals("There are 0 files on disk \"MyDisk\".", pluralMessage1);

        Object[] en_pluralArgs2 = {1, "MyDisk"};
        String pluralMessage2 = translation.getString2(component, bundle, locale1, pluralKey,
                en_pluralArgs2);
        Assert.assertEquals("There is one file on disk \"MyDisk\".", pluralMessage2);

        Object[] en_pluralArgs3 = {345678, "MyDisk"};
        String pluralMessage3 = translation.getString2(component, bundle, locale1, pluralKey,
                en_pluralArgs3);
        Assert.assertEquals("There are 345,678 files on disk \"MyDisk\".", pluralMessage3);

        Locale locale7 = new Locale("zh", "CN");
        Object[] zh_pluralArgs1 = {0, "我的硬盘"};
        String pluralMessage4 = translation.getString2(component, bundle, locale7, pluralKey,
                zh_pluralArgs1);
        Assert.assertEquals("\"我的硬盘\"上有0个文件。", pluralMessage4);

        Object[] zh_pluralArgs2 = {1, "我的硬盘"};
        String pluralMessage5 = translation.getString2(component, bundle, locale7, pluralKey,
                zh_pluralArgs2);
        Assert.assertEquals("\"我的硬盘\"上有1个文件。", pluralMessage5);

        Object[] zh_pluralArgs3 = {345678, "我的硬盘"};
        String pluralMessage6 = translation.getString2(component, bundle, locale7, pluralKey,
                zh_pluralArgs3);
        Assert.assertEquals("\"我的硬盘\"上有345,678个文件。", pluralMessage6);
    }


    @Test
    public void testGetComponentMessages() {
    	vipCfg.setPseudo(false);
    	
        String component = "JAVA";
        String key="global_text_username";
        String message_en_US="User name";
        String message_de="Benutzername";
        String message_zh_CN="用户名";
        String message_zh_TW="使用者名稱";
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

    //@Test
    //public void testGetComponentMessages_fr() {
    //    String component = "sunglow";
    //    Map<String, String> retMap2 = translation.getStrings(new Locale("fr", "CA"), component);
    //}

    @Test
    public void testGetAllComponentTranslation() {
        List<Map> list = new ProductService(dto).getAllComponentTranslation();
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void testPostSourceSet() {
    	Locale locale = new Locale("zh", "CN");
    	List<JSONObject > sources = new ArrayList<JSONObject>();
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
    	List<JSONObject > sources2 = new ArrayList<JSONObject>();
    	logger.debug(sources2.toString());
    	Assert.assertFalse(translation.postStrings(locale, component1, sources2));
    }

	@Test
	public void testSendSource() {
		boolean f = translation.postString(new Locale("zh", "CN"), component1, "key", "Host", "It's a comment");
		Assert.assertTrue(f);
		boolean ff = translation.postString(new Locale("zh", "CN"), component2, "key1", "source1", "It's a comment1");
		Assert.assertTrue(ff);
	}
	
	@Test
	public void testGetTranslation_SingleQuota() {
		Locale zhLocale = new Locale("zh", "Hans");
		String comp = "Component1";
		String key = "single quotation marks-notcollected";
		String source = "Operator '{0}' 不支持 for property ' { 1} '";
		Object[] args = {"aaa", "bbb"};

		VIPCfg vc = VIPCfg.getInstance();
		boolean existing_collect = vc.isCollectSource();
		vc.setCollectSource(false);
		boolean existing_pseudo = vc.isPseudo();
		vc.setPseudo(true);
		
		
		String enTrans1 = translation.getString(zhLocale, comp, key, source, "", args);

		vc.setPseudo(existing_pseudo);
		vc.setCollectSource(existing_collect);

		String expected = "@@Operator 'aaa' 不支持 for property ' bbb '@@";
		logger.debug("enTrans1: "+enTrans1);
		Assert.assertArrayEquals(new Object[]{expected}, new Object[]{enTrans1});
	}

	@Test
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
		
		logger.debug("pseudoTrans1: "+pseudoTrans1);
		Assert.assertArrayEquals(new Object[]{expected}, new Object[]{pseudoTrans1});
	}

    /* bug: 2360553
	cannot get pseudo translation when set 'source collection' and 'pseudo' to true for a new key
	get translation with a new key which does not exist in VIP server.
    should return source with pseudo tag @@
     */
	//locale: en-US. client won't contact server in any case.
    @Test
    public void testGetMessage__NotCollected_2() throws InterruptedException {
    	VIPCfg gc = VIPCfg.getInstance();
//        gc.setProductName("Sample");
//        gc.setVersion("1.0.0");
        gc.setPseudo(true);
        gc.setCollectSource(true);
        gc.initializeVIPService();
        

        //new key and source
        String randomStr = getSaltString();
        Locale locale1 = new Locale("en", "US");
        String key1 = "MessagesNotFound"+randomStr;
        String source1 = "Some of the messages were not found"+randomStr;
        String message1 = translation.getString(locale1, component1, key1, source1, "");
        String expected1 = "@@"+source1+"@@";
        Assert.assertEquals(expected1, message1);

        //server already collected
        String key2= "LeadTest";
        String source2 = "It's a testing source";
        String message2 = translation.getString(locale1, component1, key2, source2, "");
        String expected2 = "@@"+source2+"@@";
        Assert.assertEquals(expected2, message2);
        
        //source1 changed
		Thread.sleep(4*1000); 
        String source1_1 = source1+"-new";
        String message1_1 = translation.getString(locale1, component1, key1, source1_1, "");
        String expected1_1 = "@@"+source1_1+"@@";
        Assert.assertEquals(expected1_1, message1_1);
    }

    /* bug: 2360553
	cannot get pseudo translation when set 'source collection' and 'pseudo' to true for a new key
	get translation with a new key which does not exist in VIP server.
    should return source with pseudo tag @@
     */
	//locale: zh-CN
	@Test
	public void testGetMessage__NotCollected_3() throws InterruptedException {
		VIPCfg gc = VIPCfg.getInstance();
		gc.setPseudo(true);
		gc.setCollectSource(true);
		gc.initializeVIPService();

		String component = component1;

		//new key and source
		String randomStr = getSaltString();
		Locale locale1 = new Locale("zh", "CN");
		String key1 = "MessagesNotFound"+randomStr;
		String source1 = "Some of the messages were not found"+randomStr;
		String message1 = translation.getString(locale1, component, key1, source1, "");
		String expected1 = "@@"+source1+"@@";
		Assert.assertEquals(expected1, message1);

		//server already collected
		String key2= "LeadTest";
		String source2 = "[{0}] Test alert";
		String message2 = translation.getString(locale1, "JAVA", key2, "", "");
		String expected2 = "#@"+source2+"#@";
		Assert.assertEquals(expected2, message2);

		//source1 changed
		Thread.sleep(4*1000);
		String source1_1 = source1+"-new";
		String message1_1 = translation.getString(locale1, component, key1, source1_1, "");
		String expected1_1 = "@@"+source1_1+"@@";
		Assert.assertEquals(expected1_1, message1_1);
	}
    

	@Test
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
		
		logger.debug("pseudoTrans1: "+pseudoTrans1);
		Assert.assertEquals(expected, pseudoTrans1);
	}

	@Test
	public void testGetSourcesOfMCompAndOneLoc() {
		clearTranslationCache();

		String component1 = "JAVA";
		String component2 = "USER";
		String componentNonexistent = "Nonexistent";

		Locale locale1 = Locale.forLanguageTag("en");
		Locale locale2 = Locale.forLanguageTag("fr");
		Locale locale3 = Locale.forLanguageTag("zh-Hans");

		// Get 1 component and 1 locale
		Map<String, Map<String, String>> result = translation.getStrings(locale2, Arrays.asList(component1));
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("H\u00F4te", result.get(component1).get("table.host"));

		// Get 2 components and 1 locale
		result = translation.getStrings(locale2, Arrays.asList(component1, component2));
		Assert.assertEquals(2, result.size()); // 2 components
		Assert.assertEquals(2, result.get(component2).size()); // 2 messages
		Assert.assertEquals("valeur-1", result.get(component2).get("user-1"));

		// Get a nonexistent locale
		result = translation.getStrings(Locale.ITALY, Arrays.asList(component1));
		Assert.assertEquals(0, result.size());

		// Get a nonexistent component
		result = translation.getStrings(locale3, Arrays.asList(componentNonexistent));
		Assert.assertEquals(0, result.size());

		// Get with a null locale
		result = translation.getStrings((Locale) null, Arrays.asList(component1));
		Assert.assertEquals(0, result.size());

		// Get with a null component
		result = translation.getStrings(locale2, (List<String>) null);
		Assert.assertEquals(0, result.size());

		// Get with an empty component list
		result = translation.getStrings(locale2, new ArrayList<String>());
		Assert.assertEquals(0, result.size());

		// Get English
		result = translation.getStrings(locale1, Arrays.asList(component1));
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("Host", result.get(component1).get("table.host"));
	}

	@Test
	public void testGetSourcesOfMCompAndMLoc() {
		clearTranslationCache();

		String component1 = "JAVA";
		String component2 = "USER";

		Locale locale1 = Locale.forLanguageTag("en");
		Locale locale2 = Locale.forLanguageTag("fr");
		Locale locale3 = Locale.forLanguageTag("zh-Hans");

		// Get 1 component and 1 locale
		Map<Locale, Map<String, Map<String, String>>> result = translation.getStrings(Arrays.asList(locale2),
				Arrays.asList(component1));
		Assert.assertSame(locale2, result.keySet().iterator().next());
		Assert.assertEquals(1, result.size()); // 1 locale
		Assert.assertEquals(1, result.get(locale2).size()); // 1 component
		Assert.assertEquals("H\u00F4te", result.get(locale2).get(component1).get("table.host"));

		// Get 2 components and 1 locale
		result = translation.getStrings(Arrays.asList(locale2), Arrays.asList(component1, component2));
		Assert.assertEquals(2, result.get(locale2).size()); // 2 components
		Assert.assertEquals(2, result.get(locale2).get(component2).size()); // 2 messages
		Assert.assertEquals("valeur-1", result.get(locale2).get(component2).get("user-1"));

		// Get with a null locale
		result = translation.getStrings((List<Locale>) null, Arrays.asList(component1));
		Assert.assertEquals(0, result.size());

		// Get with a null component
		result = translation.getStrings(Arrays.asList(locale2), (List<String>) null);
		Assert.assertEquals(0, result.size());

		// Get with an empty component list
		result = translation.getStrings(Arrays.asList(locale2), new ArrayList<String>());
		Assert.assertEquals(0, result.size());

		clearTranslationCache();
		// Get 2 components and 2 locales
		Map<Locale, Map<String, Map<String, String>>> result2 = translation.getStrings(
				Stream.of(locale2, locale3).collect(Collectors.toList()), Arrays.asList(component1, component2));
		Assert.assertEquals(2, result2.size()); // 2 locales
		Assert.assertEquals(2, result2.get(locale3).size()); // 2 components
		Assert.assertEquals(2, result2.get(locale3).get(component2).size()); // 2 messages
		Assert.assertEquals("valeur-1", result2.get(locale2).get(component2).get("user-1"));

		// more cases to test cache
		// more cases to test the message sending to server
	}
}
