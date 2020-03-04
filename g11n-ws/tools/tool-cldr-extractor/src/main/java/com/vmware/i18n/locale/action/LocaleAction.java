/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.locale.action;

import com.vmware.i18n.locale.service.ILocaleService;
import com.vmware.i18n.locale.service.impl.LocaleServiceImpl;
import com.vmware.i18n.pattern.action.PatternAction;
import com.vmware.i18n.utils.CommonUtil;

public class LocaleAction {

	private static volatile LocaleAction instance = null;
	private ILocaleService service = null;

	private LocaleAction() {
		service = new LocaleServiceImpl();
	}

	public static LocaleAction getInstance() {
		if (null == instance) {
			synchronized (PatternAction.class) {
				if (null == instance) {
					instance = new LocaleAction();
				}
			}
		}
		return instance;
	}

	public String getRegion(String language) {
		if (CommonUtil.isEmpty(language))
			return "";
		language = CommonUtil.normalizeToLanguageTag(language).toLowerCase();
		return service.getRegion(language);
	}

	public String getLanguage(String displayLanguage) {
		if (CommonUtil.isEmpty(displayLanguage))
			return "";
		displayLanguage = CommonUtil.normalizeToLanguageTag(displayLanguage).toLowerCase();
		return service.getLanguage(displayLanguage);
	}

	public String getLocaleWithDefaultRegion(String locale) {
		if (CommonUtil.isEmpty(locale))
			return "";
		return service.getLocaleWithDefaultRegion(locale);
	}

	public String getContextTransforms(String displayLanguage) {
		if (CommonUtil.isEmpty(displayLanguage))
			return "";
		displayLanguage = CommonUtil.normalizeToLanguageTag(displayLanguage).toLowerCase();
		return service.getContextTransforms(displayLanguage);
	}

}
