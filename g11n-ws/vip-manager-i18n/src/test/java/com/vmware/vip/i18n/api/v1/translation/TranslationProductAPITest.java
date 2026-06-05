/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;

import java.util.List;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.vip.BootApplication;
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;
import com.vmware.vip.i18n.api.v1.common.ResponseUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TranslationProductAPITest {

	@Autowired
	private WebApplicationContext webApplicationContext;



	@Before
	public void setup() throws Exception {
	    String authenticationResult=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.POST, ConstantsForTest.AuthenticationAPIURI);
        CacheUtil.cacheSessionAndToken(webApplicationContext, authenticationResult);
	}

	@Test
	public void testGetComponentNameList() throws Exception {
	    String responseStr=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET,
				ConstantsForTest.ComponentNameListAPIURI);
        List<String> componentList=(List<String>) ResponseUtil.getNodeDataFromResponse(responseStr,"components");
        //Assert.assertTrue(componentList.size()>0);
	}

	@Test
    public void testGetSupportedLocales() throws Exception {
	    String responseStr=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET,
                ConstantsForTest.SupportedLocalesAPIURI);
        List<String> localeList=(List<String>) ResponseUtil.getNodeDataFromResponse(responseStr,"locales");
        //Assert.assertTrue(localeList.size()>0);
    }

	@Test
    public void testGetProductTranslation() throws Exception {
	    String responseStr=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET,
                ConstantsForTest.ProductTranslationAPIURI);
//        JSONArray bundlesArray=(JSONArray) ResponseUtil.getNodeDataFromResponse(responseStr,"bundles");
//        Assert.assertTrue(bundlesArray.size()>0);
    }

}
