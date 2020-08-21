/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.instances.DateFormatting;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Locale;

public class DateFormatUtilOfflineTest extends BaseTestClass {

    final String fullDateForEn = "Monday, November 20, 2017";
    final String longDateForEn = "November 20, 2017";
    final String mediumDateForEn = "Nov 20, 2017";
    final String shortDateForEn = "11/20/17";
    final String fullTimeForEn = "1:39:24 PM GMT+08:00";
    final String longTimeForEn = "1:39:24 PM GMT+8";
    final String mediumTimeForEn = "1:39:24 PM";
    final String shortTimeForEn = "1:39 PM";
    final String fullForEn = "Monday, November 20, 2017 at 1:39:24 PM GMT+08:00";
    final String longForEn = "November 20, 2017 at 1:39:24 PM GMT+8";
    final String mediumForEn = "Nov 20, 2017, 1:39:24 PM";
    final String shortForEn = "11/20/17, 1:39 PM";

    final String fullDateForZh = "2017年11月20日星期一";
    final String longDateForZh = "2017年11月20日";
    final String mediumDateForZh = "2017年11月20日";
    final String shortDateForZh = "2017/11/20";
    final String fullTimeForZh = "GMT+08:00 下午1:39:24";
    final String longTimeForZh = "GMT+8 下午1:39:24";
    final String mediumTimeForZh = "下午1:39:24";
    final String shortTimeForZh = "下午1:39";
    final String fullForZh = "2017年11月20日星期一 GMT+08:00 下午1:39:24";
    final String longForZh = "2017年11月20日 GMT+8 下午1:39:24";
    final String mediumForZh = "2017年11月20日 下午1:39:24";
    final String shortForZh = "2017/11/20 下午1:39";

    final String fullDateForFr = "lundi 20 novembre 2017";
    final String longDateForFr = "20 novembre 2017";
    final String mediumDateForFr = "20 nov. 2017";
    final String shortDateForFr = "20/11/2017";
    final String fullTimeForFr = "13:39:24 GMT+08:00";
    final String longTimeForFr = "13:39:24 GMT+8";
    final String mediumTimeForFr = "13:39:24";
    final String shortTimeForFr = "13:39";
    final String fullForFr = "lundi 20 novembre 2017 à 13:39:24 GMT+08:00";
    final String longForFr = "20 novembre 2017 à 13:39:24 GMT+8";
    final String mediumForFr = "20 nov. 2017 à 13:39:24";
    final String shortForFr = "20/11/2017 13:39";

    final long timestamp = 1511156364801l;
    final String timeZone = "GMT+8";
    Date date = new Date(timestamp);

    DateFormatting dateFormatI18n;

    @Before
    public void init() {
        VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig-offline");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
        gc.initializeVIPService();
        gc.createFormattingCache(FormattingCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        dateFormatI18n = (DateFormatting) i18n.getFormattingInstance(DateFormatting.class);
    }

    @Test
    public void testFormatDateWithLocale() {

        Locale locale = new Locale("en", "US");
        Assert.assertEquals(fullDateForEn, dateFormatI18n.formatDate(date,
                "fullDate", timeZone, locale));
        Assert.assertEquals(longDateForEn, dateFormatI18n.formatDate(date,
                "longDate", timeZone, locale));
        Assert.assertEquals(mediumDateForEn, dateFormatI18n.formatDate(
                date, "mediumDate", timeZone, locale));
        Assert.assertEquals(shortDateForEn, dateFormatI18n.formatDate(date,
                "shortDate", timeZone, locale));
        Assert.assertEquals(fullTimeForEn, dateFormatI18n.formatDate(date,
                "fullTime", timeZone, locale));
        Assert.assertEquals(longTimeForEn, dateFormatI18n.formatDate(date,
                "longTime", timeZone, locale));
        Assert.assertEquals(mediumTimeForEn, dateFormatI18n.formatDate(
                date, "mediumTime", timeZone, locale));
        Assert.assertEquals(shortTimeForEn, dateFormatI18n.formatDate(date,
                "shortTime", timeZone, locale));
        Assert.assertEquals(fullForEn, dateFormatI18n.formatDate(date,
                "full", timeZone, locale));
        Assert.assertEquals(longForEn, dateFormatI18n.formatDate(date,
                "long", timeZone, locale));
        Assert.assertEquals(mediumForEn, dateFormatI18n.formatDate(date,
                "medium", timeZone, locale));
        Assert.assertEquals(shortForEn, dateFormatI18n.formatDate(date,
                "short", timeZone, locale));

        locale = new Locale("zh", "CN");
        Assert.assertEquals(fullDateForZh, dateFormatI18n.formatDate(date,
                "fullDate", timeZone, locale));
        Assert.assertEquals(longDateForZh, dateFormatI18n.formatDate(date,
                "longDate", timeZone, locale));
        Assert.assertEquals(mediumDateForZh, dateFormatI18n.formatDate(
                date, "mediumDate", timeZone, locale));
        Assert.assertEquals(shortDateForZh, dateFormatI18n.formatDate(date,
                "shortDate", timeZone, locale));
        Assert.assertEquals(fullTimeForZh, dateFormatI18n.formatDate(date,
                "fullTime", timeZone, locale));
        Assert.assertEquals(longTimeForZh, dateFormatI18n.formatDate(date,
                "longTime", timeZone, locale));
        Assert.assertEquals(mediumTimeForZh, dateFormatI18n.formatDate(
                date, "mediumTime", timeZone, locale));
        Assert.assertEquals(shortTimeForZh, dateFormatI18n.formatDate(date,
                "shortTime", timeZone, locale));
        Assert.assertEquals(fullForZh, dateFormatI18n.formatDate(date,
                "full", timeZone, locale));
        Assert.assertEquals(longForZh, dateFormatI18n.formatDate(date,
                "long", timeZone, locale));
        Assert.assertEquals(mediumForZh, dateFormatI18n.formatDate(date,
                "medium", timeZone, locale));
        Assert.assertEquals(shortForZh, dateFormatI18n.formatDate(date,
                "short", timeZone, locale));

        locale = new Locale("fr", "");
        Assert.assertEquals(fullDateForFr, dateFormatI18n.formatDate(date,
                "fullDate", timeZone, locale));
        Assert.assertEquals(longDateForFr, dateFormatI18n.formatDate(date,
                "longDate", timeZone, locale));
        Assert.assertEquals(mediumDateForFr, dateFormatI18n.formatDate(
                date, "mediumDate", timeZone, locale));
        Assert.assertEquals(shortDateForFr, dateFormatI18n.formatDate(date,
                "shortDate", timeZone, locale));
        Assert.assertEquals(fullTimeForFr, dateFormatI18n.formatDate(date,
                "fullTime", timeZone, locale));
        Assert.assertEquals(longTimeForFr, dateFormatI18n.formatDate(date,
                "longTime", timeZone, locale));
        Assert.assertEquals(mediumTimeForFr, dateFormatI18n.formatDate(
                date, "mediumTime", timeZone, locale));
        Assert.assertEquals(shortTimeForFr, dateFormatI18n.formatDate(date,
                "shortTime", timeZone, locale));
        Assert.assertEquals(fullForFr, dateFormatI18n.formatDate(date,
                "full", timeZone, locale));
        Assert.assertEquals(longForFr, dateFormatI18n.formatDate(date,
                "long", timeZone, locale));
        Assert.assertEquals(mediumForFr, dateFormatI18n.formatDate(date,
                "medium", timeZone, locale));
        Assert.assertEquals(shortForFr, dateFormatI18n.formatDate(date,
                "short", timeZone, locale));

        //Test invalid locale fallback to en
        locale = new Locale("aa", "");
        Assert.assertEquals(fullDateForEn, dateFormatI18n.formatDate(date,
                "fullDate", timeZone, locale));
        Assert.assertEquals(longDateForEn, dateFormatI18n.formatDate(date,
                "longDate", timeZone, locale));
        Assert.assertEquals(mediumDateForEn, dateFormatI18n.formatDate(
                date, "mediumDate", timeZone, locale));
        Assert.assertEquals(shortDateForEn, dateFormatI18n.formatDate(date,
                "shortDate", timeZone, locale));
        Assert.assertEquals(fullTimeForEn, dateFormatI18n.formatDate(date,
                "fullTime", timeZone, locale));
        Assert.assertEquals(longTimeForEn, dateFormatI18n.formatDate(date,
                "longTime", timeZone, locale));
        Assert.assertEquals(mediumTimeForEn, dateFormatI18n.formatDate(
                date, "mediumTime", timeZone, locale));
        Assert.assertEquals(shortTimeForEn, dateFormatI18n.formatDate(date,
                "shortTime", timeZone, locale));
        Assert.assertEquals(fullForEn, dateFormatI18n.formatDate(date,
                "full", timeZone, locale));
        Assert.assertEquals(longForEn, dateFormatI18n.formatDate(date,
                "long", timeZone, locale));
        Assert.assertEquals(mediumForEn, dateFormatI18n.formatDate(date,
                "medium", timeZone, locale));
        Assert.assertEquals(shortForEn, dateFormatI18n.formatDate(date,
                "short", timeZone, locale));
    }

    @Test
    public void testFormatDateWithLanguageRegion() {
        String language = "en";
        String region = "US";
        Assert.assertEquals(fullDateForEn, dateFormatI18n.formatDate(date,
                "fullDate", timeZone, language, region));
        Assert.assertEquals(longDateForEn, dateFormatI18n.formatDate(date,
                "longDate", timeZone, language, region));
        Assert.assertEquals(mediumDateForEn, dateFormatI18n.formatDate(
                date, "mediumDate", timeZone, language, region));
        Assert.assertEquals(shortDateForEn, dateFormatI18n.formatDate(date,
                "shortDate", timeZone, language, region));
        Assert.assertEquals(fullTimeForEn, dateFormatI18n.formatDate(date,
                "fullTime", timeZone, language, region));
        Assert.assertEquals(longTimeForEn, dateFormatI18n.formatDate(date,
                "longTime", timeZone, language, region));
        Assert.assertEquals(mediumTimeForEn, dateFormatI18n.formatDate(
                date, "mediumTime", timeZone, language, region));
        Assert.assertEquals(shortTimeForEn, dateFormatI18n.formatDate(date,
                "shortTime", timeZone, language, region));
        Assert.assertEquals(fullForEn, dateFormatI18n.formatDate(date,
                "full", timeZone, language, region));
        Assert.assertEquals(longForEn, dateFormatI18n.formatDate(date,
                "long", timeZone, language, region));
        Assert.assertEquals(mediumForEn, dateFormatI18n.formatDate(date,
                "medium", timeZone, language, region));
        Assert.assertEquals(shortForEn, dateFormatI18n.formatDate(date,
                "short", timeZone, language, region));

        language = "zh-Hans";
        region = "CN";
        Assert.assertEquals(fullDateForZh, dateFormatI18n.formatDate(date,
                "fullDate", timeZone, language, region));
        Assert.assertEquals(longDateForZh, dateFormatI18n.formatDate(date,
                "longDate", timeZone, language, region));
        Assert.assertEquals(mediumDateForZh, dateFormatI18n.formatDate(
                date, "mediumDate", timeZone, language, region));
        Assert.assertEquals(shortDateForZh, dateFormatI18n.formatDate(date,
                "shortDate", timeZone, language, region));
        Assert.assertEquals(fullTimeForZh, dateFormatI18n.formatDate(date,
                "fullTime", timeZone, language, region));
        Assert.assertEquals(longTimeForZh, dateFormatI18n.formatDate(date,
                "longTime", timeZone, language, region));
        Assert.assertEquals(mediumTimeForZh, dateFormatI18n.formatDate(
                date, "mediumTime", timeZone, language, region));
        Assert.assertEquals(shortTimeForZh, dateFormatI18n.formatDate(date,
                "shortTime", timeZone, language, region));
        Assert.assertEquals(fullForZh, dateFormatI18n.formatDate(date,
                "full", timeZone, language, region));
        Assert.assertEquals(longForZh, dateFormatI18n.formatDate(date,
                "long", timeZone, language, region));
        Assert.assertEquals(mediumForZh, dateFormatI18n.formatDate(date,
                "medium", timeZone, language, region));
        Assert.assertEquals(shortForZh, dateFormatI18n.formatDate(date,
                "short", timeZone, language, region));

        language = "fr";
        region = "FR";
        // final Locale frLocale = new Locale("fr", "");
        Assert.assertEquals(fullDateForFr, dateFormatI18n.formatDate(date,
                "fullDate", timeZone, language, region));
        Assert.assertEquals(longDateForFr, dateFormatI18n.formatDate(date,
                "longDate", timeZone, language, region));
        Assert.assertEquals(mediumDateForFr, dateFormatI18n.formatDate(
                date, "mediumDate", timeZone, language, region));
        Assert.assertEquals(shortDateForFr, dateFormatI18n.formatDate(date,
                "shortDate", timeZone, language, region));
        Assert.assertEquals(fullTimeForFr, dateFormatI18n.formatDate(date,
                "fullTime", timeZone, language, region));
        Assert.assertEquals(longTimeForFr, dateFormatI18n.formatDate(date,
                "longTime", timeZone, language, region));
        Assert.assertEquals(mediumTimeForFr, dateFormatI18n.formatDate(
                date, "mediumTime", timeZone, language, region));
        Assert.assertEquals(shortTimeForFr, dateFormatI18n.formatDate(date,
                "shortTime", timeZone, language, region));
        Assert.assertEquals(fullForFr, dateFormatI18n.formatDate(date,
                "full", timeZone, language, region));
        Assert.assertEquals(longForFr, dateFormatI18n.formatDate(date,
                "long", timeZone, language, region));
        Assert.assertEquals(mediumForFr, dateFormatI18n.formatDate(date,
                "medium", timeZone, language, region));
        Assert.assertEquals(shortForFr, dateFormatI18n.formatDate(date,
                "short", timeZone, language, region));

        //Test invalid locale fallback to en
        language = "aa";
        region = "US";
        Assert.assertEquals(fullDateForEn, dateFormatI18n.formatDate(date,
                "fullDate", timeZone, language, region));
        Assert.assertEquals(longDateForEn, dateFormatI18n.formatDate(date,
                "longDate", timeZone, language, region));
        Assert.assertEquals(mediumDateForEn, dateFormatI18n.formatDate(
                date, "mediumDate", timeZone, language, region));
        Assert.assertEquals(shortDateForEn, dateFormatI18n.formatDate(date,
                "shortDate", timeZone, language, region));
        Assert.assertEquals(fullTimeForEn, dateFormatI18n.formatDate(date,
                "fullTime", timeZone, language, region));
        Assert.assertEquals(longTimeForEn, dateFormatI18n.formatDate(date,
                "longTime", timeZone, language, region));
        Assert.assertEquals(mediumTimeForEn, dateFormatI18n.formatDate(
                date, "mediumTime", timeZone, language, region));
        Assert.assertEquals(shortTimeForEn, dateFormatI18n.formatDate(date,
                "shortTime", timeZone, language, region));
        Assert.assertEquals(fullForEn, dateFormatI18n.formatDate(date,
                "full", timeZone, language, region));
        Assert.assertEquals(longForEn, dateFormatI18n.formatDate(date,
                "long", timeZone, language, region));
        Assert.assertEquals(mediumForEn, dateFormatI18n.formatDate(date,
                "medium", timeZone, language, region));
        Assert.assertEquals(shortForEn, dateFormatI18n.formatDate(date,
                "short", timeZone, language, region));
    }

}
