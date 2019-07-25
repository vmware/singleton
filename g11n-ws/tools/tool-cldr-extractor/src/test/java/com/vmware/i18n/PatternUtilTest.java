/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n;

import com.vmware.i18n.utils.JSONUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Unit testing of PatternUtil
 */

public class PatternUtilTest {

    /**
     * Query defaultContent.json to determine whether there is a matching locale,
     * and if so, get the processed result and run
     */
    @Test
    public void getMatchingLocaleFromLib() {
        String locale = PatternUtil.getMatchingLocaleFromLib("en_US");
        Assert.assertEquals("en", locale);
    }
}
