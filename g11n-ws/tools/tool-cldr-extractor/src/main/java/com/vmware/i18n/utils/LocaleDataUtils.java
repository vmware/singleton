/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import java.io.File;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.i18n.cldr.CLDR;
import com.vmware.i18n.common.CLDRConstants;
import com.vmware.i18n.common.Constants;

public class LocaleDataUtils {

	private static Logger logger = LoggerFactory.getLogger(LocaleDataUtils.class);

	private static volatile LocaleDataUtils instance;

	public static LocaleDataUtils getInstance() {
		if (instance == null) {
			instance = new LocaleDataUtils();
		}
		return instance;
	}

	/**
	 * get all cldr scripts data
	 *
	 * @return
	 */
	public Map<String, String> getAllScripts() {
		Map<String, String> scriptsMap = new HashMap<String, String>();
		String fileName = "cldr-core-" + CLDRUtils.CLDR_VERSION + "/scriptMetadata.json";
		String json = CLDRUtils.readZip(fileName, CLDRConstants.CORE_ZIP_FILE_PATH);
		JSONObject allScriptsContents = JSONUtil.string2JSON(json);
		JSONObject object = (JSONObject) JSONUtil.select(allScriptsContents, "scriptMetadata");
		for (Object key : object.keySet()) {
			scriptsMap.put(key.toString(), key.toString());
		}
		return scriptsMap;
	}

	/**
	 * get default region collection
	 *
	 * @return
	 */
	private Map<String, String> getAllDefaultRegion(Map<String, String> regionMap) {
		Map<String, String> defaultRegionMap = new HashMap<String, String>();
		String fileName = "cldr-core-" + CLDRUtils.CLDR_VERSION + "/defaultContent.json";
		String json = CLDRUtils.readZip(fileName, CLDRConstants.CORE_ZIP_FILE_PATH);
		JSONObject allRegionContents = JSONUtil.string2JSON(json);
		JSONArray array = (JSONArray) JSONUtil.select(allRegionContents, "defaultContent");
		for (Object item : array) {
			String locale = item.toString();
			String[] arr = locale.split("-");
			switch (arr.length) {
            case 2:
                if (regionMap.containsKey(arr[1])){
                    defaultRegionMap.put(arr[0], arr[1]);
                }
                break;
            case 3:
                if (regionMap.containsKey(arr[2])){
                    defaultRegionMap.put(arr[0] + "-" + arr[1], arr[2]);
                }
                break;
            default:
                break;
			}
		}
		return defaultRegionMap;
	}

	/**
	 * get available region collection
	 *
	 * @return
	 */
	private Map<String, String> getAvailableLocaleRegion(Map<String, String> regionMap) {
		Map<String, String> localesRegionMap = new TreeMap<String, String>();
		String fileName = "cldr-core-" + CLDRUtils.CLDR_VERSION + "/availableLocales.json";
		String zipPath = CLDRConstants.CORE_ZIP_FILE_PATH;
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject allLocalesContents = JSONUtil.string2JSON(json);
		JSONArray array = (JSONArray) JSONUtil.select(allLocalesContents, "availableLocales.full");
		for (Object item : array) {
			String locale = item.toString();
			if (locale.equals("root") || "yue".equals(locale)) {
				continue;
			}
			String[] arr = locale.split("-");
			switch (arr.length) {
			case 2:
				if (regionMap.containsKey(arr[1])){
					localesRegionMap.put(locale, arr[1]);
				}
				break;
			case 3:
				if (regionMap.containsKey(arr[2])){
					localesRegionMap.put(locale, arr[2]);
				}else if(!regionMap.containsKey(arr[2]) && regionMap.containsKey(arr[1])){
					localesRegionMap.put(locale, arr[1]);
				}
				break;
			default:
				break;
			}
		}
        return localesRegionMap;
    }

	/**
	 *
	 * @Title: getDefaultReginCode
	 * @Description: getDefaultRegionCode
	 * @param: @param inputLocale
	 * @param: @return      
	 * @return: String 
	 * @date 2019-05-30 14:25     
	 * @throws
	 */
	public String getDefaultRegionCode(String inputLocale, Map<String, String> regionMap ) {

		String defaultRegionCode = "";
		Map<String, String> defaultRegionMap = getAllDefaultRegion(regionMap);
		Map<String, String> localesRegionMap = getAvailableLocaleRegion(regionMap);
		if (inputLocale.indexOf('_') != -1) {
			inputLocale = inputLocale.replace("_", "-");
		}
		if (defaultRegionMap.containsKey(inputLocale)){
			defaultRegionCode = defaultRegionMap.get(inputLocale);
		}else{
			if (localesRegionMap.containsKey(inputLocale)) {
				defaultRegionCode = localesRegionMap.get(inputLocale);
			}
		}
		return defaultRegionCode;
	}

	/**
	 * get default locale collection according to defaultContent.json
	 *
	 * @return
	 */
	public Map<String, String> getDefaultContentLocales() {
		Map<String, String> defaultRegionMap = new HashMap<>();
		JSONArray array = getDefaultContentLocaleArray();
		for (Object item : array) {
			String locale = item.toString();
			defaultRegionMap.put(locale.toLowerCase(), locale);
		}
		return defaultRegionMap;
	}

	private JSONArray getDefaultContentLocaleArray(){
		String fileName = "cldr-core-" + CLDRUtils.CLDR_VERSION + "/defaultContent.json";
		String json = CLDRUtils.readZip(fileName, CLDRConstants.CORE_ZIP_FILE_PATH);
		JSONObject allRegionContents = JSONUtil.string2JSON(json);
		JSONArray array = (JSONArray) JSONUtil.select(allRegionContents, "defaultContent");
		return array;
	}

	private Map<String, String> getTerriContain(){
		Map<String, String> containMap = new HashMap<String, String>();
		String fileName = "cldr-core-" + CLDRUtils.CLDR_VERSION + "/supplemental/territoryContainment.json";
		String json = CLDRUtils.readZip(fileName, CLDRConstants.CORE_ZIP_FILE_PATH);
		JSONObject contents = JSONUtil.string2JSON(json);
		JSONObject object = (JSONObject) JSONUtil.select(contents, "supplemental.territoryContainment");
		for (Object key : object.keySet()) {
			containMap.put(key.toString(), key.toString());
		}
		return containMap;
	}

	/**
	 * 1.the region's key shouldn't contain 'alt' 
	 * 2.the region's key shouldn't be found in 'territoryContainment' node in 'territoryContainment.json'
	 */

	private Map<String, String> getRegionList(String inputLocale, Map<String, String> terriContain) {
		Map<String, String> regionMap = new TreeMap<String, String>();
		Map<String, Object> cldrRegionData = getRegionsData(inputLocale);
		if (cldrRegionData != null) {
			Set<Map.Entry<String, Object>> cldrRegionDataSet = cldrRegionData.entrySet();
			for (Map.Entry<String, Object> cldrRegion : cldrRegionDataSet) {
				if (cldrRegion.getKey().indexOf("alt") == -1) {
					String region = null;
					region = cldrRegion.getKey();
					if (!CommonUtil.isEmpty(region) && terriContain.get(region) == null) {
						String dispName = (String) cldrRegionData.get(region);
						if (!CommonUtil.isEmpty(dispName)) {
							regionMap.put(region, dispName);
						}
					}
				}
			}
		}
		return regionMap;
	}

	/**
	 * Get CLDR locales region data
	 *
	 * @param locale
	 * @return
	 */
	private Map<String, Object> getRegionsData(String locale) {
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		String zipPath = CLDRConstants.LOCALE_ZIP_FILE_PATH;
		String fileName = "cldr-localenames-full-" + CLDRConstants.CLDR_VERSION + "/main/" + locale
				+ "/territories.json";
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject localeDataContents = JSONUtil.string2JSON(json);
		String node = "main." + locale + ".localeDisplayNames.territories";
		if (CommonUtil.isEmpty(JSONUtil.select(localeDataContents, node))) {
			return null;
		}
		String localeDataJson = JSONUtil.select(localeDataContents, node).toString();
		resultMap.putAll(JSONUtil.string2SortMap(localeDataJson));
		return resultMap;
	}

	/**
	 * Get CLDR locales region data
	 *
	 * @param locale
	 * @return
	 */
	private Map<String, Object> getLanguagesData(String locale) {
		String zipPath = CLDRConstants.LOCALE_ZIP_FILE_PATH;
		String fileName = "cldr-localenames-full-" + CLDRConstants.CLDR_VERSION + "/main/" + locale + "/languages.json";
		String json = CLDRUtils.readZip(fileName, zipPath);
		JSONObject languageDataContents = JSONUtil.string2JSON(json);
		String node = "main." + locale + ".localeDisplayNames.languages";
		if (CommonUtil.isEmpty(JSONUtil.select(languageDataContents, node))) {
			return null;
		}
		String localeDataJson = JSONUtil.select(languageDataContents, node).toString();
		return JSONUtil.string2SortMap(localeDataJson);
	}

	public static void localesExtract() {
		logger.info("Start to extract cldr locales data ... ");
		Map<String, String> allLocales = CLDRUtils.getAllCldrLocales();
		Map<String, String> terriContain = LocaleDataUtils.getInstance().getTerriContain();
		Map<String, Object> territoriesMap = null;
		Map<String, Object> languagesMap = null;
		Map<String, String> regionMap = null;
		Map<String, Object> langMap = null;
		for (String locale : allLocales.values()) {
			territoriesMap = new LinkedHashMap<String, Object>();
			CLDR cldr = new CLDR(locale);
			territoriesMap.put(Constants.LANGUAGE, cldr.getLanguage());
			regionMap = LocaleDataUtils.getInstance().getRegionList(locale, terriContain);
			String defaultRegionCode = LocaleDataUtils.getInstance().getDefaultRegionCode(locale, regionMap);
			territoriesMap.put(Constants.DEFAULT_REGION_CODE, defaultRegionCode);
			territoriesMap.put(Constants.TERRITORIES, regionMap);
			CLDRUtils.writePatternDataIntoFile(
					CLDRConstants.GEN_CLDR_LOCALEDATA_DIR + locale + File.separator + "territories.json",
					territoriesMap);
			languagesMap = new LinkedHashMap<String, Object>();
			langMap = LocaleDataUtils.getInstance().getLanguagesData(locale);
			languagesMap.put(Constants.DISPLAY_LANGUAGE, cldr.getLanguage());
			languagesMap.put(Constants.LANGUAGES, langMap);
			CLDRUtils.writePatternDataIntoFile(
					CLDRConstants.GEN_CLDR_LOCALEDATA_DIR + locale + File.separator + "languages.json", languagesMap);
		}
		logger.info("Extract cldr locales data complete!");
	}
}
