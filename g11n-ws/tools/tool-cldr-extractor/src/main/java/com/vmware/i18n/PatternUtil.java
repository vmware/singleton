/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n;

import com.vmware.i18n.common.CLDRConstants;
import com.vmware.i18n.locale.action.LocaleAction;
import com.vmware.i18n.pattern.action.PatternAction;
import com.vmware.i18n.utils.CommonUtil;

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
		return la.getRegion(language);
	}

	/**
	 * @param displayLanguage
	 * @return languages json string
	 */
	public static String getLanguageFromLib(String displayLanguage) {
		LocaleAction la = LocaleAction.getInstance();
		return la.getLanguage(displayLanguage);
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
}
