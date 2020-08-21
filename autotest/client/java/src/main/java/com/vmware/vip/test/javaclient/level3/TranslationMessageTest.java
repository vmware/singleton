/*******************************************************************************
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
/*
 * Copyright 2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.test.javaclient.level3;

import java.util.Locale;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.vmware.vip.test.common.Utils;
import com.vmware.vip.test.common.annotation.TestCase;
import com.vmware.vip.test.common.annotation.TestCase.Priority;
import com.vmware.vip.test.javaclient.Constants;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.NumberFormatting;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;

public class TranslationMessageTest extends TestBase {
	public NumberFormatting numberFormatting = null;
	public Cache formatCache = null;
	TranslationMessage translation;
	@BeforeClass
	public void preparing() throws Exception {
		initVIPServer();
	}

	public void initVIPServer() throws Exception {
		VIPCfg vipCfg = VIPCfg.getInstance();
		vipCfg.initialize(Utils.removeFileExtension(Constants.VIP_CONFIG_FILE_NAME));
		vipCfg.initializeVIPService();
		vipCfg.createTranslationCache(MessageCache.class);
		I18nFactory i18n = I18nFactory.getInstance(vipCfg);
		translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
	}

	@Test(enabled=true, dataProvider="TranslationData", priority=0)
	@TestCase(id = "001", name = "TranslationMessageTest", description = "test desc", priority=Priority.P0)
	public void testNumberFormat(String component, String bundle,
			Locale locale, String key, Object[] args, String expected) {
		String message = translation.getString2(component, bundle, locale, key, args);
		log.verifyEqual(String.format("component=%s, bundle=%s, locale=%s, key=%s",
				component, bundle, locale, key), message, expected);
	}

	@DataProvider(name = "TranslationData")
	public static Object[][] getTranslationData() {
		return new Object[][]{
			{"default", "default", new Locale("ja"), "Verify_Translation", null, "翻訳の確認"},
			{"custom100", "custom100", new Locale("ja"), "Partner_Name", null, "パートナー名"},
			{"custom100", "notexist", new Locale("ja"), "Dear_Custom", null, "親愛なるお客様"},
			{"default", "default", new Locale("ja"), "my.new.key.one", null, "my new test string one"}
		};
	}
}
