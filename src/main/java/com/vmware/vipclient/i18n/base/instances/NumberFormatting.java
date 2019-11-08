/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.instances;

import java.util.Locale;

import com.vmware.vipclient.i18n.l2.service.NumberFormatService;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

/**
 * Provides functions to get the formatted number, percent, currency and so on.
 */
public class NumberFormatting implements Formatting {

    public NumberFormatting() {
        super();
    }

    /**
     * Format a number to an localized number string in decimal style according to locale's decimal format defined in
     * cldr.
     * Default fraction size in cldr decimal format is: the minimum is 0, the maximum is 3.
     * 
     * @param value
     *            The number to be formatted.
     * @param locale
     *            The locale for which the number format is desired.
     * @return The formatted number string.
     */
    public String formatNumber(Object value, Locale locale) {
        return formatNumber(value, null, locale);
    }

    /**
     * Format a number to an localized number string in decimal style using user self defined fraction size.
     * cldr decimal format's fraction size will be ignored.
     * 
     * @param value
     *            The number to be formatted.
     * @param fractionSize
     *            User self defined fraction size.
     * @param locale
     *            The locale for which the number format is desired.
     * @return The formatted number string.
     */
    public String formatNumber(Object value, Integer fractionSize, Locale locale) {
        return new NumberFormatService().format(value, fractionSize, locale,
                com.vmware.vipclient.i18n.l2.text.NumberFormat.NUMBERSTYLE);
    }

    /**
     * Format a number to an localized number string in decimal style using user self defined fraction size.
     * cldr decimal format's fraction size will be ignored.
     * 
     * @param value
     *            The number to be formatted.
     * @param fractionSize
     *            User self defined fraction size.
     * @param language
     *            The language for which the number format is desired.
     * @param region
     *            The region for which the number format is desired.
     * @return The formatted number string.
     */
    public String formatNumber(Object value, Integer fractionSize, String language, String region) {
        return new NumberFormatService().format(value, fractionSize, language, region,
                com.vmware.vipclient.i18n.l2.text.NumberFormat.NUMBERSTYLE);
    }

    public String formatNumber(Object value, String language, String region) {
        return new NumberFormatService().format(value, null, language, region,
                com.vmware.vipclient.i18n.l2.text.NumberFormat.NUMBERSTYLE);
    }

    /**
     * Format a number to an localized number string in percent style according to locale's percent format defined in
     * cldr.
     * Default fraction size in cldr percent format is 0.
     * 
     * @param value
     *            The number to be formatted.
     * @param locale
     *            The locale for which the percent format is desired.
     * @return The formatted percent string.
     */
    public String formatPercent(Object value, Locale locale) {
        return formatPercent(value, null, locale);
    }

    /**
     * Format a number to an localized number string in percent style using user self defined fraction size.
     * cldr percent format's fraction size will be ignored.
     * 
     * @param value
     *            The number to be formatted.
     * @param fractionSize
     *            User self defined fraction size.
     * @param locale
     *            The locale for which the percent format is desired.
     * @return The formatted percent string.
     */
    public String formatPercent(Object value, Integer fractionSize, Locale locale) {
        return new NumberFormatService().format(value, fractionSize, locale,
                com.vmware.vipclient.i18n.l2.text.NumberFormat.PERCENTSTYLE);
    }

    /**
     * Format a number to an localized number string in percent style using user self defined fraction size.
     * cldr percent format's fraction size will be ignored.
     * 
     * @param value
     *            The number to be formatted.
     * @param fractionSize
     *            User self defined fraction size.
     * @param language
     *            The language for which the number format is desired.
     * @param region
     *            The region for which the number format is desired.
     * @return The formatted percent string.
     */
    public String formatPercent(Object value, Integer fractionSize, String language, String region) {
        return new NumberFormatService().format(value, fractionSize, language, region,
                com.vmware.vipclient.i18n.l2.text.NumberFormat.PERCENTSTYLE);
    }

    public String formatPercent(Object value, String language, String region) {
        return new NumberFormatService().format(value, null, language, region,
                com.vmware.vipclient.i18n.l2.text.NumberFormat.PERCENTSTYLE);
    }

    /**
     * Format a number to an localized number string with default currency symbol($) according to locale's currency
     * format defined in cldr.
     * Default fraction size in cldr currency format is 2.
     * 
     * @param amount
     *            The number to be formatted.
     * @param locale
     *            The locale for which the currency format is desired.
     * @return The formatted currency string.
     */
    public String formatCurrency(Object amount, Locale locale) {
        return formatCurrency(amount, ConstantsKeys.USD, locale);
    }

    /**
     * Format a number to an localized number string with currency symbol specified by 3-letter currency code according
     * to locale's currency format defined in cldr.
     * Default fraction size in cldr currency format is 2.
     * 
     * @param amount
     *            The number to be formatted.
     * @param currencyCode
     *            ISO 4217 3-letter code. For all legal code please refer to
     *            https://en.wikipedia.org/wiki/ISO_4217.
     * @param locale
     *            The locale for which the currency format is desired.
     * @return The formatted currency string.
     */
    public String formatCurrency(Object amount, String currencyCode, Locale locale) {
        return new NumberFormatService().format(amount, currencyCode, null, locale,
                com.vmware.vipclient.i18n.l2.text.NumberFormat.CURRENCYSTYLE);
    }

    /**
     * Format a number to an localized number string with currency symbol specified by 3-letter currency code according
     * to locale's currency format defined in cldr.
     * Default fraction size in cldr currency format is 2.
     * 
     * @param amount
     *            The number to be formatted.
     * @param currencyCode
     *            ISO 4217 3-letter code. For all legal code please refer to
     *            https://en.wikipedia.org/wiki/ISO_4217.
     * @param language
     *            The language for which the number format is desired.
     * @param region
     *            The region for which the number format is desired.
     * @return The formatted currency string.
     */
    public String formatCurrency(Object amount, String currencyCode, String language, String region) {
        return new NumberFormatService().format(amount, currencyCode, null, language, region,
                com.vmware.vipclient.i18n.l2.text.NumberFormat.CURRENCYSTYLE);
    }

    public String formatCurrency(Object amount, String language, String region) {
        return new NumberFormatService().format(amount, ConstantsKeys.USD, null, language, region,
                com.vmware.vipclient.i18n.l2.text.NumberFormat.CURRENCYSTYLE);
    }
}
