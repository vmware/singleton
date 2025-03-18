/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.l2.common.ConstantChars;
import com.vmware.vipclient.i18n.l2.common.PatternCategory;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.l2.number.parser.IntegerDigitsParser;

public class DecimalFormat extends NumberFormat {
    Logger             logger = LoggerFactory.getLogger(DecimalFormat.class);

    private JSONObject numberSymbols;
    private String     pattern;
    private int        style;
    private JSONObject fractionData;

    /*
     * public DecimalFormat(String pattern, JSONObject numberSymbols, int style) {
     * this.pattern = pattern;
     * this.numberSymbols = numberSymbols;
     * this.style = style;
     * }
     * 
     * public DecimalFormat(String pattern, JSONObject numberSymbols, JSONObject fractionData, int style) {
     * this.pattern = pattern;
     * this.numberSymbols = numberSymbols;
     * this.style = style;
     * this.fractionData = fractionData;
     * }
     */

    public DecimalFormat(JSONObject formatData, int style) {
        JSONObject numberFormats;
        if (style == NumberFormat.CURRENCYSTYLE) {
            this.numberSymbols = (JSONObject) ((JSONObject) formatData.get(PatternCategory.NUMBERS.toString()))
                    .get(PatternKeys.NUMBERSYMBOLS);
            numberFormats = (JSONObject) ((JSONObject) formatData.get(PatternCategory.NUMBERS.toString()))
                    .get(PatternKeys.NUMBERFORMATS);
            this.fractionData = null;
            try {
            	this.fractionData = (JSONObject) formatData.get(PatternKeys.FRACTION);
            } catch (org.json.JSONException e) {
            	logger.info("Can't find fractionData, null will be set");
            }
        } else {
            this.numberSymbols = (JSONObject) formatData.get(PatternKeys.NUMBERSYMBOLS);
            numberFormats = (JSONObject) formatData.get(PatternKeys.NUMBERFORMATS);
        }
        this.pattern = getPattern(numberFormats, style);
        this.style = style;
    }

    /*
     * public DecimalFormat(String pattern, JSONObject numberSymbols, JSONObject fractionData, int style) {
     * this.pattern = pattern;
     * this.numberSymbols = numberSymbols;
     * this.style = style;
     * this.fractionData = fractionData;
     * }
     */

    @Override
    public String format(long value, Integer fractionSize) {
        return parseNumber(String.valueOf(value), fractionSize);
    }

    @Override
    public String format(double value, Integer fractionSize) {
        return parseNumber(String.valueOf(value), fractionSize);
    }

    @Override
    public String format(BigInteger value, Integer fractionSize) {
        return parseNumber(String.valueOf(value), fractionSize);
    }

    @Override
    public String format(BigDecimal value, Integer fractionSize) {
        return parseNumber(String.valueOf(value), fractionSize);
    }

    @Override
    public String format(String value, Integer fractionSize) {
        return parseNumber(value, fractionSize);
    }

    public NumberPatternInfo parsePattern(String pattern) {
        NumberPatternInfo patternInfo = new NumberPatternInfo();
        String[] patternParts = new String[2];
        String positivePattern;
        String negativePattern = null;
        if (pattern.indexOf(ConstantChars.PATTERN_SEP) >= 0) {
            patternParts = pattern.split(ConstantChars.PATTERN_SEP);
            positivePattern = patternParts[0];
            negativePattern = patternParts[1];
        } else {
            positivePattern = pattern;
        }
        /* handle positive pattern */
        String[] positiveParts = new String[2];
        if (positivePattern.indexOf(ConstantChars.DECIMAL_SEP) >= 0) {
            positiveParts = positivePattern.split("\\" + ConstantChars.DECIMAL_SEP);
        } else if (positivePattern.indexOf(ConstantChars.PATTERN_EXPONENT) >= 0) {
            positiveParts[0] = positivePattern.substring(0, positivePattern.indexOf(ConstantChars.PATTERN_EXPONENT));
            positiveParts[1] = positivePattern.substring(positivePattern.indexOf(ConstantChars.PATTERN_EXPONENT));
        } else {// There may be symbol after number pattern, e.g. percent format '#,##0%' and currency
                // format'¤#,##0.00'.
            positiveParts[0] = positivePattern.substring(0,
                    positivePattern.lastIndexOf(ConstantChars.ZEROCHAR) + 1);
            positiveParts[1] = positivePattern.substring(positivePattern.lastIndexOf(ConstantChars.ZEROCHAR) + 1);
        }
        String integerPattern = positiveParts[0];
        String fractionPattern = positiveParts[1];
        /* handle integer pattern */
        String posPrefix = "";
        for (int i = 0; i < integerPattern.length(); i++) {
            char ch = integerPattern.charAt(i);
            if (ConstantChars.ZEROCHAR == ch || ConstantChars.DIGIT_CHAR == ch) {
                break;
            } else {
                posPrefix += ch;
            }
        }
        integerPattern = !"".equals(posPrefix) ? integerPattern.substring(posPrefix.length()) : integerPattern;

        int groupSize = 0;
        int groupSize2 = 0;
        if (integerPattern.indexOf(ConstantChars.GROUP_SEP) >= 0) {
            groupSize = integerPattern.length() - 1 - integerPattern.lastIndexOf(ConstantChars.GROUP_SEP);
            String[] integerParts = integerPattern.split(ConstantChars.GROUP_SEP);
            groupSize2 = integerParts.length > 2 ? integerParts[integerParts.length - 2].length()
                    : 0;
        }
        int maxIntegerDigit = 0;
        int minIntegerDigit = 0;
        if (integerPattern.indexOf(ConstantChars.ZEROCHAR) >= 0) {
            minIntegerDigit = integerPattern.length() - integerPattern.indexOf(ConstantChars.ZEROCHAR);
            if (ConstantChars.ZEROCHAR == integerPattern.charAt(0)) {
                maxIntegerDigit = minIntegerDigit;
            }
        }
        /* handle fraction pattern */
        String posSuffix = "";// =
        int minFractionDigits = 0;
        int maxFractionDigits = 0;
        if (fractionPattern != null && !"".equals(fractionPattern)) {
            for (int i = 0; i < fractionPattern.length(); i++) {
                char ch = fractionPattern.charAt(i);
                if (ch == ConstantChars.ZEROCHAR) {
                    minFractionDigits = i + 1;
                } else if (ch == ConstantChars.DIGIT_CHAR) {
                    maxFractionDigits = i + 1;
                } else if (ch == ConstantChars.PATTERN_EXPONENT) {
                    posSuffix = fractionPattern.substring(i);
                    break;
                } else {
                    posSuffix += ch;
                }
            }
            if (maxFractionDigits < minFractionDigits) {
                maxFractionDigits = minFractionDigits;
            }
        }
        patternInfo.setPositivePrefix(posPrefix);
        patternInfo.setPositiveSuffix(posSuffix);
        patternInfo.setMinimumIntegerDigits(minIntegerDigit);
        patternInfo.setMaximumIntegerDigits(maxIntegerDigit);
        patternInfo.setMinimumFractionDigits(minFractionDigits);
        patternInfo.setMaximumFractionDigits(maxFractionDigits);
        patternInfo.setGroupingSize(groupSize);
        patternInfo.setSecondaryGroupingSize(groupSize2);
        // patternInfo.setMinimumExponentDigits(minExpDig);
        /* handle negative pattern */
        String negPrefix = "";
        String negSuffix = "";
        if (negativePattern != null && !"".equals(negativePattern)) {
            String[] negativeParts = new String[2];
            if (negativePattern.indexOf(ConstantChars.DECIMAL_SEP) > 0) {
                negativeParts = negativePattern.split("\\" + ConstantChars.DECIMAL_SEP);
            } else {
                negativeParts[0] = negativePattern.substring(0,
                        positivePattern.lastIndexOf(ConstantChars.ZEROCHAR) + 1);
                negativeParts[1] = negativePattern
                        .substring(positivePattern.lastIndexOf(ConstantChars.ZEROCHAR) + 1);
            }
            String negIntegerPattern = negativeParts[0];
            String negFractionPattern = negativeParts[1];
            for (int i = 0; i < negIntegerPattern.length(); i++) {
                char ch = negIntegerPattern.charAt(i);
                if (ConstantChars.ZEROCHAR == ch || ConstantChars.DIGIT_CHAR == ch) {
                    break;
                } else {
                    negPrefix += ch;
                }
            }
            for (int i = 0; i < negFractionPattern.length(); i++) {
                char ch = negFractionPattern.charAt(i);
                if (ch != ConstantChars.ZEROCHAR && ch != ConstantChars.DIGIT_CHAR) {
                    negSuffix += ch;
                }
            }
        } else {
            negPrefix = ConstantChars.MINUSSIGN + patternInfo.getPositivePrefix();
            negSuffix = patternInfo.getPositiveSuffix();
        }
        patternInfo.setNegativePrefix(negPrefix);
        patternInfo.setNegativeSuffix(negSuffix);
        return patternInfo;
    }

    public String parseNumber(String numStr, Integer customizedFractionSize) {
        StringBuilder localizedNumStr = new StringBuilder();
        NumberPatternInfo patternInfo = null;
        try {
            patternInfo = parsePattern(pattern);
            if (this.style == CURRENCYSTYLE) {
                adjustFraction4Currency(patternInfo);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "";
        }
        int fractionLength = 0;
        if (numStr.indexOf(".") > 0) {
            fractionLength = numStr.length() - 1 - numStr.indexOf(".");
        }
        // use default
        if (customizedFractionSize == null) {
            if (fractionLength < patternInfo.getMinimumFractionDigits()) {
                fractionLength = patternInfo.getMinimumFractionDigits();
            } else if (fractionLength > patternInfo.getMaximumFractionDigits()) {
                fractionLength = patternInfo.getMaximumFractionDigits();
            }
        } else {// use customized fraction size
            fractionLength = customizedFractionSize;
        }
        // Decimal
        BigDecimal b = new BigDecimal(numStr);
        /*
         * if(BigDecimal.b.){
         * 
         * }
         */
        boolean isNegative = isNegative(b.doubleValue());
        if (!isNegative) {
            localizedNumStr.append(patternInfo.getPositivePrefix());
        } else {
            localizedNumStr.append(patternInfo.getNegativePrefix());
        }
        b = b.abs();
        numStr = b.toString();
        if (this.style == PERCENTSTYLE) {
            numStr = b.multiply(new BigDecimal(100)).setScale(fractionLength, BigDecimal.ROUND_HALF_UP)//
                    .toString();
        } else {
            // numStr=MessageFormat.format(pattern, arguments);
            numStr = b.setScale(fractionLength, BigDecimal.ROUND_HALF_EVEN)// BigDecimal.ROUND_HALF_UP
                                                                           // patternInfo.getRound()
                    .toString();
        }
        String[] numArray = numStr.toString().split("\\" + ConstantChars.DECIMAL_SEP);
        IntegerDigitsParser parser = new IntegerDigitsParser(numberSymbols);
        String groupedIntegerDigits = parser.groupIntegerDigits(numArray[0], patternInfo.getGroupingSize());
        localizedNumStr.append(groupedIntegerDigits);
        String localizedDecimalSep = (String) numberSymbols.get(PatternKeys.DECIMAL);
        if (numArray.length > 1) {
            localizedNumStr.append(localizedDecimalSep).append(numArray[1]);
        }
        if (!isNegative) {
            localizedNumStr.append(patternInfo.getPositiveSuffix());
        } else {
            localizedNumStr.append(patternInfo.getNegativeSuffix());
        }
        return localizedNumStr.toString();
    }

    private void adjustFraction4Currency(NumberPatternInfo patternInfo) {
        if (fractionData != null) {
            int round = Integer.parseInt((String) this.fractionData.get(PatternKeys._ROUNDING));
            /*
             * if(round != 0){
             * patternInfo.setRound();
             * }
             */
            int digits = Integer.parseInt((String) this.fractionData.get(PatternKeys._DIGITS));
            int oldMinDigits = patternInfo.getMinimumFractionDigits();
            if (oldMinDigits == patternInfo.getMaximumFractionDigits()) {
                patternInfo.setMinimumFractionDigits(digits);
                patternInfo.setMaximumFractionDigits(digits);
            } else {
                patternInfo.setMinimumFractionDigits(Math.min(digits, oldMinDigits));
                patternInfo.setMaximumFractionDigits(digits);
            }
        }
    }

    private boolean isNegative(double number) {
        // Detecting whether a double is negative is easy with the exception of the value
        // -0.0. This is a double which has a zero mantissa (and exponent), but a negative
        // sign bit. It is semantically distinct from a zero with a positive sign bit, and
        // this distinction is important to certain kinds of computations. However, it's a
        // little tricky to detect, since (-0.0 == 0.0) and !(-0.0 < 0.0). Use the Double.equals test
    	// where if d1 represents +0.0 while d2 represents -0.0, or vice versa, 
    	// it will return false, even though +0.0==-0.0 has the value true.
        return (number < 0.0) || (number == 0.0 && Double.valueOf(number).equals(-0.0));
    }
}
