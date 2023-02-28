/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.number;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ibm.icu.math.BigDecimal;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.ULocale;

/**
 * The class represents number formatting
 */
@Service
public class NumberFormatService implements INumberFormatService{
	private static final Logger logger = LoggerFactory.getLogger(NumberFormatService.class);
    /**
     * Format a number to localized number
     * @param locale A string representing a specific locale in [lang]_[country (region)] format. e.g., ja_JP, zh_CN
     * @param number The digits.
     * @return Localized number
     */
    public String formatNumber(String locale, String number) {
        Number num = this.parseNumber(number);
        ULocale uLocale = new ULocale(locale);
        return NumberFormat.getNumberInstance(uLocale).format(num);
    }

	/**
	 * Format a number to localized number by scale
	 * @param locale
	 * @param number
	 * @param scale
	 * @return Localized number
	 */
	public String formatNumber(String locale, String number, int scale) {
	    Number num = this.parseNumber(number);
		ULocale uLocale = new ULocale(locale);
		NumberFormat numberFormat = NumberFormat.getNumberInstance(uLocale);
		numberFormat.setMaximumFractionDigits(scale);
		numberFormat.setMinimumFractionDigits(scale);
		numberFormat.setRoundingMode(BigDecimal.ROUND_HALF_UP);
		return numberFormat.format(num);
	}

	/**
	 * Parse a number string to number
	 * @param numberStr
	 * @return number
	 */
	private Number parseNumber(String numberStr){
	    try {
            return NumberFormat.getNumberInstance().parse(numberStr);
        } catch (ParseException e) {
        	logger.error(e.getMessage(), e);
            return 0;
        }
	}

}
