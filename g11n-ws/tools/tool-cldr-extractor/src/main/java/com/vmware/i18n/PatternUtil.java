/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n;

import com.vmware.i18n.common.CLDRConstants;
import com.vmware.i18n.locale.action.LocaleAction;
import com.vmware.i18n.pattern.action.PatternAction;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.i18n.utils.timezone.TimeZoneName;

import java.util.LinkedHashMap;
import java.util.Map;

public class PatternUtil {

	/**
	 * @param locale language locale
	 * @param categories The pattern categories: dates, numbers, plurals, measurements. use ',' to split.
	 *        e.g. "dates,numbers,plurals"
	 * @return pattern JSON string
	 */
	public static String getPatternFromLib(String locale, String categories) {
		PatternAction pa = PatternAction.getInstance();
		return pa.getPattern(locale, categories);
	}

	/**
	 * @param language
	 * @return regions json string
	 */
	public static String getRegionFromLib(String language) {
		LocaleAction la = LocaleAction.getInstance();
		return la.getLocaleData(language, CLDRConstants.LOCALE_TERRITORIES_PATH);
	}

	/**
	 * @param language
	 * @return cities json string
	 */
	public static String getCitiesFromLib(String language) {
		LocaleAction la = LocaleAction.getInstance();
		return la.getLocaleData(language, CLDRConstants.LOCALE_CITIES_PATH);
	}

	/**
	 * @param displayLanguage
	 * @return languages json string
	 */
	public static String getLanguageFromLib(String displayLanguage) {
		LocaleAction la = LocaleAction.getInstance();
		return la.getLocaleData(displayLanguage, CLDRConstants.LOCALE_LANGUAGES_PATH);
	}

	/**
	 * @param displayLanguage
	 * @return scripts json string
	 */
	public static String getScriptsFromLib(String displayLanguage) {
		LocaleAction la = LocaleAction.getInstance();
		return la.getLocaleData(displayLanguage, CLDRConstants.LOCALE_SCRIPTS_PATH);
	}

	/**
	 * @param displayLanguage
	 * @return variants json string
	 */
	public static String getVariantsFromLib(String displayLanguage) {
		LocaleAction la = LocaleAction.getInstance();
		return la.getLocaleData(displayLanguage, CLDRConstants.LOCALE_VARIANTS_PATH);
	}

	/**
	 * @param displayLanguage
	 * @return localeDisplayNames json string
	 */
	public static String getLocaleDisplayNamesFromLib(String displayLanguage) {
		LocaleAction la = LocaleAction.getInstance();
		return la.getLocaleData(displayLanguage, CLDRConstants.LOCALE_LOCALEDISPLAYNAMES_PATH);
	}

	/**
	 * @param displayLanguage
	 * @return ContextTransform json string
	 */
	public static String getContextTransformFromLib(String displayLanguage) {
		LocaleAction la = LocaleAction.getInstance();
		return la.getContextTransforms(displayLanguage);
	}

	/**
	 * @param locale
	 * @return matching locale
	 */
	public static String getMatchingLocaleFromLib(String locale) {
		LocaleAction la = LocaleAction.getInstance();
		return la.getLocaleWithDefaultRegion(locale);
	}
	
	/**
	 * @param locale
	 * @param default territory
	 * @return matching locale TimeZoneName
	 */
	public static TimeZoneName getTimeZoneName(String locale, boolean defaultTerritory) {
	    PatternAction pa = PatternAction.getInstance();
        return pa.getTimeZoneName(locale, defaultTerritory);
	}
}
