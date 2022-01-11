/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.date;

import com.vmware.vip.common.utils.LocaleUtils;

/**
 * Dto objects for date encapsulation
 */
public class DateDTO {
	private String pattern;
	private String locale;
	private String longDate;
	private String formattedDate;
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = LocaleUtils.normalizeToLanguageTag(locale);
	}
	public String getLongDate() {
		return longDate;
	}
	public void setLongDate(String longDate) {
		this.longDate = longDate;
	}
	public String getformattedDate() {
		return formattedDate;
	}
	public void setformattedDate(String formattedDate) {
		this.formattedDate = formattedDate;
	}
}
