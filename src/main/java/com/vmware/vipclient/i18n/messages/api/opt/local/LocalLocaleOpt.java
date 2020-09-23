/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;
import com.vmware.vipclient.i18n.messages.dto.LocaleDTO;
import com.vmware.vipclient.i18n.messages.service.FormattingCacheService;
import com.vmware.vipclient.i18n.messages.service.ProductService;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

public class LocalLocaleOpt implements LocaleOpt{

    private Logger logger = LoggerFactory.getLogger(LocalLocaleOpt.class);
    private static final String BUNDLE_PREFIX = "messages_";
	private static final String BUNDLE_SUFFIX = ".json";

	private LocaleDTO dto = null;

	public LocalLocaleOpt(LocaleDTO dto) {
		this.dto = dto;
	}

	@Override
    public Map<String, String> getSupportedLanguages(String locale) {
		ProductService ps = new ProductService(dto);
		Set<String> supportedLanguages = ps.getSupportedLanguageTags();
		Map<String, String> supportedLanguageNames = new HashMap<String, String>();


		if(supportedLanguages != null && !supportedLanguages.isEmpty()) {
			Map<String, String> languagesNames = getLanguagesNamesFromCLDR(locale);
			if (languagesNames == null || languagesNames.isEmpty())
				return supportedLanguageNames;
			for(String language : supportedLanguages){
				if(!ConstantsKeys.SOURCE.equalsIgnoreCase(language))
					supportedLanguageNames.put(language, (String) languagesNames.get(language));
			}
		}
		return supportedLanguageNames;
    }

    private Map<String, String> getLanguagesNamesFromCLDR(String locale){
		Map<String, String> languagesNames = null;
		logger.debug("Look for languages' names from cache for locale [{}]!", locale);
		FormattingCacheService formattingCacheService = new FormattingCacheService();
		languagesNames = formattingCacheService.getLanguagesNames(locale);// key
		if (languagesNames != null) {
			logger.debug("Find languages' names from cache for locale [{}]!", locale);
			return languagesNames;
		}
		languagesNames = getLanguagesNamesFromBundle(locale);
		if (languagesNames != null) {
			logger.debug("Find the languages' names from local bundle for locale [{}].\n", locale);// [datetime] and
			formattingCacheService.addLanguagesNames(locale, languagesNames);
			logger.debug("Languages' names are cached for locale [{}]!\n\n", locale);
			return languagesNames;
		}
		return null;
	}

	private JSONObject getLanguagesNamesFromBundle(String locale) {
		logger.debug("Look for languages' names from local package bundle for locale [{}]!", locale);
		String normalizedLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
		logger.info("Normalized locale for locale [{}] is [{}]", locale, normalizedLocale);
		if(normalizedLocale == null || normalizedLocale.isEmpty())
			return null;
        try {
            String languagesJsonStr = PatternUtil.getLanguageFromLib(normalizedLocale);
			JSONObject languagesData = (JSONObject) new JSONParser().parse(languagesJsonStr);
            return (JSONObject) languagesData.get(PatternKeys.LANGUAGES);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
        return null;
	}

	public JSONObject getRegions(String locale) {
		logger.debug("Look for regions from local bundle for locale [{}]!", locale);
		String normalizedLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
		logger.info("Normalized locale for locale [{}] is [{}]", locale, normalizedLocale);
		if(normalizedLocale == null || normalizedLocale.isEmpty())
			return null;
        try {
		    String regionsJsonStr = PatternUtil.getRegionFromLib(normalizedLocale);
			JSONObject regionsData = (JSONObject) new JSONParser().parse(regionsJsonStr);
            return (JSONObject) regionsData.get(PatternKeys.TERRITORIES);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return null;
	}
}
