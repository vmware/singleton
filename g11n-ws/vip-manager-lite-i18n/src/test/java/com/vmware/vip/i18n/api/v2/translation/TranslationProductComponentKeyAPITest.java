/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.translation;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.vip.LiteBootApplication;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.utils.JSONUtils;
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiteBootApplication.class)
public class TranslationProductComponentKeyAPITest {
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	private String test_en="{\r\n" + 
            "    \"component\" : \"test\",\r\n" + 
            "    \"messages\": {\r\n" + 
            "        \"testMD1\" : \"apple\",\r\n" + 
            "        \"testhtml0\" : \"banana\",\r\n" + 
            "        \"sample.cat\" : \"cat\",\r\n" + 
            "        \"sample.dog\" : \"dog\",\r\n" + 
            "        \"sample.egg\" : \"egg\",\r\n" + 
            "        \"sample.fly\" : \"fly\",\r\n" + 
            "        \"sample.giant\" : \"giant\"\r\n" + 
            "    },\r\n" + 
            "    \"locale\" : \"en\"\r\n" + 
            "}";
	
	 private static String multKeyGetUrl1 = "/i18n/api/v2/translation/products/SingletonSample/versions/1.0.0/locales/en/components/test/keys?keys=testMD1,testhtml0&pseudo=false";
	 private static String multKeyGetUrl2 = "/i18n/api/v2/translation/products/SingletonSample/versions/1.0.0/locales/en/components/test/keys?keys=testMD1,testhtml1&pseudo=false";
	 private static String multKeyGetUrl3 = "/i18n/api/v2/translation/products/SingletonSample/versions/1.0.0/locales/en/components/test/keys?keys=testMD2,testhtml2&pseudo=false";
	 private static String multKeyGetUrl4 = "/i18n/api/v2/translation/products/SingletonSample/versions/1.0.0/locales/en/components/test/keys?keys=testhtml0&pseudo=false";
     
	 private static String keyGetUrl1 = "/i18n/api/v2/translation/products/SingletonSample/versions/1.0.0/locales/en/components/test/keys/testMD1?collectSource=false&pseudo=false";
	 private static String keyGetUrl2 = "/i18n/api/v2/translation/products/SingletonSample/versions/1.0.0/locales/en/components/test/keys/testMD2?collectSource=false&pseudo=false";
	
	 
	 @Before
	 public void setup() throws Exception {
		 String authenticationResult=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.POST, ConstantsForTest.AuthenticationAPIURI);
	     CacheUtil.cacheSessionAndToken(webApplicationContext, authenticationResult);
	        
	     String comp1_enPath = "."+File.separator + ConstantsFile.L10N_BUNDLES_PATH
	              +"SingletonSample"+  File.separator  + "1.0.0" + File.separator + "test" +File.separator
	                + "messages_en.json";
	     FileUtils.write(new File(comp1_enPath), test_en, "UTF-8", false);
	 }
	 
	 @Test
	 public void testGetKeysTranslation1() throws Exception {
		 String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, multKeyGetUrl1);
	        @SuppressWarnings("unchecked")
	        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
	        int code = (int) dataMap.get("code");
	        Assert.assertTrue(code==200);
	 }

	 
	 @Test
	 public void testGetKeysTranslation2() throws Exception {
		 String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, multKeyGetUrl2);
	        @SuppressWarnings("unchecked")
	        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
	        int code = (int) dataMap.get("code");
	        Assert.assertTrue(code==207);
	 }
	 
	 @Test
	 public void testGetKeysTranslation3() throws Exception {
		 String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, multKeyGetUrl3);
	        @SuppressWarnings("unchecked")
	        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
	        int code = (int) dataMap.get("code");
	        Assert.assertTrue(code==404);
	 }
	 
	 
	 @Test
	 public void testGetKeysTranslation4() throws Exception {
		 String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, multKeyGetUrl4);
	        @SuppressWarnings("unchecked")
	        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
	        int code = (int) dataMap.get("code");
	        Assert.assertTrue(code==200);
	 }
	 
	 @Test
	 public void testGetTranslationByGetTest1() throws Exception {
		 String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, keyGetUrl1);
	        @SuppressWarnings("unchecked")
	        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
	        int code = (int) dataMap.get("code");
	        Assert.assertTrue(code==200);
	 }
	 
	 @Test
	 public void testGetTranslationByGetTest2() throws Exception {
		 String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, keyGetUrl2);
	        @SuppressWarnings("unchecked")
	        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
	        int code = (int) dataMap.get("code");
	        Assert.assertTrue(code==200);
	 }
}
