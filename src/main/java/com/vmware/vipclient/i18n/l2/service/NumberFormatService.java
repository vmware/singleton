/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.service;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.base.instances.PatternMessage;
import com.vmware.vipclient.i18n.l2.common.ConstantChars;
import com.vmware.vipclient.i18n.l2.common.PatternCategory;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.l2.text.NumberFormat;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;

public class NumberFormatService {
    Logger logger = LoggerFactory.getLogger(NumberFormatService.class);

    public String format(Object value, Integer fractionSize, Locale locale, int style) {
        return format(value, null, fractionSize, locale, style);
    }

    public String format(Object value, Integer fractionSize, String language, String region, int style) {
        return format(value, null, fractionSize, language, region, style);
    }

    public String format(Object value, String currencyCode, Integer fractionSize, String language, String region,
            int style) {

        if (language == null || region == null) {
            return format(value, currencyCode, fractionSize, null, style);
        } else {
            String formatNumber;
            if ((null == value) || "".equals(value)) {
                logger.info("Invalid value! ");
                return "";
            }
            if (fractionSize != null && fractionSize < 0) {
                fractionSize = 0;
            }

            I18nFactory factory = I18nFactory.getInstance();
            PatternMessage p = null;
            if (factory != null) {
                p = (PatternMessage) factory.getMessageInstance(PatternMessage.class);
            }
            if (null == p) {
                return null;
            }
            JSONObject formatData = p.getPatternMessage(language, region);
            if (style == NumberFormat.CURRENCYSTYLE) {
                validateCurrencyCode(currencyCode);
                formatData = getCurrencyRelatedData(formatData, currencyCode);
            } else {
                formatData = (JSONObject) formatData.get(PatternCategory.NUMBERS.toString());
            }
            if (formatData == null) {
                // return (String) value;
                throw new RuntimeException("Can't format " + value + " without pattern data!");
            }
            NumberFormat numberFormat = NumberFormat.getInstance(formatData, style);
            formatNumber = numberFormat.format(value, fractionSize);
            if (style == NumberFormat.PERCENTSTYLE) {
                String percentSymbol = (String) ((JSONObject) formatData.get(PatternKeys.NUMBERSYMBOLS))
                        .get(PatternKeys.PERCENTSIGN);
                formatNumber = formatNumber.replace(String.valueOf(ConstantChars.PERCENTSIGN), percentSymbol);
            } else if (style == NumberFormat.CURRENCYSTYLE) {
                JSONObject currencyData = (JSONObject) formatData.get(PatternKeys.CURRENCY);
                // String narrowCurrencySymbol = (String) currencyData.get(PatternKeys.NARROWCURRENCYSYMBOL);
                // String currencySymbol = narrowCurrencySymbol != null? narrowCurrencySymbol : (String)
                // currencyData.get(PatternKeys.CURRENCYSYMBOL);
                String currencySymbol = (String) currencyData.get(PatternKeys.CURRENCYSYMBOL);
                formatNumber = formatNumber.replace(String.valueOf(ConstantChars.CURRENCY_SIGN),
                        currencySymbol);
            }
            return formatNumber;
        }

    }

    public String format(Object value, String currencyCode, Integer fractionSize, Locale locale, int style) {
        String formatNumber;
        if ((null == value) || "".equals(value)) {
            logger.info("Invalid value! ");
            return "";
        }
        if (fractionSize != null && fractionSize < 0) {
            fractionSize = 0;
        }
        locale = locale == null ? Locale.ENGLISH : locale;
        I18nFactory factory = I18nFactory.getInstance();
        PatternMessage p = null;
        if (factory != null) {
            p = (PatternMessage) factory.getMessageInstance(PatternMessage.class);
        }
        if (null == p) {
            return null;
        }
        JSONObject formatData = (JSONObject) p.getPatternMessage(locale);
        if (style == NumberFormat.CURRENCYSTYLE) {
            validateCurrencyCode(currencyCode);
            formatData = getCurrencyRelatedData(formatData, currencyCode);
        } else {
            formatData = (JSONObject) formatData.get(PatternCategory.NUMBERS.toString());
        }
        if (formatData == null) {
            // return (String) value;
            throw new RuntimeException("Can't format " + value + " without pattern data!");
        }
        NumberFormat numberFormat = NumberFormat.getInstance(formatData, style);
        formatNumber = numberFormat.format(value, fractionSize);
        if (style == NumberFormat.PERCENTSTYLE) {
            String percentSymbol = (String) ((JSONObject) formatData.get(PatternKeys.NUMBERSYMBOLS))
                    .get(PatternKeys.PERCENTSIGN);
            formatNumber = formatNumber.replace(String.valueOf(ConstantChars.PERCENTSIGN), percentSymbol);
        } else if (style == NumberFormat.CURRENCYSTYLE) {
            JSONObject currencyData = (JSONObject) formatData.get(PatternKeys.CURRENCY);
            // String narrowCurrencySymbol = (String) currencyData.get(PatternKeys.NARROWCURRENCYSYMBOL);
            // String currencySymbol = narrowCurrencySymbol != null? narrowCurrencySymbol : (String)
            // currencyData.get(PatternKeys.CURRENCYSYMBOL);
            String currencySymbol = (String) currencyData.get(PatternKeys.CURRENCYSYMBOL);
            formatNumber = formatNumber.replace(String.valueOf(ConstantChars.CURRENCY_SIGN),
                    currencySymbol);
        }
        return formatNumber;
    }

    private JSONObject getCurrencyRelatedData(JSONObject allCategoriesData, String currencyCode) {
        JSONObject currencyFormatData = new JSONObject();
        JSONObject numberFormatData = (JSONObject) allCategoriesData.get(PatternCategory.NUMBERS.toString());
        JSONObject currencyData = (JSONObject) ((HashMap) allCategoriesData.get(PatternKeys.CURRENCIES))
                .get(currencyCode);
        if (currencyData == null) {
            throw new IllegalArgumentException("Unsupported currency code " + currencyCode + ".");
        }
        JSONObject currencySupplementalData = (JSONObject) ((HashMap) allCategoriesData
                .get(PatternCategory.SUPPLEMENTAL.toString())).get(PatternKeys.CURRENCIES);
        JSONObject fractionData = (JSONObject) ((HashMap) currencySupplementalData.get(PatternKeys.FRACTIONS))
                .get(currencyCode);
        currencyFormatData.put(PatternCategory.NUMBERS.toString(), numberFormatData);
        currencyFormatData.put(PatternKeys.CURRENCY, currencyData);
        currencyFormatData.put(PatternKeys.FRACTION, fractionData);
        return currencyFormatData;
    }

    public boolean validateCurrencyCode(String theISOCode) {
        if (theISOCode == null) {
            throw new NullPointerException("The input currency code is null.");
        }
        if (!isAlpha3Code(theISOCode)) {
            throw new IllegalArgumentException(
                    "The input currency code is not 3-letter alphabetic code.");
        }
        return true;
    }

    private static boolean isAlpha3Code(String code) {
        if (code.length() != 3) {
            return false;
        } else {
            for (int i = 0; i < 3; i++) {
                char ch = code.charAt(i);
                if (ch < 'A' || (ch > 'Z' && ch < 'a') || ch > 'z') {
                    return false;
                }
            }
        }
        return true;
    }
}
