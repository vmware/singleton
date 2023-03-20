/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.formatting.pattern;

import com.vmware.vip.LiteBootApplication;
import com.vmware.vip.common.utils.JSONUtils;
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiteBootApplication.class)
public class FormattingPatternAPITest {
    public static final String PatternAPIURI = "/i18n/api/v2/formatting/patterns/locales/en-US?scope=dates,numbers";
    public static final String PatternAPIURL = "/i18n/api/v2/formatting/patterns?language=fr&region=FR&scope=dates,numbers";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void authentication() throws Exception {
        String authenticationResult = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.POST,
                ConstantsForTest.AuthenticationAPIURI);
        CacheUtil.cacheSessionAndToken(webApplicationContext, authenticationResult);
    }

    @Test
    public void testGetI18nPattern() throws Exception {
        String cateStr = "dates,numbers";
        String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, PatternAPIURI);
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("data");
        Map<String, Object> catesMap = (Map<String, Object>) dataMap.get("categories");
        String[] catesArr = cateStr.split(",");
        for (String key : catesArr) {
            Assert.assertNotNull(catesMap.get(key));
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetI18nPatternWithLanguageAndRegion() throws Exception {
        String cateStr = "dates,numbers";
        String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, PatternAPIURL);
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("data");
        Map<String, Object> catesMap = (Map<String, Object>) dataMap.get("categories");
        String[] catesArr = cateStr.split(",");
        for (String key : catesArr) {
            Assert.assertNotNull(catesMap.get(key));
        }
    }

}
