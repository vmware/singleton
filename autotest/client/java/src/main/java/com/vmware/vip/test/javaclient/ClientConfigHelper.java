/*******************************************************************************
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.vmware.vip.test.javaclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.vmware.g11n.log.GLogger;

import com.vmware.vip.test.common.Utils;

public class ClientConfigHelper {
	private static GLogger log = GLogger.getInstance(TestBase.class.getName());
	public static final String CONFIG_TEMPLATE_ONLIN = "vipconfig.properties";
	public static final String CONFIG_TEMPLATE_OFFLINE = "vipconfig-offline.properties";
	public static final String CONFIG_TEMPLATE_MIX = "vipconfig-mix.properties";

	/*
	 * make a new client configuration file with specific properties based on a template
	 */
	public static boolean makeConfig(String configTemplate, String newConfigName, Map<String, String> properties) {
		boolean isConfigGenerated = false;
		//get configuration file name with file extensions
		newConfigName = newConfigName.endsWith(".properties") ? newConfigName:newConfigName+".properties";
		configTemplate = configTemplate.endsWith(".properties") ? configTemplate:configTemplate+".properties";
		Properties pro = new Properties();
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(configTemplate);
			pro.load(inputStream);
			//set all new properties
			for( String key : properties.keySet()) {
				pro.setProperty(key, properties.get(key));
			}
			outputStream = new FileOutputStream(newConfigName);
			pro.store(outputStream, "Make new client config file.");
			isConfigGenerated = true;
		} catch (FileNotFoundException e) {
			log.error(String.format("Cannot find configuration template '%s'", configTemplate));
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Fail to store properties into new configuration file");
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return isConfigGenerated;
	}

	public static void removeConfig(String configName) {
		configName = configName.endsWith(".properties") ? configName:configName+".properties";
		File f = new File(configName);
		f.delete();
		log.debug(String.format("Remove config file '%s'.", configName));
	}

	/*
	 * generate VIP config name used in VIPCfg.initialize()
	 */
	public static String formatConfigName(String configPath) {
		return Utils.removeFileExtension(Utils.getFileName(configPath));
	}
}
