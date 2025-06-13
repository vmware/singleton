/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.vip.LiteBootApplication;
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;
import com.vmware.vip.i18n.api.v1.common.ResponseUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiteBootApplication.class)
public class TranslationSourceAPITest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void authentication() throws Exception{
        String authenticationResult=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.POST, ConstantsForTest.AuthenticationAPIURI);
        CacheUtil.cacheSessionAndToken(webApplicationContext, authenticationResult);
    }

    @Test
    public void testCreateSource() throws Exception{
        JSONObject requestBody=new JSONObject();
        requestBody.put("source", "For more information about EVC modes and EVC modes supported in an ESX release, please refer to VMware KB 1003212");
        String responseStr=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.POST,
                ConstantsForTest.CreateSourceAPIURI,requestBody.toString());
        int businessCode=ResponseUtil.getResponseCode(responseStr);
        Assert.assertTrue(businessCode==200);
    }

    @Test
    public void testGetTranslationBySource() throws Exception{
        String responseStr=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET,
                ConstantsForTest.GetTranslationBySourceAPIURI);
        String translation=(String) ResponseUtil.getNodeDataFromResponse(responseStr,"translation");
        //Assert.assertTrue("产品发行版本".equalsIgnoreCase(translation));
    }
}
