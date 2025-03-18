/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.instances;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.l2.common.PatternCategory;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Provide some functions to get formatted date/time string.
 */
public class DateFormatting implements Formatting {

    public DateFormatting() {
        super();
    }

    /**
     * Get the formatted string of default time zone in specified pattern and specified Locale.
     * 
     * @param obj
     *            The object which represents date, it can be Date, Calendar, timestamp in long, or ISO string.
     *            For ISO string, please refer to https://www.w3.org/TR/NOTE-datetime.
     * @param pattern
     *            The format you want the date string show in. Currently VIP supports 12 formats, they are
     *            full, long, medium, short, fullDate, longDate, mediumDate, shortDate, fullTime, longTime,
     *            mediumTime, shortTime.
     *            The first four formats combine date and time together. For the real pattern each format
     *            represents
     * @param locale
     *            The locale for which the date format is desired.
     * @return
     *         The formatted date/time string.
     */
    public String formatDate(Object obj, String pattern, Locale locale) {
        return formatDate(obj, pattern, null, locale);
    }

    /**
     * Get the formatted string of specified time zone in specified pattern and specified Locale.
     * 
     * @param obj
     *            The object which represents date, it can be Date, Calendar, timestamp in long, or ISO
     *            string. For ISO string, please refer to https://www.w3.org/TR/NOTE-datetime.
     * @param pattern
     *            The format you want the date string show in. Currently VIP supports 12 formats, they are
     *            full, long, medium, short, fullDate, longDate, mediumDate, shortDate, fullTime, longTime,
     *            mediumTime, shortTime.
     *            The first four formats combine date and time together. For the real pattern each format
     *            represents
     * @param timeZone
     *            The ID for a TimeZone, such as "America/Los_Angeles", or a custom ID such as "GMT-8:00".
     * @param locale
     *            The locale for which the date format is desired.
     * @return
     *         The formatted date/time string.
     */
    public String formatDate(Object obj, String pattern, String timeZone, Locale locale) {
        // validate parameter
        if (null == obj || "".equalsIgnoreCase(obj.toString())) {
            return null;
        }
        if (null == pattern || "".equalsIgnoreCase(pattern)) {
            pattern = com.vmware.vipclient.i18n.l2.text.DateFormat.DEFAULT;
        }
        if (null == timeZone) {
            timeZone = "";// TimeZone.getDefault();
        }
        JSONObject dateFormatData = null;
        I18nFactory factory = I18nFactory.getInstance();
        if (factory == null) {
            throw new RuntimeException("I18nFactory is null, please create it first!");
        }
        PatternMessage p = (PatternMessage) factory.getMessageInstance(PatternMessage.class);
        JSONObject localeFormatData = p.getPatternMessage(locale);
        if(localeFormatData != null)
            dateFormatData = (JSONObject) localeFormatData.get(PatternCategory.DATES.toString());
        if (dateFormatData == null) {
            throw new RuntimeException("Can't format " + obj + " without pattern data!");
        }
        com.vmware.vipclient.i18n.l2.text.DateFormat dateFormat = com.vmware.vipclient.i18n.l2.text.DateFormat
                .getInstance(dateFormatData, pattern, locale.toLanguageTag());
        return dateFormat.format(obj, timeZone);
    }

    public String formatDate(Object obj, String pattern, String timeZone, String language, String region) {
        // validate parameter
        if (null == obj || "".equalsIgnoreCase(obj.toString())) {
            return null;
        }
        if (null == pattern) {
            pattern = com.vmware.vipclient.i18n.l2.text.DateFormat.DEFAULT;
        }
        if (null == timeZone) {
            timeZone = "";// TimeZone.getDefault();
        }
        JSONObject dateFormatData = null;
        I18nFactory factory = I18nFactory.getInstance();
        if (factory == null) {
            throw new RuntimeException("I18nFactory is null, please create it first!");
        }
        PatternMessage p = (PatternMessage) factory.getMessageInstance(PatternMessage.class);
        JSONObject localeFormatData = p.getPatternMessage(language, region);
        if(localeFormatData != null)
            dateFormatData = (JSONObject) localeFormatData.get(PatternCategory.DATES.toString());
        if (dateFormatData == null) {
            throw new RuntimeException("Can't format " + obj + " without pattern data!");
        }
        com.vmware.vipclient.i18n.l2.text.DateFormat dateFormat = com.vmware.vipclient.i18n.l2.text.DateFormat
                .getInstance(dateFormatData, pattern, language, region);
        return dateFormat.format(obj, timeZone);
    }

}
