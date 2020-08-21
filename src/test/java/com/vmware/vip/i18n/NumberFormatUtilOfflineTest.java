/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.instances.NumberFormatting;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class NumberFormatUtilOfflineTest extends BaseTestClass {

    NumberFormatting numberFormatI18n;

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
        numberFormatI18n = (NumberFormatting) i18n.getFormattingInstance(NumberFormatting.class);
    }

    @Test
    public void testRegionFormatNumber() {
        long num1 = 201703;
        double num2 = 201703.54;
        String num3 = "201703.5416926";

        String language = "zh-Hans";
        String region = "CN";

        Assert.assertEquals("201,703", numberFormatI18n.formatNumber(num1,
                language, region));
        Assert.assertEquals("201,703", numberFormatI18n.formatNumber(num1,
                -1, language, region));
        Assert.assertEquals("201,703.00", numberFormatI18n.formatNumber(num1,
                2, language, region));
        Assert.assertEquals("201,703.00000", numberFormatI18n.formatNumber(num1,
                5, language, region));

        Assert.assertEquals("201,703.54", numberFormatI18n.formatNumber(num2,
                language, region));
        Assert.assertEquals("201,704", numberFormatI18n.formatNumber(num2,
                -1, language, region));
        Assert.assertEquals("201,703.54", numberFormatI18n.formatNumber(num2,
                2, language, region));
        Assert.assertEquals("201,703.54000", numberFormatI18n.formatNumber(num2,
                5, language, region));

        Assert.assertEquals("201,703.542", numberFormatI18n.formatNumber(num3,
                language, region));
        Assert.assertEquals("201,704", numberFormatI18n.formatNumber(num3,
                -1, language, region));
        Assert.assertEquals("201,703.54", numberFormatI18n.formatNumber(num3,
                2, language, region));
        Assert.assertEquals("201,703.54169", numberFormatI18n.formatNumber(num3,
                5, language, region));

        String frlanguage = "fr";
        String frregion = "FR";

        Assert.assertEquals("201 703", numberFormatI18n.formatNumber(num1,
                frlanguage, frregion));
        Assert.assertEquals("201 703", numberFormatI18n.formatNumber(num1,
                -10, frlanguage, frregion));
        Assert.assertEquals("201 703,000", numberFormatI18n.formatNumber(num1,
                3, frlanguage, frregion));
        Assert.assertEquals("201 703,0000", numberFormatI18n.formatNumber(num1,
                4, frlanguage, frregion));
    }

    @Test
    public void testFormatNumber() {
        long num1 = 201703;
        double num2 = 201703.54;
        String num3 = "201703.5416926";

        final Locale zhLocale = new Locale("zh", "CN");
        Assert.assertEquals("201,703", numberFormatI18n.formatNumber(num1,
                zhLocale));
        Assert.assertEquals("201,703", numberFormatI18n.formatNumber(num1,
                -1, zhLocale));
        Assert.assertEquals("201,703.00", numberFormatI18n.formatNumber(num1,
                2, zhLocale));
        Assert.assertEquals("201,703.00000", numberFormatI18n.formatNumber(num1,
                5, zhLocale));

        Assert.assertEquals("201,703.54", numberFormatI18n.formatNumber(num2,
                zhLocale));
        Assert.assertEquals("201,704", numberFormatI18n.formatNumber(num2,
                -1, zhLocale));
        Assert.assertEquals("201,703.54", numberFormatI18n.formatNumber(num2,
                2, zhLocale));
        Assert.assertEquals("201,703.54000", numberFormatI18n.formatNumber(num2,
                5, zhLocale));

        Assert.assertEquals("201,703.542", numberFormatI18n.formatNumber(num3,
                zhLocale));
        Assert.assertEquals("201,704", numberFormatI18n.formatNumber(num3,
                -1, zhLocale));
        Assert.assertEquals("201,703.54", numberFormatI18n.formatNumber(num3,
                2, zhLocale));
        Assert.assertEquals("201,703.54169", numberFormatI18n.formatNumber(num3,
                5, zhLocale));

        final Locale frLocale = new Locale("fr", "");
        Assert.assertEquals("201 703", numberFormatI18n.formatNumber(num1,
                frLocale));
        Assert.assertEquals("201 703", numberFormatI18n.formatNumber(num1,
                -10, frLocale));
        Assert.assertEquals("201 703,000", numberFormatI18n.formatNumber(num1,
                3, frLocale));
        Assert.assertEquals("201 703,0000", numberFormatI18n.formatNumber(num1,
                4, frLocale));
    }

    @Test
    public void testFormatPercent() {
        double num1 = 0.354;

        final Locale zhLocale = new Locale("zh", "CN");
        Assert.assertEquals("35%", numberFormatI18n.formatPercent(num1,
                zhLocale));
        Assert.assertEquals("35%", numberFormatI18n.formatPercent(num1,
                -1, zhLocale));
        Assert.assertEquals("35.40%", numberFormatI18n.formatPercent(num1,
                2, zhLocale));

        final Locale frLocale = new Locale("fr", "");
        Assert.assertEquals("35 %", numberFormatI18n.formatPercent(num1,
                frLocale));
        Assert.assertEquals("35 %", numberFormatI18n.formatPercent(num1,
                -3, frLocale));
        Assert.assertEquals("35,40000 %", numberFormatI18n.formatPercent(num1,
                5, frLocale));
    }

    @Test
    public void testRegionFormatPercent() {
        double num1 = 0.354;

        String language = "zh-Hans";
        String region = "CN";
        ;
        Assert.assertEquals("35%", numberFormatI18n.formatPercent(num1,
                language, region));
        Assert.assertEquals("35%", numberFormatI18n.formatPercent(num1,
                -1, language, region));
        Assert.assertEquals("35.40%", numberFormatI18n.formatPercent(num1,
                2, language, region));

        String frlanguage = "fr";
        String frregion = "FR";

        Assert.assertEquals("35 %", numberFormatI18n.formatPercent(num1,
                frlanguage, frregion));
        Assert.assertEquals("35 %", numberFormatI18n.formatPercent(num1,
                -3, frlanguage, frregion));
        Assert.assertEquals("35,40000 %", numberFormatI18n.formatPercent(num1,
                5, frlanguage, frregion));
    }

    @Test
    public void testFormatCurrency() {
        String currencyCode = "JPY";
        long num1 = 201703;
        double num2 = 201703.54;
        String num3 = "201704.5456926";

        final Locale zhLocale = new Locale("zh", "CN");
        Assert.assertEquals("US$201,703.00", numberFormatI18n.formatCurrency(
                num1, zhLocale));
        Assert.assertEquals("JP¥201,703", numberFormatI18n.formatCurrency(
                num1, currencyCode, zhLocale));
        /*
         * Assert.assertEquals("￥201,703", numberFormatI18n.formatCurrency(
         * num1, -1, zhLocale));
         * Assert.assertEquals("￥201,703.0", numberFormatI18n.formatCurrency(
         * num1, 1, zhLocale));
         * Assert.assertEquals("￥201,703.00000", numberFormatI18n
         * .formatCurrency(num1, 5, zhLocale));
         */

        Assert.assertEquals("US$201,703.54", numberFormatI18n.formatCurrency(
                num2, zhLocale));
        Assert.assertEquals("JP¥201,704", numberFormatI18n.formatCurrency(
                num2, currencyCode, zhLocale));
        /*
         * Assert.assertEquals("￥201,704", numberFormatI18n.formatCurrency(
         * num2, -1, zhLocale));
         * Assert.assertEquals("￥201,703.5", numberFormatI18n.formatCurrency(
         * num2, 1, zhLocale));
         * Assert.assertEquals("￥201,703.54000", numberFormatI18n
         * .formatCurrency(num2, 5, zhLocale));
         */

        Assert.assertEquals("US$201,704.55", numberFormatI18n.formatCurrency(
                num3, zhLocale));
        Assert.assertEquals("JP¥201,705", numberFormatI18n.formatCurrency(
                num3, currencyCode, zhLocale));
        /*
         * Assert.assertEquals("￥201,704", numberFormatI18n.formatCurrency(
         * num3, -1, zhLocale));
         * Assert.assertEquals("￥201,703.5", numberFormatI18n.formatCurrency(
         * num3, 1, zhLocale));
         * Assert.assertEquals("￥201,703.54169", numberFormatI18n
         * .formatCurrency(num3, 5, zhLocale));
         */

        final Locale frLocale = new Locale("fr", "");
        Assert.assertEquals("201 703,00 $US", numberFormatI18n
                .formatCurrency(num1, frLocale));
        Assert.assertEquals("201 703 JPY", numberFormatI18n
                .formatCurrency(num1, currencyCode, frLocale));
        /*
         * Assert.assertEquals("201 703 €", numberFormatI18n.formatCurrency(
         * num1, -1, frLocale));
         * Assert.assertEquals("201 703,00 €", numberFormatI18n
         * .formatCurrency(num1, 2, frLocale));
         * Assert.assertEquals("201 703,00000 €", numberFormatI18n
         * .formatCurrency(num1, 5, frLocale));
         */
    }

    @Test
    public void testRegionFormatCurrency() {
        String currencyCode = "JPY";
        long num1 = 201703;
        double num2 = 201703.54;
        String num3 = "201704.5456926";

        String language = "zh-Hans";
        String region = "CN";

        Assert.assertEquals("US$201,703.00", numberFormatI18n.formatCurrency(
                num1, language, region));
        Assert.assertEquals("JP¥201,703", numberFormatI18n.formatCurrency(
                num1, currencyCode, language, region));

        Assert.assertEquals("US$201,703.54", numberFormatI18n.formatCurrency(
                num2, language, region));
        Assert.assertEquals("JP¥201,704", numberFormatI18n.formatCurrency(
                num2, currencyCode, language, region));

        Assert.assertEquals("US$201,704.55", numberFormatI18n.formatCurrency(
                num3, language, region));
        Assert.assertEquals("JP¥201,705", numberFormatI18n.formatCurrency(
                num3, currencyCode, language, region));

        String frlanguage = "fr";
        String frregion = "FR";

        Assert.assertEquals("201 703,00 $US", numberFormatI18n
                .formatCurrency(num1, frlanguage, frregion));
        Assert.assertEquals("201 703 JPY", numberFormatI18n
                .formatCurrency(num1, currencyCode, frlanguage, frregion));

    }
}
