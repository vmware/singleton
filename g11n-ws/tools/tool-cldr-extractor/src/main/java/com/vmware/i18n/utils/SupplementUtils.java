/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.i18n.common.CLDRConstants;

public class SupplementUtils {

	private static Logger logger = LoggerFactory.getLogger(SupplementUtils.class);

	/**
	 * Get all currency data from 'currencyData.json' file for symbol and name
	 * 
	 * @param zipPath
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> getCurrencySupplementalData() {
		String zipPath = CLDRConstants.CORE_ZIP_FILE_PATH;
		String fileName = "cldr-core-" + CLDRConstants.CLDR_VERSION + "/supplemental/currencyData.json";
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject currencyDataContents = JSONUtil.string2JSON(json);
		String currencyDataRegionJson = JSONUtil.select(currencyDataContents, "supplemental.currencyData").toString();
		return JSONUtil.getMapFromJson(currencyDataRegionJson);
	}

	@SuppressWarnings("unchecked")
	public static void supplementalCurrencyExtract() {
		logger.info("Start to extract cldr supplemental currency data ... ");
		Map<String, Object> supplementalMap = getCurrencySupplementalData();
		Map<String, Object> fractionsMap = (Map<String, Object>) supplementalMap.get("fractions");
		Map<String, Object> regionMap = (Map<String, Object>) supplementalMap.get("region");
		Map<String, String> resMap = new LinkedHashMap<String, String>();
		for (Map.Entry<String, Object> entry : regionMap.entrySet()) {
			LinkedList<Object> list = (LinkedList<Object>) entry.getValue();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> itemMap = (Map<String, Object>) list.get(i);
				for (Map.Entry<String, Object> item : itemMap.entrySet()) {
					Map<String, String> data = (Map<String, String>) item.getValue();
					if (!CommonUtil.isEmpty(data.get("_from")) && CommonUtil.isEmpty(data.get("_to"))) {
						resMap.put(entry.getKey(), item.getKey());
						break;
					}
				}
			}
		}
		try {
			String result = new ObjectMapper().writeValueAsString(resMap);
			Map<String, Object> tmpMap = new LinkedHashMap<String, Object>();
			tmpMap.put("fractions", fractionsMap);
			tmpMap.put("regions", JSONUtil.string2SortMap(result));
			CLDRUtils.writePatternDataIntoFile(
					CLDRConstants.GEN_CLDR_SUPPLEMENT_DIR + File.separator + "currencies.json", tmpMap);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		}
		logger.info("Extract cldr supplemental currency data complete!");
	}

}
