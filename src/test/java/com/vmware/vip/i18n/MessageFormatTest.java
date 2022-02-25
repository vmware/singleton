/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.text.FieldPosition;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.l2.text.MessageFormat;

public class MessageFormatTest extends BaseTestClass {

    @Before
    public void init() {
        VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
        gc.initializeVIPService();
        gc.createFormattingCache(FormattingCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
    }

    @Test
    public void testFormatPluralMessage() {
        String message1 = "{num_files, plural, "
                + "=0{There are no files on disk \"{disk_name}\".}"
                + "=1{There is one file on disk \"{disk_name}\".}"
                + "other{There are # files on disk \"{disk_name}\".}}";
        MessageFormat msgFmt1 = new MessageFormat(message1, Locale.ENGLISH);
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("num_files", 0);//
        args.put("disk_name", "MyDisk");
        Assert.assertEquals("There are no files on disk \"MyDisk\".",
                msgFmt1.format(args, new StringBuilder(), new FieldPosition(0)).toString());
        args.put("num_files", 1);
        Assert.assertEquals("There is one file on disk \"MyDisk\".",
                msgFmt1.format(args, new StringBuilder(), new FieldPosition(0)).toString());
        args.put("num_files", 345678);
        Assert.assertEquals("There are 345,678 files on disk \"MyDisk\".",
                msgFmt1.format(args, new StringBuilder(), new FieldPosition(0)).toString());

        String message2 = "{num_files, plural, " +
                "one{There is one file on disk \"{disk_name}\".}" +
                "other{There are # files on disk \"{disk_name}\".}}";
        MessageFormat msgFmt2 = new MessageFormat(message2, Locale.ENGLISH);
        args.put("num_files", 0);
        Assert.assertEquals("There are 0 files on disk \"MyDisk\".",
                msgFmt2.format(args, new StringBuilder(), new FieldPosition(0)).toString());
        args.put("num_files", 1);
        Assert.assertEquals("There is one file on disk \"MyDisk\".",
                msgFmt2.format(args, new StringBuilder(), new FieldPosition(0)).toString());
        args.put("num_files", 345678);
        Assert.assertEquals("There are 345,678 files on disk \"MyDisk\".",
                msgFmt2.format(args, new StringBuilder(), new FieldPosition(0)).toString());

        String message3 = "There {0, plural, one{is one file} other{are # files}} on disk \"{1}\".";// {1, number}
        MessageFormat msgFmt3 = new MessageFormat(message3, Locale.ENGLISH);
        Assert.assertEquals("There are 0 files on disk \"MyDisk\".",
                msgFmt3.format(new Object[] { 0, "MyDisk" }, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("There is one file on disk \"MyDisk\".",
                msgFmt3.format(new Object[] { 1, "MyDisk" }, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("There are 345,678 files on disk \"MyDisk\".", msgFmt3
                .format(new Object[] { 345678, "MyDisk" }, new StringBuilder(), new FieldPosition(0)).toString());

        String msgPatSl = "{0,plural, one{# pes} two{# psa} few{# psi} other{# psov}}";
        MessageFormat msgFmt4 = new MessageFormat(msgPatSl, new Locale("sl"));
        Assert.assertEquals("0 psov",
                msgFmt4.format(new Object[] { 0 }, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("1 pes",
                msgFmt4.format(new Object[] { 1 }, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("2 psa",
                msgFmt4.format(new Object[] { 2 }, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("3 psi",
                msgFmt4.format(new Object[] { 3 }, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("4 psi",
                msgFmt4.format(new Object[] { 4 }, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("5 psov",
                msgFmt4.format(new Object[] { 5 }, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("10 psov",
                msgFmt4.format(new Object[] { 10 }, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("100 psov",
                msgFmt4.format(new Object[] { 100 }, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("101 pes",
                msgFmt4.format(new Object[] { 101 }, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("102 psa",
                msgFmt4.format(new Object[] { 102 }, new StringBuilder(), new FieldPosition(0)).toString());
    }

    @Test
    public void testFormatMessageWithSimpleArg(){
       Locale locale = null;
        // test argument type: number,
        // style: none, percent, currency
        long num1 = 201703;
        double num2 = 201703.54;
        String num3 = "201704.5456926";
        double num4 = 0.354;

        String messageWithSimpleArg_number =
                "number: {0,number} {1,number} {2,number} " +
                "percent: {3,number, percent} " +
                "currency: {4,number, currency} {5,number, currency} {6,number, currency}";
        Object[] arguments4Number = {
                num1, num2, num3,
                num4,
                num1, num2, num3,};

        String expectedNumberFormat_en =
                "number: 201,703 201,703.54 201,704.546 " +
                "percent: 35% " +
                "currency: $201,703.00 $201,703.54 $201,704.55";
        locale = new Locale("en", "");
        MessageFormat msgFmt4Number_en = new MessageFormat(messageWithSimpleArg_number, locale);
        System.out.println("en number format result: "+ msgFmt4Number_en.format(arguments4Number, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals(expectedNumberFormat_en,
                msgFmt4Number_en.format(arguments4Number, new StringBuilder(), new FieldPosition(0)).toString());

        String expectedNumberFormat_fr =
                "number: 201 703 201 703,54 201 704,546 " +
                "percent: 35 % " +
                "currency: 201 703,00 $US 201 703,54 $US 201 704,55 $US";
        locale = new Locale("fr", "");
        MessageFormat msgFmt4Number_fr = new MessageFormat(messageWithSimpleArg_number, locale);
        System.out.println("fr number format result: "+ msgFmt4Number_fr.format(arguments4Number, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals(expectedNumberFormat_fr,
                msgFmt4Number_fr.format(arguments4Number, new StringBuilder(), new FieldPosition(0)).toString());

        // test argument type: date,
        // style: fullDate, longDate, mediumDate, shortDate, fullTime, longTime, mediumTime, shortTime, full, long, medium, short
        final long timestamp = 1511156364801l;
        //final String timeZone = "GMT+8";
        Date date = new Date(timestamp);

        String messageWithSimpleArg_date =
                "default style is mediumDateTime: {0, date} " +
                "fullDate: {1, date, fullDate} longDate: {2, date, longDate} mediumDate: {3, date, mediumDate} shortDate: {4, date, shortDate} " +
                "fullTime: {5, date, fullTime} longTime: {6, date, longTime} mediumTime: {7, date, mediumTime} shortTime: {8, date, shortTime} " +
                "fullDateTime: {9, date, full} longDateTime: {10, date, long} mediumDateTime: {11, date, medium} shortDateTime: {12, date, short}";
        Object[] arguments4Date = {
                1511156364801l,
                1511156364801l, date, 1511156364801l, date,
                1511156364801l, date, 1511156364801l, date,
                1511156364801l, date, 1511156364801l, date
        };

        String expectedDateFormat_en =
                "default style is mediumDateTime: Nov 20, 2017, 1:39:24 PM " +
                "fullDate: Monday, November 20, 2017 longDate: November 20, 2017 mediumDate: Nov 20, 2017 shortDate: 11/20/17 " +
                "fullTime: 1:39:24 PM GMT+08:00 longTime: 1:39:24 PM GMT+8 mediumTime: 1:39:24 PM shortTime: 1:39 PM " +
                "fullDateTime: Monday, November 20, 2017 at 1:39:24 PM GMT+08:00 longDateTime: November 20, 2017 at 1:39:24 PM GMT+8 mediumDateTime: Nov 20, 2017, 1:39:24 PM shortDateTime: 11/20/17, 1:39 PM";
        locale = new Locale("en", "");
        MessageFormat msgFmt4Date_en = new MessageFormat(messageWithSimpleArg_date, locale);
        System.out.println("en date format result: "+ msgFmt4Date_en.format(arguments4Date, new StringBuilder(), new FieldPosition(0)).toString());
        //comment out this comparision temporarily due to the actual result is different on different timezone,
        //but the expected result is hardcode on Chinese timezone, which leads to comparision failure on non-Chinese timezone.
        /*Assert.assertEquals(expectedDateFormat_en,
                msgFmt4Date_en.format(arguments4Date, new StringBuilder(), new FieldPosition(0)).toString());*/

        String expectedDateFormat_fr =
                "default style is mediumDateTime: 20 nov. 2017 à 13:39:24 " +
                "fullDate: lundi 20 novembre 2017 longDate: 20 novembre 2017 mediumDate: 20 nov. 2017 shortDate: 20/11/2017 " +
                "fullTime: 13:39:24 GMT+08:00 longTime: 13:39:24 GMT+8 mediumTime: 13:39:24 shortTime: 13:39 " +
                "fullDateTime: lundi 20 novembre 2017 à 13:39:24 GMT+08:00 longDateTime: 20 novembre 2017 à 13:39:24 GMT+8 mediumDateTime: 20 nov. 2017 à 13:39:24 shortDateTime: 20/11/2017 13:39";
        locale = new Locale("fr", "");
        MessageFormat msgFmt4Date_fr = new MessageFormat(messageWithSimpleArg_date, locale);
        System.out.println("fr date format result: "+ msgFmt4Date_fr.format(arguments4Date, new StringBuilder(), new FieldPosition(0)).toString());
        //comment out this comparision temporarily due to the actual result is different on different timezone,
        //but the expected result is hardcode on Chinese timezone, which leads to comparision failure on non-Chinese timezone.
        /*Assert.assertEquals(expectedDateFormat_fr,
                msgFmt4Date_fr.format(arguments4Date, new StringBuilder(), new FieldPosition(0)).toString());*/



        String messageWithSimpleArg = "At {1,date,shortTime} on {1,date,longDate}, there was {2} on planet {0,number}.";
        Object[] arguments = {
                7,
                new Date(timestamp),
                "a disturbance in the Force"
        };

        MessageFormat msgFmt1 = new MessageFormat(messageWithSimpleArg, Locale.ENGLISH);
        System.out.println(msgFmt1.format(arguments, new StringBuilder(), new FieldPosition(0)).toString());
        //comment out this comparision temporarily due to the actual result is different on different timezone,
        //but the expected result is hardcode on Chinese timezone, which leads to comparision failure on non-Chinese timezone.
        /*Assert.assertEquals("At 1:39 PM on November 20, 2017, there was a disturbance in the Force on planet 7.",
                msgFmt1.format(arguments, new StringBuilder(), new FieldPosition(0)).toString());*/
    }
}
