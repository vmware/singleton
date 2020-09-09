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
import com.vmware.vip.test.javaclient.BundleDataProvider;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vip.test.javaclient.TestConstants;

import com.vmware.vipclient.i18n.base.instances.TranslationMessage;

public class InitCacheTest extends TestBase{
	@Test(enabled=true, priority=0, groups=TestGroups.BUG,
			dataProvider = "getACommonOfflineMessage", dataProviderClass=BundleDataProvider.class)
	@TestCase(id = "001", name = "InitCacheOfflineMode", priority=Priority.P0,
	description = "Initailize cache in offline mode, bug from https://github.com/vmware/singleton/issues/661")
	public void initializeCacheTest(String component, Locale locale, String key, String translation, String messageInDefaultLocale) throws Exception {
		log.info("Make offline mode config file and set initialize cache as ture.");
		String configName = "testconf-offline-init-cache.properties";
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(Constants.VIP_CONF_KEY_INIT_CACHE, "true");
		boolean isConfigReady = ClientConfigHelper.makeConfig(
				TestConstants.CLIENT_CONFIG_TEMPLATE_OFFLINE, configName, properties);
        if (isConfigReady) {
    		log.info(String.format("Initialize with test config '%s'.", configName));
    		vipCfg.initialize(ClientConfigHelper.formatConfigName(configName));
            ClientConfigHelper.removeConfig(configName);

            try {
            	TranslationMessage tm = getTranslationMessage(vipCfg);
            	String msg = tm.getMessage(locale, component, key);
            	log.verifyEqual("Get translation from local bundles", msg, translation);
            } catch (Exception e) {
            	log.verifyNull("Verify no exception", e, e.toString());
            }
        } else {
        	log.error("Failed to make offline mode configuration file. Skip all test steps.");
        }
	}
}
