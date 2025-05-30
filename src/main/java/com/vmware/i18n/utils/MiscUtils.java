/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import java.io.File;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.i18n.cldr.CLDR;
import com.vmware.i18n.common.CLDRConstants;
import com.vmware.i18n.common.Constants;

public class MiscUtils {

	private static Logger logger = LoggerFactory.getLogger(MiscUtils.class);

	private static MiscUtils instance;

	public static MiscUtils getInstance() {
		if (instance == null) {
			instance = new MiscUtils();
		}
		return instance;
	}

	public static void miscDataExtract() {
		logger.info("Start to extract cldr misc data ... ");
		Map<String, String> allLocales = CLDRUtils.getAllCldrLocales();
		Map<String, Object> contextTransformsMap = null;
		for (String locale : allLocales.values()) {
			contextTransformsMap = new LinkedHashMap<>();
			Map<String, Object> contextTransformsData = MiscUtils.getInstance().getContextTransformsData(locale);
			if (contextTransformsData != null) {
				CLDR cldr = new CLDR(locale);
				contextTransformsMap.put(Constants.LANGUAGES, cldr.getLanguage());
				contextTransformsMap.put(Constants.CONTEXT_TRANSFORMS, contextTransformsData);
				CLDRUtils.writePatternDataIntoFile(CLDRConstants.GEN_CLDR_MISC_DIR + locale + File.separator
						+ CLDRConstants.CONTEXT_TRANSFORM_JSON, contextTransformsMap);
			}
		}
		logger.info("Extract cldr misc data complete!");
	}

	/**
	 * Get CLDR ContextTransforms data
	 *
	 * @param locale
	 * @return
	 */
	private Map<String, Object> getContextTransformsData(String locale) {
		Map<String, Object> contextTransformsMap = new LinkedHashMap<String, Object>();
		String zipPath = CLDRConstants.MISC_ZIP_FILE_PATH;
		String fileName = MessageFormat.format(CLDRConstants.MISC_CONTEXT_TRANSFORM, locale);
		String json = CLDRUtils.readZip(fileName, zipPath);
		if (CommonUtil.isEmpty(json)) {
			return null;
		}
		JSONObject contextTransforms = JSONUtil.string2JSON(json);
		String node = MessageFormat.format(CLDRConstants.CONTEXT_TRANSFORM_NODE, locale);
		if (CommonUtil.isEmpty(JSONUtil.select(contextTransforms, node))) {
			return null;
		}

		String contextTransformsDataJson = JSONUtil.select(contextTransforms, node).toString();
		contextTransformsMap.putAll(JSONUtil.string2SortMap(contextTransformsDataJson));
		return contextTransformsMap;
	}
}
