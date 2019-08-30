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

import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.LocaleMessage;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class LocaleTest extends BaseTestClass {
	LocaleMessage localeI18n;

	@Before
	public void init() {
        VIPCfg gc = VIPCfg.getInstance();
        gc.initialize("vipconfig");
        gc.initializeVIPService();
        gc.createFormattingCache(MessageCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        localeI18n = (LocaleMessage)i18n.getMessageInstance(LocaleMessage.class);
	}

	@Test
	public void testPickupLocaleFromList() {
		Locale[] supportedLocales = { Locale.forLanguageTag("de"),
				Locale.forLanguageTag("es"), Locale.forLanguageTag("fr"),
				Locale.forLanguageTag("ja"), Locale.forLanguageTag("ko"),
				Locale.forLanguageTag("zh-Hans"),
				Locale.forLanguageTag("zh-Hant")
				
		
		};
		Locale[] testLocales = { Locale.forLanguageTag("de"),
				Locale.forLanguageTag("es"), Locale.forLanguageTag("fr"),
				Locale.forLanguageTag("ja"), Locale.forLanguageTag("ko"),
				Locale.forLanguageTag("zh"), Locale.forLanguageTag("zh-CN"),
				Locale.forLanguageTag("zh-TW"),
				Locale.forLanguageTag("zh-HANS-CN"),
				Locale.forLanguageTag("zh-HANT-TW"),
				Locale.forLanguageTag("zh-HANS"),
				Locale.forLanguageTag("zh-HANT") };
		
		String[] expectedLocales = { "de", "es", "fr", "ja", "ko", "zh",
				"zh-Hans", "zh-Hant","zh-Hans", "zh-Hant", "zh-Hans", "zh-Hant"};
		
		
		for (int i = 0; i < testLocales.length; i++) {
			String matchedLanguageTag = LocaleUtility.pickupLocaleFromList(
					Arrays.asList(supportedLocales), testLocales[i])
					.toLanguageTag();
			
			logger.debug(matchedLanguageTag + "-----" + expectedLocales[i]);
			Assert.assertEquals(expectedLocales[i], matchedLanguageTag);
		}
	}

    @Test
    public void normalizeToLanguageTag() {
        String[] testLocaleStrs = { "de", "es", "fr", "ja", "ko", "en-US", "zh-CN", "zh-TW",
                "zh-Hans", "zh-Hant", "zh__#Hans", "zh__#Hant",
                "zh-Hans-CN", "zh-Hant-TW","zh_CN_#Hans", "zh_TW_#Hant" };
        String[] expectedLocales = { "de", "es", "fr", "ja", "ko", "en-US", "zh-CN", "zh-TW",
                "zh-Hans", "zh-Hant", "zh-Hans", "zh-Hant",
                "zh-Hans-CN", "zh-Hant-TW","zh-Hans-CN", "zh-Hant-TW" };
        for (int i = 0; i < testLocaleStrs.length; i++) {
            String normalizedLanguageTag = LocaleUtility.normalizeToLanguageTag(testLocaleStrs[i]);
            Assert.assertEquals(expectedLocales[i], normalizedLanguageTag);
        }
    }

	@Test
	public void testGetRegionList() throws ParseException{
		List<String> list = new ArrayList<String>();
		list.add("zh_Hant");
		list.add("ja");
		list.add("de");
		Map<String, Map<String, String>> result = localeI18n.getRegionList(list);
		Assert.assertNotNull(result);
		localeI18n.getRegionList(list);//get data from cache
	}

	@Test
	public void testGetDisplayNamesByLanguage() throws ParseException {
		Map<String, String> resp = localeI18n.getDisplayLanguagesList("zh_Hans");
		Assert.assertNotNull(resp);
		localeI18n.getDisplayLanguagesList("zh_Hans");//get data from cache
	}
}
