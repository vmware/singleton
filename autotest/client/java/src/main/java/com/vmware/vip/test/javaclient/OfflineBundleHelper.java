/*******************************************************************************
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.vmware.vip.test.javaclient;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import com.vmware.g11n.log.GLogger;
import com.vmware.vip.test.common.Utils;

public class OfflineBundleHelper {
	private static GLogger log = GLogger.getInstance(TestBase.class.getName());
	public static final String OFFLINE_BUNDLES_FOLER = "offlineBundles";
	public static final String OFFLINE_BUNDLES_COMPONENT = "JAVA";
	private static final String BUNDLE_PREFIX = "messages_";
	private static final String BUNDLE_SUFFIX = ".json";
	private static final String BUNDLE_MESSAGES_KEY = "messages";
	public static String getTranslation(String component, String languageTag, String key) {
		String bundleFileName = BUNDLE_PREFIX + languageTag + BUNDLE_SUFFIX;
		String bundleFilePath = Paths.get(OFFLINE_BUNDLES_FOLER, component, bundleFileName).toString();
		log.debug(String.format("Read translation from bundle file '%s'", bundleFilePath));
		JSONObject jsonObj = Utils.readJSONObjFromFile(bundleFilePath);
		JSONObject messagesObj = jsonObj.getJSONObject(BUNDLE_MESSAGES_KEY);
		return messagesObj.getString(key);
	}
	/*
	 * get none English language tags
	 */
	public static List<String> getLocalizedLanguageTags(String component) {
		List<String> tags = new ArrayList<String>();
		File componentRootdir = new File(Paths.get(OFFLINE_BUNDLES_FOLER, OFFLINE_BUNDLES_COMPONENT).toString());
		for (String fileName : componentRootdir.list()) {
			if (isBundleFile(fileName)) {
				String languageTag = getLanguageTagFromBundleFileName(fileName);
				if (isNoneEnglish(languageTag)) {
					log.debug(String.format("Find a localized language tag '%s'.", languageTag));
					tags.add(languageTag);
				}
			}
		}
		return tags;
	}
	public static String getRandomLocalizedLanguageTag(String component) {
		List<String> languageTags = getLocalizedLanguageTags(component);
		Random random = new Random();
		int index = random.nextInt(languageTags.size()-1);
		return languageTags.get(index);
	}
	private static boolean isNoneEnglish(String languageTag) {
		if (languageTag.equals("en") || languageTag.startsWith("en-") || languageTag.equals("source")) {
			return false;
		} else {
			return true;
		}
	}
	private static String getLanguageTagFromBundleFileName(String fileName) {
		return fileName.substring(BUNDLE_PREFIX.length(), fileName.indexOf(BUNDLE_SUFFIX));
	}
	private static boolean isBundleFile(String fileName) {
		return fileName.startsWith(BUNDLE_PREFIX) && fileName.endsWith(BUNDLE_SUFFIX);
	}
}
