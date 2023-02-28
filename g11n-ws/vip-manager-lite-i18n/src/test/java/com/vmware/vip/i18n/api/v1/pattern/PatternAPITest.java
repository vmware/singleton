/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.pattern;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.vip.LiteBootApplication;
import com.vmware.vip.common.utils.JSONUtils;
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiteBootApplication.class)
public class PatternAPITest {

    public static final String[] PatternAPIURI = {"/i18n/api/v1/i18nPattern?locale=fr&scope=dates,numbers",
            "/i18n/api/v1/i18nPattern?locale=en-US&scope=dates,numbers"};

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void authentication() throws Exception {
        String authenticationResult = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.POST,
                ConstantsForTest.AuthenticationAPIURI);
        CacheUtil.cacheSessionAndToken(webApplicationContext, authenticationResult);
    }
    /**
     *
     * @Title: testPatternAPI
     * @Description: test apiUrl:/i18n/api/v1/i18nPattern
     * @param: @throws Exception
     * @return: void
     * @date 2019-01-03 14:25
     * @throws
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPatternAPI() throws Exception {
        String cateStr = "dates,numbers";
        for (String url : PatternAPIURI){
            String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, url);
            Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("data");
            Map<String, Object> catesMap = (Map<String, Object>) dataMap.get("categories");
            String[] catesArr = cateStr.split(",");
            for (String key : catesArr) {
                Assert.assertNotNull(catesMap.get(key));
            }
        }
    }
}
