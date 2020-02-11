/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.locale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.i18n.utils.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ibm.icu.impl.LocaleUtility;
import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.utils.LocaleUtils;
import com.vmware.vip.core.messages.service.product.IProductService;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

/**
 * This class is used to convert a string to a locale object
 */
@Service
public class LocaleService implements ILocaleService {
    private static final String LANGUAGE_STR="languages";
	private static final String CONTEXT_TRANSFORMS = "contextTransforms";
	private static final String DISPLAY_NAME_SENTENCE_BEGINNING = "displayName-sentenceBeginning";
	private static final String DISPLAY_NAME_UI_LIST = "displayName-uiListOrMenu";
	private static final String DISPLAY_NAME_STANDALONE = "displayName-standalone";
	private static final String STAND_ALONE = "stand-alone";
	private static final String UI_LIST_OR_MENU = "uiListOrMenu";
	private static final String NO_CHANGES = "no-change";
	private static final Logger logger = LoggerFactory.getLogger(LocaleService.class.getName());

	@Autowired
	IProductService productService;

	/**
	 * A function to convert a string of the form aa_BB_CC to a locale object
	 *
	 * @param locale
	 *            A string representing a specific locale in [lang]_[country
	 *            (region)] format. e.g., ja_JP, zh_CN
	 * @return Returns a well-formed IETF BCP 47 language tag representing this
	 *         locale
	 */
	public String getLocaleFromName(String locale) {
		return LocaleUtility.getLocaleFromName(locale).toLanguageTag();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DisplayLanguageDTO> getDisplayNamesFromCLDR(String productName, String version, String dispLanguage)
			throws Exception {
		LanguagesFileParser languagesParser = new LanguagesFileParser();
		List<DisplayLanguageDTO> dtoList = new ArrayList<DisplayLanguageDTO>();
		DisplayLanguageDTO dto = null;
		Map<String, String> languagesMap = null;
		Map<String, Object> jsonMap = null;
		List<String> languageList = this.productService.getSupportedLanguageList(productName, version);
		if (languageList.size() == 0 || languageList == null){
			return dtoList;
		}
		String cacheKey = dispLanguage;
		if (StringUtils.isEmpty(dispLanguage)) {
			cacheKey = productName + "_" + version + "_" + languageList.size();
		}
		jsonMap = TranslationCache3.getCachedObject(CacheName.LANGUAGE, cacheKey, HashMap.class);
		if (jsonMap == null) {
			logger.info("get data from file");
			if (StringUtils.isEmpty(dispLanguage)) {
				Map<String, String> tmp = new HashMap<String, String>();
				Map<String, String> tmp1 = null;
				Map<String, Object> tmp2 = null;
				for (String language : languageList) {
					tmp2 = languagesParser.getDisplayNames(language);
					if(tmp2 != null && tmp2.get(LANGUAGE_STR) != null){
						tmp1 = (Map<String, String>) tmp2.get(LANGUAGE_STR);
						if(tmp1 != null) {
							tmp.put(language, tmp1.get(language) == null ? "" : tmp1.get(language));
						}else {
							tmp.put(language, "");
						}
					}else{
						continue;
					}
				}
				if (tmp.size() == 0) {
					return dtoList;
				}
				jsonMap = new HashMap<String, Object>();
				jsonMap.put(LANGUAGE_STR, tmp);
			} else {
				logger.info("get data from cache");
				// VIP-2001:[Get LanguageList API]Can't parse the language(i.e. en-US) which in default content json file.
				String locale = dispLanguage.replace("_", "-");
				String newLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
				jsonMap = languagesParser.getDisplayNames(newLocale);
				if (jsonMap == null || jsonMap.get(LANGUAGE_STR) == null) {
					return dtoList;
				}
			}
			TranslationCache3.addCachedObject(CacheName.LANGUAGE, cacheKey,HashMap.class,  (HashMap<String, Object>)jsonMap);
		}
		languagesMap = (Map<String, String>) jsonMap.get(LANGUAGE_STR);

		for (String language : languageList) {
			Map<String, String> displayNameMap = getDisplayNameMap(language, languagesParser, languagesMap.get(language));
			language = LocaleUtils.normalizeToLanguageTag(language);
			dto = new DisplayLanguageDTO();
			dto.setDisplayName(languagesMap.get(language) == null ? "" : languagesMap.get(language));
			dto.setDisplayName_sentenceBeginning(StringUtils.isEmpty(displayNameMap.get(DISPLAY_NAME_SENTENCE_BEGINNING)) ? dto.getDisplayName() : displayNameMap.get(DISPLAY_NAME_SENTENCE_BEGINNING));
			dto.setDisplayName_uiListOrMenu(StringUtils.isEmpty(displayNameMap.get(DISPLAY_NAME_UI_LIST)) ? dto.getDisplayName() : displayNameMap.get(DISPLAY_NAME_UI_LIST));
			dto.setDisplayName_standalone(StringUtils.isEmpty(displayNameMap.get(DISPLAY_NAME_STANDALONE)) ? dto.getDisplayName() : displayNameMap.get(DISPLAY_NAME_STANDALONE));
			dto.setLanguageTag(language);
			dtoList.add(dto);
		}
		return dtoList;
	}

	private Map<String, String> getDisplayNameMap(String language, LanguagesFileParser languagesParser, String displayName) {
		Map<String, Object> tmpContext = languagesParser.getContextTransforms(language);
		Map<String, String> displayNameMap = new HashMap<>();
		displayNameMap.put(DISPLAY_NAME_UI_LIST, "");
		displayNameMap.put(DISPLAY_NAME_STANDALONE, "");
		displayNameMap.put(DISPLAY_NAME_SENTENCE_BEGINNING, tittleCase(displayName));
		if (tmpContext != null && tmpContext.get(CONTEXT_TRANSFORMS) != null) {
			Map<String, Map<String, String>> contextTransformMap = (Map<String, Map<String, String>>) tmpContext.get(CONTEXT_TRANSFORMS);
			if (contextTransformMap != null && contextTransformMap.get(LANGUAGE_STR) != null) {
				Map<String, String> languageMap = contextTransformMap.get(LANGUAGE_STR);
				if (languageMap.get(STAND_ALONE) != null) {
					displayNameMap.put(DISPLAY_NAME_STANDALONE, languageMap.get(STAND_ALONE).equals(NO_CHANGES) ? displayName : tittleCase(displayName));
				}
				if (languageMap.get(UI_LIST_OR_MENU) != null) {
					displayNameMap.put(DISPLAY_NAME_UI_LIST, languageMap.get(UI_LIST_OR_MENU).equals(NO_CHANGES) ? displayName : tittleCase(displayName));
				}
			}
		}
		return displayNameMap;
	}

	private String tittleCase(String language) {
		if (StringUtils.isEmpty(language)) {
			return language;
		}

		char[] chars = language.toCharArray();
		if (chars[0] >= 'a' && chars[0] <= 'z') {
			chars[0] = (char) (chars[0] - 32);
		}
		return String.valueOf(chars);
	}

	@Override
	public List<TerritoryDTO> getTerritoriesFromCLDR(String languageList) throws Exception {
		TerritoriesFileParser territoriesParser = new TerritoriesFileParser();
		List<TerritoryDTO> territoryList = new ArrayList<TerritoryDTO>();
		TerritoryDTO territory = null;
		String[] langArr = languageList.split(",");
		for (String lang : langArr) {
			String locale = lang.replace("_", "-");
			lang = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap).toLowerCase();
			logger.info("get data from cache");
			territory = TranslationCache3.getCachedObject(CacheName.REGION, lang, TerritoryDTO.class);
			if (territory == null) {
				logger.info("cache is null, get data from file");
				territory = territoriesParser.getTerritoriesByLanguage(lang);
				if (territory.getTerritories() != null) {
					TranslationCache3.addCachedObject(CacheName.REGION, lang, TerritoryDTO.class, territory);
				}
			}
			territoryList.add(territory);
		}
		return territoryList;
	}

}
