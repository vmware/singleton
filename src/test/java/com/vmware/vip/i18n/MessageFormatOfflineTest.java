/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.l2.text.MessageFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.FieldPosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MessageFormatOfflineTest extends BaseTestClass {

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
    }

    @Test
    public void testFormat() {
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
}
