/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.service;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.base.instances.PatternMessage;
import com.vmware.vipclient.i18n.l2.common.ConstantChars;
import com.vmware.vipclient.i18n.l2.common.PatternCategory;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.l2.text.NumberFormat;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.json.JSONObject;
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
            JSONObject numberFormatData = null;
            I18nFactory factory = I18nFactory.getInstance();
            if (factory == null) {
                throw new RuntimeException("I18nFactory is null, please create it first!");
            }
            PatternMessage p = (PatternMessage) factory.getMessageInstance(PatternMessage.class);
            JSONObject localeFormatData = p.getPatternMessage(language, region);
            if (localeFormatData == null) {
                throw new RuntimeException("No format pattern data found for language: " + language + ", region: " + region);
            }
            if (style == NumberFormat.CURRENCYSTYLE) {
                validateCurrencyCode(currencyCode);
                numberFormatData = getCurrencyRelatedData(localeFormatData, currencyCode);
            } else {
                numberFormatData = (JSONObject) localeFormatData.get(PatternCategory.NUMBERS.toString());
            }
            if (numberFormatData == null) {
                // return (String) value;
                throw new RuntimeException("Can't format " + value + " without pattern data!");
            }
            NumberFormat numberFormat = NumberFormat.getInstance(numberFormatData, style);
            formatNumber = numberFormat.format(value, fractionSize);
            if (style == NumberFormat.PERCENTSTYLE) {
                String percentSymbol = (String) ((JSONObject) numberFormatData.get(PatternKeys.NUMBERSYMBOLS))
                        .get(PatternKeys.PERCENTSIGN);
                formatNumber = formatNumber.replace(String.valueOf(ConstantChars.PERCENTSIGN), percentSymbol);
            } else if (style == NumberFormat.CURRENCYSTYLE) {
                JSONObject currencyData = (JSONObject) numberFormatData.get(PatternKeys.CURRENCY);
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
        JSONObject numberFormatData = null;
        I18nFactory factory = I18nFactory.getInstance();
        if (factory == null) {
            throw new RuntimeException("I18nFactory is null, please create it first!");
        }
        PatternMessage p = (PatternMessage) factory.getMessageInstance(PatternMessage.class);
        JSONObject localeFormatData = (JSONObject) p.getPatternMessage(locale);
        if(localeFormatData == null) {
            throw new RuntimeException("No format pattern data found for locale " + locale + " !");
        }
        if (style == NumberFormat.CURRENCYSTYLE) {
            String actualCurrencyCode;
            if(currencyCode == null){
                actualCurrencyCode = ConstantsKeys.USD;
            }else {
                actualCurrencyCode = currencyCode;
                validateCurrencyCode(actualCurrencyCode);
            }
            numberFormatData = getCurrencyRelatedData(localeFormatData, actualCurrencyCode);
        } else {
            numberFormatData = (JSONObject) localeFormatData.get(PatternCategory.NUMBERS.toString());
        }
        if (numberFormatData == null) {
            // return (String) value;
            throw new RuntimeException("Can't format " + value + " without pattern data!");
        }
        NumberFormat numberFormat = NumberFormat.getInstance(numberFormatData, style);
        formatNumber = numberFormat.format(value, fractionSize);
        if (style == NumberFormat.PERCENTSTYLE) {
            String percentSymbol = (String) ((JSONObject) numberFormatData.get(PatternKeys.NUMBERSYMBOLS))
                    .get(PatternKeys.PERCENTSIGN);
            formatNumber = formatNumber.replace(String.valueOf(ConstantChars.PERCENTSIGN), percentSymbol);
        } else if (style == NumberFormat.CURRENCYSTYLE) {
            JSONObject currencyData = (JSONObject) numberFormatData.get(PatternKeys.CURRENCY);
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
        JSONObject currencyData = (JSONObject) ((JSONObject) allCategoriesData.get(PatternKeys.CURRENCIES))
                .get(currencyCode);
        if (currencyData == null) {
            throw new IllegalArgumentException("Unsupported currency code " + currencyCode + ".");
        }
        JSONObject currencySupplementalData = (JSONObject) ((JSONObject) allCategoriesData
                .get(PatternCategory.SUPPLEMENTAL.toString())).get(PatternKeys.CURRENCIES);
        JSONObject fractionData = null;
        try {
        	fractionData = (JSONObject) ((JSONObject) currencySupplementalData.get(PatternKeys.FRACTIONS)).get(currencyCode);
        } catch (org.json.JSONException e) {
        	logger.info("NumberFormatService - Can't find fractionData, null will be set");
        }               
        currencyFormatData.put(PatternCategory.NUMBERS.toString(), numberFormatData);
        currencyFormatData.put(PatternKeys.CURRENCY, currencyData);
        currencyFormatData.put(PatternKeys.FRACTION, fractionData);
        return currencyFormatData;
    }

    public boolean validateCurrencyCode(String theISOCode) {
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
