/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.locale;

import com.ibm.icu.impl.LocaleUtility;
import com.vmware.i18n.cldr.CLDR;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.utils.LocaleUtils;
import com.vmware.vip.core.messages.service.product.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

/**
 * This class is used to convert a string to a locale object
 */
@Service
public class LocaleService implements ILocaleService {
	private static final String CONTEXT_TRANSFORMS = "contextTransforms";
	private static final String DISPLAY_NAME_SENTENCE_BEGINNING = "displayName-sentenceBeginning";
	private static final String DISPLAY_NAME_UI_LIST = "displayName-uiListOrMenu";
	private static final String DISPLAY_NAME_STANDALONE = "displayName-standalone";
	private static final String STAND_ALONE = "stand-alone";
	private static final String UI_LIST_OR_MENU = "uiListOrMenu";
	private static final String NO_CHANGES = "no-change";
	private static final Logger logger = LoggerFactory.getLogger(LocaleService.class.getName());
	private static final String TERRITORY_REGIONS = "terr-region";
	private static final String TERRITORY_CITIES = "terr-city";

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
		List<String> supportedLanguageList = this.productService.getSupportedLanguageList(productName, version);
		if (supportedLanguageList.size() == 0 || supportedLanguageList == null) {
			return null;
		}

		String normalizedDispLanguage = "";
		String cacheKey = "";
		if (StringUtils.isEmpty(dispLanguage)) {
			cacheKey = productName + "_" + version;
		} else {
			String locale = dispLanguage.replace("_", "-");
			normalizedDispLanguage = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
			cacheKey = productName + "_" + version + "_" + normalizedDispLanguage;
		}

		ArrayList<DisplayLanguageDTO> dtoList = TranslationCache3.getCachedObject(CacheName.LANGUAGE, cacheKey, ArrayList.class);
		if (dtoList == null) {
			logger.info("get data from file");
			dtoList = new ArrayList<DisplayLanguageDTO>();
			Map<String, String> supportedLanguageMap = supportedLanguageList.stream().collect(Collectors.toMap(language -> language, language -> LocaleUtils.normalizeToLanguageTag(language)));
			Map<String, String> supportedLanguageNameMap = new HashMap<String, String>();
			Map<String, Object> allLangDisplayNamesMap = null;
			Map<String, Object> contextTransforms = null;
			LanguagesFileParser languagesParser = new LanguagesFileParser();

			if (!StringUtils.isEmpty(dispLanguage)) {
				allLangDisplayNamesMap = languagesParser.getDisplayNames(normalizedDispLanguage);
				contextTransforms = languagesParser.getContextTransforms(normalizedDispLanguage);
			}

			for (String supportedLanguage : supportedLanguageMap.keySet()) {
				String normalizedSuppLanguage = supportedLanguageMap.get(supportedLanguage);
				if (StringUtils.isEmpty(dispLanguage)) {
					normalizedDispLanguage = CommonUtil.getCLDRLocale(normalizedSuppLanguage, localePathMap, localeAliasesMap);
					allLangDisplayNamesMap = languagesParser.getDisplayNames(normalizedDispLanguage);
					contextTransforms = languagesParser.getContextTransforms(normalizedDispLanguage);
				}

				getDisplayNameForLanguage(supportedLanguage, normalizedSuppLanguage, allLangDisplayNamesMap, supportedLanguageNameMap);

				String displayName = supportedLanguageNameMap.get(supportedLanguage);
				Map<String, String> layoutDisplayNameMap = getLayoutDisplayNameMap(contextTransforms, displayName);

				DisplayLanguageDTO dto = new DisplayLanguageDTO();
				if (supportedLanguage.indexOf(ConstantsUnicode.ALT) > 0) {//handle languages like en-US-alt-short
					dto.setLanguageTag(supportedLanguage);
				} else {
					dto.setLanguageTag(normalizedSuppLanguage);
				}
				dto.setDisplayName(displayName);
				dto.setDisplayName_sentenceBeginning(layoutDisplayNameMap.get(DISPLAY_NAME_SENTENCE_BEGINNING));
				dto.setDisplayName_uiListOrMenu(layoutDisplayNameMap.get(DISPLAY_NAME_UI_LIST));
				dto.setDisplayName_standalone(layoutDisplayNameMap.get(DISPLAY_NAME_STANDALONE));
				dtoList.add(dto);
			}

			TranslationCache3.addCachedObject(CacheName.LANGUAGE, cacheKey, ArrayList.class, dtoList);
		}
		return dtoList;
	}

	private void getDisplayNameForLanguage(String language, String normalizedLanguage, Map<String, Object> allLangDisplayNamesMap, Map<String, String> languageNameMap){
		if(allLangDisplayNamesMap != null && allLangDisplayNamesMap.get(ConstantsKeys.LANGUAGES) != null){
			Map<String, Object> languageMap = (Map<String, Object>) allLangDisplayNamesMap.get(ConstantsKeys.LANGUAGES);
			if(languageMap != null && languageMap.get(ConstantsKeys.LANGUAGES) != null && ((Map<String, String>)languageMap.get(ConstantsKeys.LANGUAGES)).get(language) != null) {
				languageNameMap.put(language, ((Map<String, String>)languageMap.get(ConstantsKeys.LANGUAGES)).get(language));
			}else {
				languageNameMap.put(language, getDisplayNameByLocaleElements(normalizedLanguage, allLangDisplayNamesMap));
			}
		}else{
			languageNameMap.put(language, "");
		}
	}

	private String getDisplayNameByLocaleElements(String locale, Map<String, Object> displayNamesMap) {
		String displayName ="";
		Map<String, Object> localeDisplayNamesMap = (Map<String, Object>) displayNamesMap.get(ConstantsKeys.LOCALE_DISPLAY_NAMES);
		Map<String, String> languageData = (Map<String, String>) ((Map<String, Object>)displayNamesMap.get(ConstantsKeys.LANGUAGES)).get(ConstantsKeys.LANGUAGES);
		Map<String, String> regionData = (Map<String, String>) ((Map<String, Object>) displayNamesMap.get(ConstantsKeys.REGIONS)).get(ConstantsKeys.TERRITORIES);
		Map<String, String> scriptsData = (Map<String, String>) ((Map<String, Object>) displayNamesMap.get(ConstantsKeys.SCRIPTS)).get(ConstantsKeys.SCRIPTS);
		Map<String, String> variantsData = (Map<String, String>) ((Map<String, Object>)displayNamesMap.get(ConstantsKeys.VARIANTS)).get(ConstantsKeys.VARIANTS);
		Map<String, Object> localeDisplayNamesData = (Map<String, Object>) localeDisplayNamesMap.get(ConstantsKeys.LOCALE_DISPLAY_NAMES);
		Map<String, String> localeDisplayPattern = (Map<String, String>) localeDisplayNamesData.get(ConstantsKeys.LOCALE_DISPLAY_PATTERN);
		String localePattern = localeDisplayPattern.get(ConstantsKeys.LOCALE_PATTERN);
		String localeSeparator = localeDisplayPattern.get(ConstantsKeys.LOCALE_SEPARATOR);

		Locale localeObj = Locale.forLanguageTag(locale);
		String language = localeObj.getLanguage();
		String script = localeObj.getScript();
		String country = localeObj.getCountry();
		String variant = localeObj.getVariant();

		boolean hasScript = script !=null && script.length() > 0;
		boolean hasCountry = country !=null && country.length() > 0;
		boolean hasVariant = variant !=null && variant.length() > 0;

		String languageName = languageData.get(language);

		if (!StringUtils.isEmpty(languageName)) {
			StringBuilder buf = new StringBuilder();
			if (hasScript) {
				String scriptName = scriptsData.get(script);
				if (scriptName != null) {buf.append(scriptName);}
			}
			if (hasCountry) {
				String countryName = regionData.get(country);
				if (countryName != null) {appendWithSep(buf, localeSeparator, countryName);}
			}
			if (hasVariant) {
				String variantName = variantsData.get(variant);
				if (variantName != null) {appendWithSep(buf, localeSeparator, variantName);}
			}

			if (buf.isEmpty()) {
				displayName = languageName;
			} else {
				displayName = MessageFormat.format(localePattern, languageName, buf.toString());
			}
		}
		return displayName;
	}

	private StringBuilder appendWithSep( StringBuilder b, String localeSeparator, String s) {
		if (b.length() == 0) {
			b.append(s);
		} else {
			String actualLocaleSeparator = localeSeparator.substring(localeSeparator.indexOf("{0}")+3, localeSeparator.indexOf("{1}"));
			b.append(actualLocaleSeparator).append(s);
		}
		return b;
	}

	private Map<String, String> getLayoutDisplayNameMap(Map<String, Object> contextTransforms, String displayName) {
		Map<String, String> layoutDisplayNameMap = new HashMap<>();
		layoutDisplayNameMap.put(DISPLAY_NAME_UI_LIST, displayName);
		layoutDisplayNameMap.put(DISPLAY_NAME_STANDALONE, displayName);
		layoutDisplayNameMap.put(DISPLAY_NAME_SENTENCE_BEGINNING, tittleCase(displayName));
		if (contextTransforms != null && contextTransforms.get(CONTEXT_TRANSFORMS) != null) {
			Map<String, Map<String, String>> contextTransformMap = (Map<String, Map<String, String>>) contextTransforms.get(CONTEXT_TRANSFORMS);
			if (contextTransformMap != null && contextTransformMap.get(ConstantsKeys.LANGUAGES) != null) {
				Map<String, String> languageMap = contextTransformMap.get(ConstantsKeys.LANGUAGES);
				if (languageMap.get(STAND_ALONE) != null) {
					layoutDisplayNameMap.put(DISPLAY_NAME_STANDALONE, languageMap.get(STAND_ALONE).equals(NO_CHANGES) ? displayName : tittleCase(displayName));
				}
				if (languageMap.get(UI_LIST_OR_MENU) != null) {
					layoutDisplayNameMap.put(DISPLAY_NAME_UI_LIST, languageMap.get(UI_LIST_OR_MENU).equals(NO_CHANGES) ? displayName : tittleCase(displayName));
				}
			}
		}
		return layoutDisplayNameMap;
	}

	private String tittleCase(String displayName) {
		if (StringUtils.isEmpty(displayName)) {
			return displayName;
		}

		char[] chars = displayName.toCharArray();
		if (chars[0] >= 'a' && chars[0] <= 'z') {
			chars[0] = (char) (chars[0] - 32);
		}
		return String.valueOf(chars);
	}

@Override
	public List<TerritoryDTO> getTerritoriesFromCLDR(String languageList, String displayCity, String regions)
			throws Exception {
		TerritoriesFileParser territoriesParser = new TerritoriesFileParser();
		List<TerritoryDTO> territoryList = new ArrayList<TerritoryDTO>();
		String[] langArr = languageList.split(",");
		for (String lang : langArr) {
			String locale = lang.replace("_", "-");
			lang = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap).toLowerCase();
			logger.info("get data from cache");
			TerritoryDTO cacheTerritoryRegions = TranslationCache3.getCachedObject(CacheName.REGION,
					TERRITORY_REGIONS + lang, TerritoryDTO.class);
			if (cacheTerritoryRegions == null) {
				logger.info("cache regions is null, get data from file");
				cacheTerritoryRegions = territoriesParser.getRegionsByLanguage(lang);
				if (cacheTerritoryRegions.getTerritories() != null) {
					TranslationCache3.addCachedObject(CacheName.REGION, TERRITORY_REGIONS + lang, TerritoryDTO.class,
							cacheTerritoryRegions);
				}
			}
			if (Boolean.parseBoolean(displayCity)) {
				TerritoryDTO cacheTerritoryCities = TranslationCache3.getCachedObject(CacheName.REGION,
						TERRITORY_CITIES + lang, TerritoryDTO.class);
				if (cacheTerritoryCities == null) {
					logger.info("cache cities is null, get data from file");
					cacheTerritoryCities = territoriesParser.getCitiesByLanguage(lang);
					if (cacheTerritoryCities.getCities() != null) {
						TranslationCache3.addCachedObject(CacheName.REGION, TERRITORY_CITIES + lang, TerritoryDTO.class,
								cacheTerritoryCities);
					}
				}
				TerritoryDTO territory = cacheTerritoryRegions.shallowCopy();
				if (!StringUtils.isEmpty(regions) && !StringUtils.isEmpty(cacheTerritoryCities.getCities())) {
					Map<String, Object> cityMap = new HashMap<>();
					Map<String, Object> originCityMap = cacheTerritoryCities.getCities();
					Arrays.stream(regions.split(",")).forEach(regionName -> {
						regionName = regionName.toUpperCase();
						if (originCityMap.containsKey(regionName)) {
							cityMap.put(regionName, originCityMap.get(regionName));
						}
					});
					territory.setCities(cityMap);
				} else {
					territory.setCities(cacheTerritoryCities.getCities());
					
				}
				territoryList.add(territory);
			} else {
				territoryList.add(cacheTerritoryRegions);
			}

		}

		return territoryList;
	}
}
