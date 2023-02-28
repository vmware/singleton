/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.number;

/**
 * The class represents number formatting
 */
public interface INumberFormatService {

    /**
     * Format a number to localized number
     * @param locale A string representing a specific locale in [lang]_[country (region)] format. e.g., ja_JP, zh_CN
     * @param number The digits.
     * @return Localized number
     */
    public String formatNumber(String locale, String number);

	/**
	 * Format a number to localized number by scale
	 * @param locale
	 * @param number
	 * @param scale
	 * @return Localized number
	 */
	public String formatNumber(String locale, String number, int scale);
;

}
