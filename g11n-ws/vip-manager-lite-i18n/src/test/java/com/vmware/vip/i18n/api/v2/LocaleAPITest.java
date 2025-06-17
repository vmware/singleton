/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2;

import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;
import com.vmware.i18n.l2.service.locale.TerritoryDTO;
import com.vmware.vip.LiteBootApplication;
import com.vmware.vip.common.utils.JSONUtils;
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiteBootApplication.class)
public class LocaleAPITest {

	public static final String REGION_LIST_API_URI = "/i18n/api/v2/locale/regionList?supportedLanguageList=fr,ja";

	public static final String SUPPORTED_LANGUAGE_LIST_API_URI = "/i18n/api/v2/locale/supportedLanguageList?productName=VMCUI&version=1.0.0&displayLanguage=ja";

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void authentication() throws Exception {
		String authenticationResult = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.POST,
				ConstantsForTest.AuthenticationAPIURI);
		CacheUtil.cacheSessionAndToken(webApplicationContext, authenticationResult);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRegionListAPI() throws Exception {
		String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, REGION_LIST_API_URI);
		List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) JSONUtils.getMapFromJson(json).get("data");
		for (int i = 0; i < list.size(); i++) {
			Assert.assertNotNull(list.get(i).get("language"));
			Assert.assertNotNull(list.get(i).get("territories"));
		}
	}
	
	@Test
	public void test006CustomErrorController() throws Exception {
		String json = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET, "/error");
		Assert.assertNotNull(json);
	}

}
