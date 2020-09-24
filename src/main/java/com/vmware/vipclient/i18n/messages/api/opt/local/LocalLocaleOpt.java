/*
 * Copyright 2019 VMware, Inc.
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
import com.vmware.vipclient.i18n.messages.service.FormattingCacheService;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.JSONUtils;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

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
		List<String> supportedLanguages = DataSourceEnum.Bundle.createProductOpt(dto)
				.getSupportedLocales();
		if(supportedLanguages != null && !supportedLanguages.isEmpty()) {
			Map<String, String> languagesNames = getLanguagesNamesFromCLDR(locale);
			if (languagesNames == null || languagesNames.isEmpty())
				return;
			for(String language : supportedLanguages){
				if(!ConstantsKeys.SOURCE.equalsIgnoreCase(language))
					supportedLanguageNames.put(language, languagesNames.get(language));
			}
		}
		if (!supportedLanguageNames.isEmpty()) {
			logger.debug("Find the supported languages from local bundle for product [{}], version [{}], locale [{}].\n", dto.getProductID(), dto.getVersion(), locale);
			supportedLanguageNames = JSONUtils.map2SortMap(supportedLanguageNames);
			cacheItem.set(supportedLanguageNames, System.currentTimeMillis());
		}else{
			logger.debug("Doesn't find the supported languages from local bundle for product [{}], version [{}], locale [{}].\n", dto.getProductID(), dto.getVersion(), locale);
			cacheItem.set(System.currentTimeMillis());
		}
    }

    private Map<String, String> getLanguagesNamesFromCLDR(String locale){
		LocaleCacheItem languagesNames = null;
		logger.debug("Look for languages' names from cache for locale [{}]!", locale);
		FormattingCacheService formattingCacheService = new FormattingCacheService();
		languagesNames = formattingCacheService.getLanguagesNames(locale);// key
		if (languagesNames != null) {
			if (languagesNames.isExpired()) { // cacheItem has expired
				// Update the cache in a separate thread
				populateLanguagesCache(locale, languagesNames);
			}
			logger.debug("Find languages' names from cache for locale [{}]!", locale);
			return languagesNames.getCachedData();
		}
		languagesNames = new LocaleCacheItem();
		getLanguagesNamesFromBundle(locale, languagesNames);
		formattingCacheService.addLanguagesNames(locale, languagesNames);
		logger.debug("Languages' names are cached for locale [{}]!\n\n", locale);
		return languagesNames.getCachedData();
	}

	private void getLanguagesNamesFromBundle(String locale, LocaleCacheItem cacheItem) {
		logger.debug("Look for languages' names from local package bundle for locale [{}]!", locale);
		String normalizedLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
		logger.info("Normalized locale for locale [{}] is [{}]", locale, normalizedLocale);
		if(normalizedLocale == null || normalizedLocale.isEmpty())
			return;
		try {
			String languagesJsonStr = PatternUtil.getLanguageFromLib(normalizedLocale);
			Map<String, Object> languagesData = (Map<String, Object>) new JSONParser().parse(languagesJsonStr);
			if (languagesData != null) {
				logger.debug("Find the languages' names from local bundle for locale [{}].\n", locale);
				cacheItem.set((Map<String, String>) languagesData.get(PatternKeys.LANGUAGES), System.currentTimeMillis());
			}else{
				logger.debug("Doesn't find the languages' names from local bundle for locale [{}].\n", locale);
				cacheItem.set(System.currentTimeMillis());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public void getRegions(String locale, LocaleCacheItem cacheItem) {
		logger.debug("Look for regions from local bundle for locale [{}]!", locale);
		String normalizedLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
		logger.info("Normalized locale for locale [{}] is [{}]", locale, normalizedLocale);
		if(normalizedLocale == null || normalizedLocale.isEmpty())
			return;
		try {
			String regionsJsonStr = PatternUtil.getRegionFromLib(normalizedLocale);
			Map<String, Object> regionsData = (Map<String, Object>) new JSONParser().parse(regionsJsonStr);
			if (regionsData != null) {
				Map<String, String> territories = (Map<String, String>) regionsData.get(PatternKeys.TERRITORIES);
				if(territories != null) {
					logger.debug("Find the regions from local bundle for locale [{}].\n", locale);
					territories = JSONUtils.map2SortMap(territories);
					cacheItem.set(territories, System.currentTimeMillis());
				}else{
					logger.debug("Doesn't find the regions from local bundle for locale [{}].\n", locale);
					cacheItem.set(System.currentTimeMillis());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void populateLanguagesCache(String locale, LocaleCacheItem cacheItem) {
		Callable<LocaleCacheItem> callable = () -> {
			try {

				// Pass cacheItem to getMessages so that:
				// 1. A previously stored etag, if any, can be used for the next HTTP request.
				// 2. CacheItem properties such as etag, timestamp and maxAgeMillis can be refreshed
				// 	 with new properties from the next HTTP response.
				getLanguagesNamesFromBundle(locale, cacheItem);
				return cacheItem;
			} catch (Exception e) {
				// To make sure that the thread will close
				// even when an exception is thrown
				return null;
			}
		};
		FutureTask<LocaleCacheItem> task = new FutureTask<LocaleCacheItem>(callable);
		Thread thread = new Thread(task);
		thread.start();
	}
}
