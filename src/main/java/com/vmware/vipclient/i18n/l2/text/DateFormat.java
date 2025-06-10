/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.vmware.vipclient.i18n.l2.common.ConstantChars;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.util.FormatUtils;

public abstract class DateFormat {
    public final static int    DATETIME = 0;
    public final static int    DATE     = 1;
    public final static int    TIME     = 2;

    /**
     * {@icu} Constant for empty style pattern.
     * 
     * @stable ICU 3.8
     */
    public static final int    NONE     = -1;

    /**
     * Constant for full style pattern.
     * 
     * @stable ICU 2.0
     */
    public static final String FULL     = "full";

    /**
     * Constant for long style pattern.
     * 
     * @stable ICU 2.0
     */
    public static final String LONG     = "long";

    /**
     * Constant for medium style pattern.
     * 
     * @stable ICU 2.0
     */
    public static final String MEDIUM   = "medium";

    /**
     * Constant for short style pattern.
     * 
     * @stable ICU 2.0
     */
    public static final String SHORT    = "short";

    /**
     * Constant for default style pattern. Its value is MEDIUM.
     * 
     * @stable ICU 2.0
     */
    public static final String DEFAULT  = MEDIUM;

    public static DateFormat getInstance(JSONObject formatData, String format, String locale) {
        String pattern = getPattern(formatData, format);
        DateFormat dateFormat = new SimpleDateFormat(pattern, formatData, locale);
        return dateFormat;
    }

    public static DateFormat getInstance(JSONObject formatData, String format, String language, String region) {
        String pattern = getPattern(formatData, format);
        DateFormat dateFormat = new RegionDateFormat(pattern, formatData, language, region);
        return dateFormat;
    }

    public static String getPattern(JSONObject formatData, String format) {
        String pattern;
        String patternType = null;
        String patternStyle = "";
        if (format.indexOf(PatternKeys.SHORT) != -1) {
            patternType = PatternKeys.SHORT;
        } else if (format.indexOf(PatternKeys.MEDIUM) != -1) {
            patternType = PatternKeys.MEDIUM;
        } else if (format.indexOf(PatternKeys.LONG) != -1) {
            patternType = PatternKeys.LONG;
        } else if (format.indexOf(PatternKeys.FULL) != -1) {
            patternType = PatternKeys.FULL;
        }
        if (patternType != null && !"".equals(patternType)) {
            if (format.indexOf(PatternKeys.DATE) != -1) {
                patternStyle = PatternKeys.DATEFORMATS;
            } else if (format.indexOf(PatternKeys.TIME) != -1) {
                patternStyle = PatternKeys.TIMEFORMATS;
            } else {
                patternStyle = PatternKeys.DATETIMEFORMATS;
            }
        }
        if (!"".equals(patternStyle)) {
            JSONObject patternObj = (JSONObject) formatData.get(patternStyle);
            pattern = (String) patternObj.get(patternType);
            if (PatternKeys.DATETIMEFORMATS.equalsIgnoreCase(patternStyle)) {
                String datePattern = (String) ((JSONObject) formatData.get(PatternKeys.DATEFORMATS))
                        .get(patternType);
                String timePattern = (String) ((JSONObject) formatData.get(PatternKeys.TIMEFORMATS))
                        .get(patternType);
                pattern = FormatUtils.format(pattern, timePattern, datePattern);
            }
        } else {
            pattern = format;
        }
        return pattern;
    }

    public String format(Object obj, String timeZone) {
        if (obj instanceof Calendar) {
            return format(((Calendar) obj).getTime(), timeZone);
        } else if (obj instanceof Date) {
            return format((Date) obj, timeZone);
        } else if (obj instanceof Number) {
            return format(new Date(((Number) obj).longValue()), timeZone);
        } else if (obj instanceof String) {
            Date date = parseStr2Date(obj.toString());
            return format(date, timeZone);
            // return obj.toString();
        } else {
            throw new IllegalArgumentException("Cannot format given Object ("
                    + obj.getClass().getName() + ") as a Date");
        }
    }

    public abstract String format(Date date, String timeZone);

    public Date parseStr2Date(String str) {
        if (isNumeric(str)) {
            long date;
            if (str.endsWith("l") || str.endsWith("L")) {
                str = str.substring(0, str.length() - 1);
            }
            if (str.indexOf(".") > 0) {
                date = new BigDecimal(str).longValue();
            } else {
                date = new BigInteger(str).longValue();
            }
            return new Date(date);
        } else {
            return getDateByString(str);
        }
    }

    public Date getDateByString(String dateStr) {
        if (dateStr.indexOf(ConstantChars.DOT) > 0 || dateStr.indexOf(ConstantChars.COMMA) > 0
                || dateStr.indexOf(ConstantChars.BACKSLASH) > 0) {
            dateStr = dateStr.replace(ConstantChars.DOT, ConstantChars.DASHLINE)
                    .replace(ConstantChars.COMMA, ConstantChars.DASHLINE)
                    .replace(ConstantChars.BACKSLASH, ConstantChars.DASHLINE);
        }
        if (dateStr.indexOf("T") < 0) {
            dateStr = dateStr.replace(ConstantChars.DASHLINE, ConstantChars.BACKSLASH);
            Date date = new Date(dateStr);
            return date;
        } else {
            return IsoToDate(dateStr);
        }
    }

    public Date IsoToDate(String isoDateStr) {
        Calendar cal = Calendar.getInstance();
        String[] dateTimeArray = isoDateStr.split("T");
        String[] dateArray = dateTimeArray[0].split("-");
        for (int i = 0; i < dateArray.length; i++) {
            switch (i) {
            case 0:
                cal.set(Calendar.YEAR, Integer.parseInt(dateArray[0]));
            case 1:
                cal.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1);
            case 2:
                cal.set(Calendar.DATE, Integer.parseInt(dateArray[2]));
            }
        }
        if (dateTimeArray.length > 1) {
            String timeStr = dateTimeArray[1];
            if (dateTimeArray[1].indexOf("+") > 0) {
                timeStr = dateTimeArray[1].substring(0, dateTimeArray[1].indexOf("+"));
            } else if (dateTimeArray[1].indexOf("-") > 0) {
                timeStr = dateTimeArray[1].substring(0, dateTimeArray[1].indexOf("-"));
            }
            String[] timeArray = timeStr.split(":");
            for (int i = 0; i < timeArray.length; i++) {
                switch (i) {
                case 0:
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
                case 1:
                    cal.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
                case 2:
                    cal.set(Calendar.SECOND, Integer.parseInt(timeArray[2]));
                }
            }
        }
        return cal.getTime();
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+([l,L]$)?");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
