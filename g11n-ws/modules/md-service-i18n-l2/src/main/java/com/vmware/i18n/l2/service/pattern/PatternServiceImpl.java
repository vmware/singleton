/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.pattern;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

import java.util.*;

import javax.annotation.Resource;

import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.dto.LocaleDataDTO;
import com.vmware.vip.common.constants.ConstantsChar;
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
	 * @param scopeFilter a String for filtering out the pattern data, separated by commas and underline.
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked" })
	public Map<String, Object> getPattern(String locale, List<String> categoryList, String scopeFilter) throws VIPCacheException {
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
		Map<String, Object> categoryPatternMap = getCategories(categoryList, patternMap);
		filterScope(categoryPatternMap, scopeFilter);
		patternMap.put(ConstantsKeys.CATEGORIES, categoryPatternMap);
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
	 * @param scopeFilter a String for filtering out the pattern data, separated by commas and underline.
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> getPatternWithLanguageAndRegion(String language, String region, List<String> categoryList, String scopeFilter) throws VIPCacheException {
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
		if (!CommonUtil.isEmpty(patternMap) && !CommonUtil.isEmpty(patternMap.get(ConstantsKeys.CATEGORIES))) {
			filterScope((Map<String, Object>) patternMap.get(ConstantsKeys.CATEGORIES), scopeFilter);
		}
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
		Map<String, Object> patternMap = getPattern(language, Arrays.asList(category), null);
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
			suppMap.put(ConstantsKeys.NUMBERS, supplementMap.get(ConstantsKeys.NUMBERS));
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

	/**
	 * Filtering out the pattern data
	 * @param patternMap
	 * @param scopeFilter
	 */
	private void filterScope(Map<String, Object> patternMap, String scopeFilter) {
		if (CommonUtil.isEmpty(scopeFilter) || CommonUtil.isEmpty(patternMap)) {
			return;
		}

		// If scopeFilter starts with ^, it means deleting the data under the node
		if (scopeFilter.startsWith(ConstantsChar.REVERSE)) {
			scopeFilter = scopeFilter.substring(scopeFilter.indexOf(ConstantsChar.LEFT_PARENTHESIS) + 1, scopeFilter.indexOf(ConstantsChar.RIGHT_PARENTHESIS));
			Arrays.asList(scopeFilter.split(ConstantsChar.COMMA)).stream().forEach(scopeNode -> {
				List<String> scopeFilters = Arrays.asList(scopeNode.split(ConstantsChar.UNDERLINE));
				removeData(patternMap, scopeFilters, 0);
			});
		} else {
			Map<String, Object> newPatternMap = new HashMap<>();
			for (String scopeNode : Arrays.asList(scopeFilter.split(ConstantsChar.COMMA))) {
				List<String> scopeFilters = Arrays.asList(scopeNode.split(ConstantsChar.UNDERLINE));
				Map<String, Object> tempPatternMap = getData(patternMap, scopeFilters, 0);
				newPatternMap = mergePatternMap(newPatternMap, tempPatternMap, scopeFilters, 0);
			}
			patternMap.clear();
			patternMap.putAll(newPatternMap);
		}
	}

	private void removeData(Map<String, Object> patternMap, List<String> scopeFilters, Integer index) {
		String scopeFilter = scopeFilters.get(index);
		if (index == scopeFilters.size() - 1) {
			patternMap.remove(scopeFilter);
		} else if (!CommonUtil.isEmpty(patternMap.get(scopeFilter))) {
			patternMap = (Map<String, Object>) patternMap.get(scopeFilter);
			removeData(patternMap, scopeFilters, index + 1);
		}
	}

	/**
	 * Obtain the data of the corresponding node in the pattern according to scopeFilters
	 * @param originPatternMap
	 * @param scopeFilters
	 * @param index
	 * @return
	 */
	private Map<String, Object> getData(Map<String, Object> originPatternMap, List<String> scopeFilters, Integer index) {
		String scopeFilter = scopeFilters.get(index);
		Map<String, Object> patternMap = new HashMap<>();

		if (index == scopeFilters.size() - 1) {
			patternMap.put(scopeFilter, originPatternMap.get(scopeFilter));
		} else if (!CommonUtil.isEmpty(originPatternMap.get(scopeFilter))) {
			originPatternMap = (Map<String, Object>) originPatternMap.get(scopeFilter);
			patternMap.put(scopeFilter, getData(originPatternMap, scopeFilters, index + 1));
		}
		return patternMap;
	}

	/**
	 * Merging pattern data
	 * @param originPatternMap
	 * @param newPatternMap
	 * @param scopeFilters
	 * @param index
	 * @return
	 */
	private Map<String, Object> mergePatternMap(Map<String, Object> originPatternMap, Map<String, Object> newPatternMap,
												List<String> scopeFilters, Integer index) {
		Map<String, Object> patternMap = new HashMap<>(originPatternMap);
		String scopeFilter = scopeFilters.get(index);

		if (!CommonUtil.isEmpty(originPatternMap.get(scopeFilter))) {
			originPatternMap = (Map<String, Object>) originPatternMap.get(scopeFilter);
			newPatternMap = (Map<String, Object>) newPatternMap.get(scopeFilter);
			patternMap.put(scopeFilter, mergePatternMap(originPatternMap, newPatternMap, scopeFilters, index + 1));
		} else {
			patternMap.putAll(newPatternMap);
		}

		return patternMap;
	}
}
