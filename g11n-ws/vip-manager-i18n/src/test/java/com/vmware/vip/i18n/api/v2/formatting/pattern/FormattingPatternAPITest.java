/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.formatting.pattern;

import com.vmware.vip.BootApplication;
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
@SpringBootTest(classes = BootApplication.class)
public class FormattingPatternAPITest {
    public static final String LocalePatternAPIURI = "/i18n/api/v2/formatting/patterns/locales/{locale}";
    public static final String LanguageRegionPatternAPIURI = "/i18n/api/v2/formatting/patterns";

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
        String locale = "en-US";
        String cateStr = "dates,numbers";

        //Test with valid 'scope' parameter
        String url = new StringBuilder(
                LocalePatternAPIURI.replace("{locale}", locale))
                .append("?scope=").append(cateStr)
                .toString();
        String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, url);
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("data");
        Map<String, Object> catesMap = (Map<String, Object>) dataMap.get("categories");
        String[] catesArr = cateStr.split(",");
        for (String key : catesArr) {
            Assert.assertNotNull(catesMap.get(key));
        }

        //Test with invalid 'scope' parameter
        url = new StringBuilder(
                LocalePatternAPIURI.replace("{locale}", locale))
                .append("?scope=").append("datess,numbers")
                .toString();
        json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, url);
        Map<String, Object> respMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        Assert.assertEquals(400L, respMap.get("code"));

        //Test with valid 'scope' and 'scopeFilter' parameter
        url = new StringBuilder(
                LocalePatternAPIURI.replace("{locale}", locale))
                .append("?scope=").append(cateStr)
                .append("&scopeFilter=").append("dates_dateFormats_short")
                .toString();
        json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, url);
        respMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        Assert.assertEquals(200L, respMap.get("code"));


    }

    @Test
    public void testGetI18nPatternInvalidateScopeFilter() throws Exception {
        String locale = "en";
        String cateStr = "dates,numbers";

        String url = new StringBuilder(LocalePatternAPIURI.replace("{locale}", locale))
                .append("?scope=").append(cateStr)
                .append("&scopeFilter=").append("dates_a,c_d")
                .toString();
        String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, url);
        Map<String,Object> respMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        Assert.assertEquals(400L, respMap.get("code"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetI18nPatternWithLanguageAndRegion() throws Exception {
        String language = "fr";
        String region = "FR";
        String cateStr = "dates,numbers";

        //Test with valid 'scope' parameter
        String url = new StringBuilder(LanguageRegionPatternAPIURI)
                .append("?language=").append(language)
                .append("&region=").append(region)
                .append("&scope=").append(cateStr)
                .toString();
        String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, url);
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("data");
        Map<String, Object> catesMap = (Map<String, Object>) dataMap.get("categories");
        String[] catesArr = cateStr.split(",");
        for (String key : catesArr) {
            Assert.assertNotNull(catesMap.get(key));
        }

        //Test with valid 'scope' and 'scopeFilter' parameter
        url = new StringBuilder(LanguageRegionPatternAPIURI)
                .append("?language=").append(language)
                .append("&region=").append(region)
                .append("&scope=").append(cateStr)
                .append("&scopeFilter=").append("dates_dateFormats_short")
                .toString();
        json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, url);
        Map<String, Object> respMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        Assert.assertEquals(200L, respMap.get("code"));
    }

}
