/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.LocaleCacheItem;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;
import com.vmware.vipclient.i18n.messages.dto.LocaleDTO;
import com.vmware.vipclient.i18n.messages.service.ProductService;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.JSONUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

public class LocalLocaleOpt implements LocaleOpt{

    private Logger logger = LoggerFactory.getLogger(LocalLocaleOpt.class);

	private LocaleDTO dto = null;

	public LocalLocaleOpt(LocaleDTO dto) {
		this.dto = dto;
	}

	@Override
    public void getSupportedLanguages(String locale, LocaleCacheItem cacheItem) {
		Map<String, String> supportedLanguageNames = new HashMap<String, String>();

		ProductService ps = new ProductService(dto);
		Set<String> supportedLanguages = ps.getSupportedLocales(DataSourceEnum.Bundle);

		if(supportedLanguages != null && !supportedLanguages.isEmpty()) {
			Map<String, String> languagesNames = getLanguagesNamesFromBundle(locale);
			if (languagesNames == null || languagesNames.isEmpty())
				return;
			for(String language : supportedLanguages){
				if(!ConstantsKeys.SOURCE.equalsIgnoreCase(language))
					supportedLanguageNames.put(language, languagesNames.get(language));
			}
		}
		if (!supportedLanguageNames.isEmpty()) {
			logger.debug("Found the supported languages from local bundle for product [{}], version [{}], locale [{}].\n", dto.getProductID(), dto.getVersion(), locale);
			supportedLanguageNames = JSONUtils.map2SortMap(supportedLanguageNames);
			cacheItem.set(supportedLanguageNames, System.currentTimeMillis());
		}else{
			logger.warn("Didn't find the supported languages from local bundle for product [{}], version [{}], locale [{}].\n", dto.getProductID(), dto.getVersion(), locale);
		}
    }

	private Map<String, String> getLanguagesNamesFromBundle(String locale) {
		logger.debug("Look for languages' names from local package bundle for locale [{}]!", locale);
		String normalizedLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
		logger.debug("Normalized locale for locale [{}] is [{}]", locale, normalizedLocale);
		if(normalizedLocale == null || normalizedLocale.isEmpty()) {
			logger.error("Normalized locale is empty for locale [{}]", locale);
			return null;
		}
		try {
			String languagesJsonStr = PatternUtil.getLanguageFromLib(normalizedLocale);
			JSONObject languagesData = new JSONObject(languagesJsonStr);
			if (languagesData != null) {
				logger.debug("Found the languages' names from local bundle for locale [{}].\n", locale);
				return ((JSONObject) languagesData.get(PatternKeys.LANGUAGES)).toMap().entrySet().stream()
					     .collect(Collectors.toMap(Map.Entry::getKey, e -> (String)e.getValue()));
			}else{
				logger.warn("Didn't find the languages' names from local bundle for locale [{}].\n", locale);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}

	public void getRegions(String locale, LocaleCacheItem cacheItem) {
		logger.debug("Look for regions from local bundle for locale [{}]!", locale);
		String normalizedLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
		logger.info("Normalized locale for locale [{}] is [{}]", locale, normalizedLocale);
		if(normalizedLocale == null || normalizedLocale.isEmpty())
			return;
		try {
			String regionsJsonStr = PatternUtil.getRegionFromLib(normalizedLocale);
			JSONObject jsonObject = new JSONObject(regionsJsonStr);
			Map<String, Object> regionsData = jsonObject.toMap();
			if (regionsData != null) {
				Map<String, String> territories = (Map<String, String>) regionsData.get(PatternKeys.TERRITORIES);
				if(territories != null) {
					logger.debug("Found the regions from local bundle for locale [{}].\n", locale);
					territories = JSONUtils.map2SortMap(territories);
					cacheItem.set(territories, System.currentTimeMillis());
				}else{
					logger.warn("Didn't find the regions from local bundle for locale [{}].\n", locale);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
}
