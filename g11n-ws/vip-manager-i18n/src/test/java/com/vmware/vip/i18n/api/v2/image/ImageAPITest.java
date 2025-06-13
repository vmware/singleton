/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vip.i18n.api.v2.image;


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
public class ImageAPITest {


    private static String countryFlagDefaultSucc = "/i18n/api/v2/image/countryFlag?region=US";
    private static String countryFlagScaleSucc = "/i18n/api/v2/image/countryFlag?region=US&scale=2";
    private static String countryFlagRegionErr = "/i18n/api/v2/image/countryFlag?region=USS";
    private static String countryFlagScaleErr = "/i18n/api/v2/image/countryFlag?region=US&scale=5";

    private static String countryFlagImageTypeSucc = "/i18n/api/v2/image/countryFlag?region=US&type=svg";

    private static String countryFlagIMageTypeErr = "/i18n/api/v2/image/countryFlag?region=US&type=svgg";


    @Autowired
    private WebApplicationContext webApplicationContext;


    @Before
    public void setup() throws Exception {
        String authenticationResult=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.POST, ConstantsForTest.AuthenticationAPIURI);
        CacheUtil.cacheSessionAndToken(webApplicationContext, authenticationResult);
    }

    @Test
    public void testCountryFlag1() throws Exception {

            String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, countryFlagDefaultSucc);
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
            int code = (int) dataMap.get("code");
            Assert.assertTrue(code==200);

    }

    @Test
    public void testCountryFlag2() throws Exception {

        String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, countryFlagScaleSucc);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        int code = (int) dataMap.get("code");
        Assert.assertTrue(code==200);

    }

    @Test
    public void testCountryFlag3() throws Exception {

        String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, countryFlagRegionErr);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        int code = (int) dataMap.get("code");
        Assert.assertTrue(code==400);

    }

    @Test
    public void testCountryFlag4() throws Exception {

        String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET,countryFlagScaleErr);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        int code = (int) dataMap.get("code");
        Assert.assertTrue(code==400);

    }

    @Test
    public void testCountryFlag5() throws Exception {

        String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET,countryFlagImageTypeSucc);

        Assert.assertTrue(json != null);

    }



    @Test
    public void testCountryFlag6() throws Exception {

        String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET,countryFlagIMageTypeErr);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json);
        int code = (int) dataMap.get("code");
        Assert.assertTrue(code==400);

    }




}
