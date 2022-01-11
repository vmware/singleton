/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.locale.service.impl;

import java.text.MessageFormat;
import java.util.Map;

import com.vmware.i18n.common.CLDRConstants;
import com.vmware.i18n.common.Constants;
import com.vmware.i18n.locale.dao.impl.LocaleDaoImpl;
import com.vmware.i18n.locale.service.ILocaleService;
import com.vmware.i18n.utils.JSONUtil;
import com.vmware.i18n.utils.LocalJSONReader;

@SuppressWarnings("unchecked")
public class LocaleServiceImpl implements ILocaleService {

	public static Map<String, String> localePathMap = null;
	public static Map<String, String> defaultContentMap = null;
	static {
		String result = "";
		String defaultContent = "";
		if (CLDRConstants.JSON_PATH.lastIndexOf(".jar") > 0) {
			result = LocalJSONReader.readJarJsonFile(CLDRConstants.JSON_PATH, CLDRConstants.PARSE_DATA);
			defaultContent = LocalJSONReader.readJarJsonFile(CLDRConstants.JSON_PATH, CLDRConstants.DEFAULT_CONTENT_PATH);
		} else {
			result = LocalJSONReader.readLocalJSONFile(CLDRConstants.RESOURCES_PATH + CLDRConstants.PARSE_DATA);
			defaultContent = LocalJSONReader.readLocalJSONFile(CLDRConstants.RESOURCES_PATH + CLDRConstants.DEFAULT_CONTENT_PATH);
		}
		localePathMap = (Map<String, String>) JSONUtil.getMapFromJson(result).get(Constants.LOCALE_PATH);
		defaultContentMap = (Map<String, String>) JSONUtil.getMapFromJson(defaultContent).get(Constants.DEFAULT_CONTENT);
	}

	@Override
	public String getLocaleData(String language, String filePath) {
		if (localePathMap.get(language) == null)
			return "";
		filePath = MessageFormat.format(filePath, localePathMap.get(language));
		return new LocaleDaoImpl().getLocaleData(CLDRConstants.JSON_PATH, filePath);
	}

	/**
	 * Query defaultContent.json to determine whether there is a matching locale,
	 * and if so, get the processed result and run
	 * e.g. fr-FR ==> fr
	 *
	 * @param locale
	 * @return
	 */
	@Override
	public String getLocaleWithDefaultRegion(String locale) {
		locale = locale.replace("_", "-");
		for (Map.Entry<String, String> defaultRegionLocale : defaultContentMap.entrySet()) {
			if (locale.toLowerCase().equals(defaultRegionLocale.getKey())) {
				locale = locale.substring(0, locale.lastIndexOf("-"));
				return locale;
			}
		}
		return "";
	}

	@Override
	public String getContextTransforms(String displayLanguage) {
		if (localePathMap.get(displayLanguage) == null)
			return "";
		String filePath = MessageFormat.format(CLDRConstants.CONTEXT_TRANSFORM_PATH, localePathMap.get(displayLanguage));
		return new LocaleDaoImpl().getLocaleData(CLDRConstants.JSON_PATH, filePath);
	}

}
