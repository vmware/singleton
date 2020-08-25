/*******************************************************************************
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.vmware.vip.test.javaclient.cache;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.testng.annotations.Test;

import com.vmware.vip.test.common.TestGroups;
import com.vmware.vip.test.common.annotation.TestCase;
import com.vmware.vip.test.common.annotation.TestCase.Priority;
import com.vmware.vip.test.javaclient.ClientConfigHelper;
import com.vmware.vip.test.javaclient.Constants;
import com.vmware.vip.test.javaclient.OfflineBundleHelper;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vip.test.javaclient.TestData;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;

public class InitCacheTest extends TestBase{
	@Test(enabled=true, priority=0, groups=TestGroups.BUG)
	@TestCase(id = "001", name = "InitCacheOfflineMode", priority=Priority.P0,
	description = "Initailize cache in offline mode, bug from https://github.com/vmware/singleton/issues/661")
	public void initializeCacheTest() throws Exception {
		log.info("Make offline mode config file and set initialize cache as ture.");
		String configName = "testconf-offline-init-cache.properties";
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(Constants.VIP_CONF_KEY_INIT_CACHE, "true");
		boolean isConfigReady = ClientConfigHelper.makeConfig(
				TestData.CLIENT_CONFIG_TEMPLATE_OFFLINE, configName, properties);
        if (isConfigReady) {
    		log.debug(String.format("Test config '%s' is generated.", configName));
        }
		log.debug("Initialize with test config.");
		vipCfg.initialize(ClientConfigHelper.formatConfigName(configName));
        try {
        	log.info("Create translation cache.");
        	Cache translationCache = vipCfg.createTranslationCache(MessageCache.class);
        	log.verifyTrue("Verify cache is not empty", translationCache.size()>0);
        	I18nFactory i18n = I18nFactory.getInstance(vipCfg);
        	TranslationMessage translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
        	String component = TestData.OFFLIN_BUNDLES_COMPONENT;
        	String key = TestData.OFFLIN_BUNDLES_KEY1;
        	String languageTag = OfflineBundleHelper.getRandomLocalizedLanguageTag(component);
        	String msg = translation.getMessage(Locale.forLanguageTag(languageTag), component, key);
        	log.verifyEqual("Get translation from locale bundles", msg,
        			OfflineBundleHelper.getTranslation(component, languageTag, key));
        } catch (Exception e) {
        	log.verifyNull("Verify no exception", e, e.toString());
        }
        ClientConfigHelper.removeConfig(configName);
	}
}
