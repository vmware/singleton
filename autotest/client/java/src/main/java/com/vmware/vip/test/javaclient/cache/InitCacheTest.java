/*******************************************************************************
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.vmware.vip.test.javaclient.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

import com.vmware.vipclient.i18n.base.DataSourceEnum;
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
    		log.info(String.format("Initialize with test config '%s'.", configName));
    		vipCfg.initialize(ClientConfigHelper.formatConfigName(configName));
            ClientConfigHelper.removeConfig(configName);
            List<DataSourceEnum> msgOriginsQueueOrig = vipCfg.getMsgOriginsQueue();
            vipCfg.setMsgOriginsQueue(new LinkedList<DataSourceEnum>(Arrays.asList(DataSourceEnum.Bundle)));

            try {
            	String component = TestData.OFFLIN_BUNDLES_COMPONENT;
            	String key = TestData.OFFLIN_BUNDLES_KEY1;
            	String languageTag = OfflineBundleHelper.getRandomLocalizedLanguageTag(component);
            	
            	TranslationMessage translation = getTranslationMessage(vipCfg);
            	String msg = translation.getMessage(Locale.forLanguageTag(languageTag), component, key);
            	log.verifyEqual("Get translation from locale bundles", msg,
            			OfflineBundleHelper.getTranslation(component, languageTag, key));
            } catch (Exception e) {
            	log.verifyNull("Verify no exception", e, e.toString());
            }
            vipCfg.setMsgOriginsQueue(msgOriginsQueueOrig);
        } else {
        	log.error("Failed to make offline mode configuration file. Skip all test steps.");
        }
	}
}
