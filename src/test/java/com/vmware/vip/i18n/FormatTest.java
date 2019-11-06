/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.formats.DateFormat;
import com.vmware.vipclient.i18n.formats.NumberFormat;

public class FormatTest extends BaseTestClass {
    final String locale = "zh_CN";

    @Before
    public void init() {
        VIPCfg gc = VIPCfg.getInstance();
        try {
            gc.initialize("vipconfig");
        } catch (VIPClientInitException e) {
            logger.error(e.getMessage());
        }
        gc.initializeVIPService();
    }

    @Test
    public void testGetLocalizedDateFormat() {
        DateFormat dateFormat = new DateFormat();
        dateFormat.setLocale(locale);
        String formatStr = dateFormat.getLocalizedDateFormat("1472728030290",
                "MMMEd");
        Assert.assertTrue(formatStr.length() > 0);
    }

    @Test
    public void testGetLocalizedNumber() {
        NumberFormat numberFormat = new NumberFormat();
        numberFormat.setLocale(locale);
        String formatStr = numberFormat.getLocalizedNumber("121", "2");
        Assert.assertTrue(formatStr.length() > 0);
    }
}
