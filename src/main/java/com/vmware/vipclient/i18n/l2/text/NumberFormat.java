/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.json.JSONObject;

import com.vmware.vipclient.i18n.l2.common.PatternKeys;

public abstract class NumberFormat {
    public static final int NUMBERSTYLE           = 0;
    public static final int CURRENCYSTYLE         = 1;
    public static final int PERCENTSTYLE          = 2;
    public static final int SCIENTIFICSTYLE       = 3;
    public static final int INTEGERSTYLE          = 4;
    public static final int STANDARDCURRENCYSTYLE = 9;

    /*
     * public static NumberFormat getInstance(JSONObject patterns, int style) {
     * if (style < NUMBERSTYLE || style > STANDARDCURRENCYSTYLE) {
     * throw new IllegalArgumentException(
     * "choice should be from NUMBERSTYLE to STANDARDCURRENCYSTYLE");
     * }
     * NumberFormat nf;
     * JSONObject numberSymbols = (JSONObject) patterns.get(PatternKeys.NUMBERSYMBOLS);
     * JSONObject numberFormats = (JSONObject) patterns.get(PatternKeys.NUMBERFORMATS);
     * String pattern = getPattern(numberFormats, style);
     * DecimalFormat df = new DecimalFormat(pattern, numberSymbols, style);
     * nf = df;
     * return nf;
     * }
     */

    public static NumberFormat getInstance(JSONObject patterns, int style) {
        if (style < NUMBERSTYLE || style > STANDARDCURRENCYSTYLE) {
            throw new IllegalArgumentException(
                    "choice should be from NUMBERSTYLE to STANDARDCURRENCYSTYLE");
        }
        NumberFormat nf;
        DecimalFormat df = new DecimalFormat(patterns, style);
        nf = df;
        return nf;
    }

    public static String getPattern(JSONObject numberFormats, int style) {
        String pattern = "";
        switch (style) {
        case 0:
            pattern = (String) numberFormats.get(PatternKeys.DECIMALFORMATS);
            break;
        case 1:
            pattern = (String) numberFormats.get(PatternKeys.CURRENCYFORMATS);
            break;
        case 2:
            pattern = (String) numberFormats.get(PatternKeys.PERCENTFORMATS);
            break;
        case 3:
            pattern = (String) numberFormats.get(PatternKeys.SCIENTIFICFORMATS);
            break;
        default:
            pattern = (String) numberFormats.get(PatternKeys.DECIMALFORMATS);
        }
        return pattern;
    }

    public String format(Object value, Integer fractionSize) {
        if (value instanceof Long || value instanceof Integer || value instanceof Short
                || value instanceof Byte) {
            return this.format(((Number) value).longValue(), fractionSize);
        } else if (value instanceof BigInteger) {
            return this.format((BigInteger) value, fractionSize);
        } else if (value instanceof BigDecimal) {
            return this.format((BigDecimal) value, fractionSize);
        } else if (value instanceof Float) {
            return this.format(String.valueOf(value), fractionSize);
        } else if (value instanceof Double) {
            return this.format(((Number) value).doubleValue(), fractionSize);
        } else if (value instanceof String) {
            return this.format((String) value, fractionSize);
        } else {
            throw new IllegalArgumentException("Cannot format given Object as a Number");
        }
    }

    public abstract String format(long value, Integer fractionSize);

    public abstract String format(double value, Integer fractionSize);

    public abstract String format(BigInteger value, Integer fractionSize);

    public abstract String format(BigDecimal value, Integer fractionSize);

    public abstract String format(String value, Integer fractionSize);
}
