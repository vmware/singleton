/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.pattern;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

import java.util.*;

import javax.annotation.Resource;

import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.dto.LocaleDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.vmware.i18n.l2.dao.pattern.IPatternDao;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.utils.JSONUtils;
import org.springframework.util.StringUtils;

@Service
public class PatternServiceImpl implements IPatternService {

	private static final Logger logger = LoggerFactory.getLogger(PatternServiceImpl.class.getName());

	private static List<String> specialCategories = Arrays.asList(ConstantsKeys.PLURALS, ConstantsKeys.DATE_FIELDS);
	private static List<String> otherCategories = Arrays.asList(ConstantsKeys.DATES, ConstantsKeys.NUMBERS, ConstantsKeys.MEASUREMENTS, ConstantsKeys.CURRENCIES);

	@Resource
	private IPatternDao patternDao;

	/**
	 *
	 * @param locale
	 * @param categoryList dates,numbers,plurals,measurements,currencies, split by ','
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked" })
	public Map<String, Object> getPattern(String locale, List<String> categoryList) throws VIPCacheException {
		/**
		 * VIP-1665:[i18n pattern API] it always return CN/TW patten
		 * as long as language starts with zh-Hans/zh-Hant.
		 * @param locale
		 */
		locale = locale.replace("_", "-");
		String newLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
		if (CommonUtil.isEmpty(newLocale)){
			logger.info("Invalid locale!");
			return buildPatternMap(locale);
		}

		locale = newLocale;

		Map<String, Object> patternMap = null;
		String patternJson = "";
		patternJson = TranslationCache3.getCachedObject(CacheName.PATTERN, locale, String.class);
	
		if (StringUtils.isEmpty(patternJson)) {
			logger.info("get pattern data from file");
			patternJson = patternDao.getPattern(locale, null);
			if (StringUtils.isEmpty(patternJson)) {
				logger.info("file data don't exist");
				return buildPatternMap(locale);
			}
			TranslationCache3.addCachedObject(CacheName.PATTERN, locale, String.class, patternJson);
		}
		logger.info("get pattern data from cache");
		patternMap = JSONUtils.getMapFromJson(patternJson);
		if (StringUtils.isEmpty(patternMap.get(ConstantsKeys.REGION))) {
			String regionJson = PatternUtil.getRegionFromLib(locale.replace("_", "-"));
			if (StringUtils.hasLength(regionJson)) {
				Object region = JSONUtils.getMapFromJson(regionJson).get(ConstantsKeys.DEFAULT_REGION_CODE);
				patternMap.put(ConstantsKeys.REGION, region.toString());
			}
		}
		patternMap.put(ConstantsKeys.CATEGORIES, getCategories(categoryList, patternMap));
		return patternMap;
	}

	private Map<String, Object> buildPatternMap(String locale) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(ConstantsKeys.LOCALEID, locale);
		map.put(ConstantsKeys.CATEGORIES, null);
		return map;
	}

	/**
	 * Get i18n pattern with language, region and scope parameter.
	 *
	 * @param language
	 * @param region
	 * @param categoryList dates,numbers,plurals,measurements,currencies, split by ','
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> getPatternWithLanguageAndRegion(String language, String region, List<String> categoryList) throws VIPCacheException {
		logger.info("Get i18n pattern with language: {},region: {} and categories: {}", language, region, categoryList);
		Map<String, Object> patternMap = null;
		String patternJson = "";
		language = language.replace("_", "-");
		LocaleDataDTO resultData = CommonUtil.getLocale(language, region);
		String locale = resultData.getLocale();
		logger.info(locale);
		patternJson = TranslationCache3.getCachedObject(CacheName.PATTERN, locale, String.class);
		if (StringUtils.isEmpty(patternJson)) {
			logger.info("get pattern data from file");
			patternJson = patternDao.getPattern(locale, null);
			if (StringUtils.isEmpty(patternJson)) {
				logger.info("file data don't exist");
				return buildPatternMap(language, region, patternJson, categoryList, resultData);
			}
			TranslationCache3.addCachedObject(CacheName.PATTERN, locale, String.class, patternJson);
		}
		logger.info("get pattern data from cache");
		patternMap = buildPatternMap(language, region, patternJson, categoryList, resultData);
		logger.info("The result pattern: {}", patternMap);
		logger.info("Get i18n pattern successful");
		return patternMap;
	}

	/**
	 * Build pattern
	 *
	 * @param patternJson
	 * @param categoryList
	 * @return
	 */
	private Map<String, Object> buildPatternMap(String language, String region, String patternJson, List<String> categoryList, LocaleDataDTO localeDataDTO) throws VIPCacheException {
		Map<String, Object> patternMap = new LinkedHashMap<>();
		Map<String, Object> categoriesMap = new LinkedHashMap<>();
		if (StringUtils.isEmpty(patternJson)) {
			patternMap.put(ConstantsKeys.LOCALEID, "");
			patternMap.put(ConstantsKeys.IS_EXIST_PATTERN, false);
			for (String category : categoryList) {
				categoriesMap.put(category, null);
			}
		} else {
			patternMap = JSONUtils.getMapFromJson(patternJson);
			categoriesMap = getCategories(categoryList, patternMap);
			patternMap.put(ConstantsKeys.IS_EXIST_PATTERN, true);
		}

		if (categoryList.contains(ConstantsKeys.PLURALS)) {
			handleSpecialCategory(ConstantsKeys.PLURALS, language, categoriesMap);
		}

		if (categoryList.contains(ConstantsKeys.DATE_FIELDS)) {
			handleSpecialCategory(ConstantsKeys.DATE_FIELDS, language, categoriesMap);
			// As long as the value of dateFields exist, the response needs to return its value.
			if (null != categoriesMap.get(ConstantsKeys.DATE_FIELDS)) {
				patternMap.put(ConstantsKeys.IS_EXIST_PATTERN, true);
			}
		}

		if (!localeDataDTO.isDisplayLocaleID()) {
			patternMap.put(ConstantsKeys.LOCALEID, "");
		}

		// fix issue: https://github.com/vmware/singleton/issues/311
		if (!Collections.disjoint(categoryList, specialCategories)) {
			if (Collections.disjoint(categoryList, otherCategories)) {
				patternMap.put(ConstantsKeys.LOCALEID, language);
			} else if (!language.equals(localeDataDTO.getLocale())) {
				patternMap.put(ConstantsKeys.LOCALEID, "");
			}
		}

		patternMap.put(ConstantsKeys.LANGUAGE, language);
		patternMap.put(ConstantsKeys.REGION, region);
		patternMap.put(ConstantsKeys.CATEGORIES, categoriesMap);
		return patternMap;
	}

	private void handleSpecialCategory(String category, String language, Map<String, Object> categoriesMap) throws VIPCacheException {
		categoriesMap.put(category, null);
		Map<String, Object> patternMap = getPattern(language, Arrays.asList(category));
		if (null != patternMap.get(ConstantsKeys.CATEGORIES)) {
			Map<String, Object> categoryMap = (Map<String, Object>) patternMap.get(ConstantsKeys.CATEGORIES);
			if (null != categoryMap.get(category)) {
				categoriesMap.put(category, categoryMap.get(category));
			}
		}
	}

	/**
	 *  Getting categories according to pattern
	 * @param categoryList
	 * @param patternMap
	 * @return
	 */
	private Map<String, Object> getCategories(List<String> categoryList, Map<String, Object> patternMap){
		Map<String, Object> resultMap = new LinkedHashMap<>();
		Map<String, Object> categoriesMap = (Map<String, Object>) patternMap.get(ConstantsKeys.CATEGORIES);
		Map<String, Object> supplementMap = (Map<String, Object>) categoriesMap.get(ConstantsKeys.SUPPLEMENT);
		Map<String, Object> suppMap = new HashMap<>();
		for (String cat : categoryList) {
			suppMap.put(cat, supplementMap.get(cat));
		}

		if (categoryList.contains(ConstantsKeys.CURRENCIES) && !categoryList.contains(ConstantsKeys.NUMBERS)) {
			categoryList.add(ConstantsKeys.NUMBERS);
		}

		for (String cat : categoryList) {
			if (!CommonUtil.isEmpty(categoriesMap.get(cat))) {
				resultMap.put(cat, categoriesMap.get(cat));
			}
		}
		//add the supplement data
		resultMap.put(ConstantsKeys.SUPPLEMENT, suppMap);

		return resultMap;
	}
}
