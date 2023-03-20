/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.pattern.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.i18n.common.CLDRConstants;
import com.vmware.i18n.common.Constants;
import com.vmware.i18n.pattern.dao.IPatternDao;
import com.vmware.i18n.pattern.dao.impl.PatternDaoImpl;
import com.vmware.i18n.pattern.service.IPatternService;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.i18n.utils.JSONUtil;
import com.vmware.i18n.utils.LocalJSONReader;
import com.vmware.i18n.utils.timezone.TimeZoneName;

@SuppressWarnings("unchecked")
public class PatternServiceImpl implements IPatternService {

	private static Logger logger = LoggerFactory.getLogger(PatternServiceImpl.class);
	public static Map<String, Object> likelySubtagMap = null;
	public static Map<String, String> localePathMap = null;
	public static Map<String, String> regionMap = null;
	public static Map<String, Object> localeAliasesMap = null;
	public static Map<String, Object> pluralsMap = null;
	public static Map<String, Object> languageDataMap = null;
	IPatternDao dao = new PatternDaoImpl();
	static {
		String result = "";
		String regionResult = "";
		String localeAliases = "";
		String plurals = "";
		String languageData = "";
		if (CLDRConstants.JSON_PATH.lastIndexOf(".jar") > 0) {
			result = LocalJSONReader.readJarJsonFile(CLDRConstants.JSON_PATH, CLDRConstants.PARSE_DATA);// jar
			regionResult = LocalJSONReader.readJarJsonFile(CLDRConstants.JSON_PATH,
					CLDRConstants.REGION_LANGUAGES_PATH);
			localeAliases = LocalJSONReader.readJarJsonFile(CLDRConstants.JSON_PATH, CLDRConstants.LOCALE_ALIASES_PATH);
			plurals = LocalJSONReader.readJarJsonFile(CLDRConstants.JSON_PATH, CLDRConstants.PLURALS_PATH);
			languageData = LocalJSONReader.readJarJsonFile(CLDRConstants.JSON_PATH, CLDRConstants.LANGUAGE_DATA_PATH);
		} else {
			result = LocalJSONReader.readLocalJSONFile(CLDRConstants.RESOURCES_PATH + CLDRConstants.PARSE_DATA);// local
			regionResult = LocalJSONReader
					.readLocalJSONFile(CLDRConstants.RESOURCES_PATH + CLDRConstants.REGION_LANGUAGES_PATH);
			localeAliases = LocalJSONReader
					.readLocalJSONFile(CLDRConstants.RESOURCES_PATH + CLDRConstants.LOCALE_ALIASES_PATH);
			plurals = LocalJSONReader.readLocalJSONFile(CLDRConstants.RESOURCES_PATH + CLDRConstants.PLURALS_PATH);
			languageData = LocalJSONReader
					.readLocalJSONFile(CLDRConstants.RESOURCES_PATH + CLDRConstants.LANGUAGE_DATA_PATH);
		}
		likelySubtagMap = (Map<String, Object>) JSONUtil.getMapFromJson(result).get(Constants.LIKELY_SUBTAG);
		localePathMap = (Map<String, String>) JSONUtil.getMapFromJson(result).get(Constants.LOCALE_PATH);
		regionMap = (Map<String, String>) JSONUtil.getMapFromJson(regionResult).get(Constants.REGION_INFO);
		localeAliasesMap = (Map<String, Object>) JSONUtil.getMapFromJson(localeAliases).get(Constants.LANGUAGE_ALIASES);
		pluralsMap = (Map<String, Object>) JSONUtil.getMapFromJson(plurals).get(Constants.PLURAL_INFO);
		languageDataMap = (Map<String, Object>) JSONUtil.getMapFromJson(languageData).get(Constants.LANGUAGE_DATA);
	}

	/**
	 * Get i18n pattern by specific locale and categories
	 *
	 * @param locale     A string specified by the product to represent a specific
	 *                   locale. e.g. de, fr.
	 * @param categories The pattern categories: dates, numbers, plurals,
	 *                   measurements, currencies. use ',' to split. e.g.
	 *                   "dates,numbers,plurals", if null, will return all category
	 *                   pattern.
	 * @return pattern
	 */
	public String getPattern(String locale, String categories) {
		if (CommonUtil.isEmpty(locale)) {
			return "";
		}
		String tmpLocale = locale.replace("_", "-");
		String[] arr = tmpLocale.split("-");
		if (CommonUtil.isEmpty(categories)) {
			categories = CLDRConstants.ALL_CATEGORIES;
		}

		String pathLocale = CommonUtil.getPathLocale(tmpLocale, localePathMap, likelySubtagMap);
		if (CommonUtil.isEmpty(pathLocale))
			return "";

		Map<String, Object> tmpMap = getCategoriesMap(pathLocale, categories);
		Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
		resultMap.put("localeID", locale);
		resultMap.put("language", arr[0]);
		resultMap.put("region", parseRegion(arr));
		resultMap.put("categories", tmpMap);
		try {
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(resultMap);
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
			return "";
		}
	}

	private String parseRegion(String[] arr) {
		String region = "";
		switch (arr.length) {
		case 2:
			if (arr[1].length() == 2) {
				region = arr[1].toUpperCase();
			}
			break;
		case 3:
			region = arr[2].toUpperCase();
			break;
		}
		return region;
	}

	/**
	 * Get categories
	 *
	 * @param categories
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, Object> getCategoriesMap(String locale, String categories) {
		// get supplement data by categories
		Map<String, Object> suppleMap = new HashMap<>();
		IPatternDao dao = new PatternDaoImpl();
		String[] cateList = categories.split(",");

		String resourcePath = CLDRConstants.RESOURCES_PATH;
		if (CLDRConstants.JSON_PATH.lastIndexOf(".jar") > 0) {
			resourcePath = CLDRConstants.JSON_PATH;
		}

		for (String cat : cateList) {
			String filePath = MessageFormat.format(CLDRConstants.SUPPLEMENTAL_PATH, cat);
			String suppleData = dao.getPattern(resourcePath, filePath);
			if (!CommonUtil.isEmpty(suppleData)) {
				suppleMap.put(cat, JSONUtil.string2SortMap(suppleData));
			}
		}

		// get pattern data
		String patternJson = dao.getPattern(resourcePath,
				MessageFormat.format(CLDRConstants.PATTERN_JSON_PATH, locale));
		Map<String, Object> categMap = (Map<String, Object>) JSONUtil.getMapFromJson(patternJson)
				.get(Constants.CATEGORIES);
		List<String> cates = new ArrayList(Arrays.asList(cateList));
		if (cates.contains(Constants.CURRENCIES) && !cates.contains(Constants.NUMBERS)) {
			cates.add(Constants.NUMBERS);
		}

		if (cates.contains(Constants.DATE_FIELDS)) {
			// get pattern data
			String dateFieldsJson = dao.getPattern(resourcePath,
					MessageFormat.format(CLDRConstants.DATE_FIELDS_JSON_PATH, locale));
			categMap.put(Constants.DATE_FIELDS, JSONUtil.getMapFromJson(dateFieldsJson).get(Constants.DATE_FIELDS));
		}
		Map<String, Object> tmpMap = new LinkedHashMap<>();
		for (String cat : cates) {
			if (!CommonUtil.isEmpty(categMap.get(cat))) {
				tmpMap.put(cat, categMap.get(cat));
			}
		}
		tmpMap.put(Constants.SUPPLEMENTAL, suppleMap);
		return tmpMap;
	}

	/*
	 * (non-Javadoc) * @see
	 * com.vmware.i18n.pattern.service.IPatternService#getTimeZoneName(java.lang.
	 * String)
	 */
	@Override
	public TimeZoneName getTimeZoneName(String locale, boolean defaultTerritory) {
		// TODO Auto-generated method stub
		if (CommonUtil.isEmpty(locale)) {
			return null;
		}
		String tmpLocale = locale.replace("_", "-");

		String pathLocale = CommonUtil.getPathLocale(tmpLocale, localePathMap, likelySubtagMap);
		if (CommonUtil.isEmpty(pathLocale))
			return null;
		String patternStr = dao.getPattern(CLDRConstants.JSON_PATH,
				MessageFormat.format(CLDRConstants.DATE_TIMEZONENAME_JSON_PATH, pathLocale));
		TimeZoneName timeZoneObj = null;
		try {
			ObjectMapper obm = new ObjectMapper();
			obm.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			timeZoneObj = obm.readValue(patternStr, TimeZoneName.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		if (defaultTerritory) {
			List<LinkedHashMap<String,Object>>  cldrMetaZone = new ArrayList<LinkedHashMap<String,Object>>();
			for (LinkedHashMap<String,Object> metaZone : timeZoneObj.queryMetaZones()) {
				if (metaZone.get(Constants.TIMEZONENAME_METAZONE_MAPZONES) != null) {
					cldrMetaZone.add(metaZone);
				}
			}
			timeZoneObj.resetMetaZones(cldrMetaZone);
		}
		return timeZoneObj;
	}
}
