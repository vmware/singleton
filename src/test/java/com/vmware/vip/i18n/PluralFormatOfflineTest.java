/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.l2.text.PluralFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.FieldPosition;
import java.util.Locale;

public class PluralFormatOfflineTest extends BaseTestClass {

    public PluralFormatOfflineTest() {
        // TODO Auto-generated constructor stub
    }

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
        String patEn = "one{one dog} other{# dogs}"; // English 'dog'
        String patSl = "one{# pes} two{# psa} few{# psi} other{# psov}"; // Slovenian translation of dog in Plural Form

        // Create a new PluralFormat for a given locale locale and pattern string
        PluralFormat plfmtEn = new PluralFormat(new Locale("en"), patEn);
        PluralFormat plfmtSl = new PluralFormat(new Locale("sl"), patSl);
        // Assert.assertEquals("0 dogs", plfmtEn.format(0).toString());
        // Assert.assertEquals("one dog", plfmtEn.format(1).toString());
        // Assert.assertEquals("345,678 dogs", plfmtEn.format(345678).toString());
        Assert.assertEquals("0 dogs", plfmtEn.format(0, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("one dog", plfmtEn.format(1, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("345,678 dogs",
                plfmtEn.format(345678, new StringBuilder(), new FieldPosition(0)).toString());

        Assert.assertEquals("0 psov", plfmtSl.format(0, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("1 pes", plfmtSl.format(1, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("2 psa", plfmtSl.format(2, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("3 psi", plfmtSl.format(3, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("4 psi", plfmtSl.format(4, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("5 psov", plfmtSl.format(5, new StringBuilder(), new FieldPosition(0)).toString());
        Assert.assertEquals("345.678 psov",
                plfmtSl.format(345678, new StringBuilder(), new FieldPosition(0)).toString());
    }
}
