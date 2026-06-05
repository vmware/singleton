/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.combine;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;
import com.vmware.vip.BootApplication;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.utils.JSONUtils;
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TranslationWithPatternTest {
   private static Logger logger = LoggerFactory.getLogger(TranslationWithPatternTest.class);

   private static String noRegionJsonReq = "{\n" + "  \"combine\": 2,\n" + "  \"components\": [\n"
         + "    \"testing\"\n" + "  ],\n" + "  \"language\": \"en-US\",\n"
         + "  \"productName\": \"Testing\",\n" + "  \"pseudo\": \"false\",\n"
         + "  \"scope\": \"dates,numbers\",\n" + "  \"version\": \"2.0.0\"\n" + "}";
   private static String regionJsonReq =
         "{\n" + "  \"combine\": 1,\n" + "  \"components\": [\n" + "    \"testing\"\n" + "  ],\n"
               + "  \"language\": \"en\",\n" + "  \"productName\": \"Testing\",\n"
               + "  \"region\":\"US\",\n" + "  \"pseudo\": \"false\",\n"
               + "  \"scope\": \"dates,numbers\",\n" + "  \"version\": \"2.0.0\"\n" + "}";
   
   private static String regionJsonReqVersionFallback =
	         "{\n" + "  \"combine\": 1,\n" + "  \"components\": [\n" + "    \"testing\"\n" + "  ],\n"
	               + "  \"language\": \"en\",\n" + "  \"productName\": \"Testing\",\n"
	               + "  \"region\":\"US\",\n" + "  \"pseudo\": \"false\",\n"
	               + "  \"scope\": \"dates,numbers\",\n" + "  \"version\": \"3.0.0\"\n" + "}";
	   
   

   private static String TranslationWithPatternAPIURI =
         "/i18n/api/v2/combination/translationsAndPattern";

   private static String noRegionParaReq= "/i18n/api/v2/combination/translationsAndPattern?combine=2&components=testing&language=en-US&productName=Testing&pseudo=false&scope=dates,numbers&version=2.0.0";
   
   private static String regionParaReq = "/i18n/api/v2/combination/translationsAndPattern?combine=1&components=testing&language=en&productName=Testing&pseudo=false&region=US&scope=dates,numbers&version=2.0.0";
   
   private static String versionFallfackRegionParaReq = "/i18n/api/v2/combination/translationsAndPattern?combine=1&components=testing&language=en&productName=Testing&pseudo=false&region=US&scope=dates,numbers&version=3.0.0";
   private static String msg = "{\n" + "    \"component\" : \"testing\",\n"
         + "    \"messages\": {\n" + "        \"sample.apple\" : \"apple\",\n"
         + "        \"sample.banana\" : \"banana\",\n" + "        \"sample.cat\" : \"cat\",\n"
         + "        \"sample.dog\" : \"dog\",\n" + "        \"sample.egg\" : \"egg\",\n"
         + "        \"sample.fly\" : \"fly\",\n" + "        \"sample.giant\" : \"giant\"\n"
         + "    },\n" + "    \"locale\" : \"en\"\n" + "}";

   @Autowired
   private WebApplicationContext webApplicationContext;

   @Before
   public void authentication() throws Exception {
      String authenticationResult = RequestUtil.sendRequest(webApplicationContext,
            ConstantsForTest.POST, ConstantsForTest.AuthenticationAPIURI);
      CacheUtil.cacheSessionAndToken(webApplicationContext, authenticationResult);
    
      String filepath = "."+File.separator + ConstantsFile.L10N_BUNDLES_PATH
    		  +"Testing"+ File.separator+ "2.0.0" + File.separator + "testing" +File.separator
            + "messages_en.json";

      FileUtils.write(new File(filepath), msg, "UTF-8", false);


   }

   @Test
   public void testTranslationWithPatternWithoutRegion() throws Exception {

      String responseStr = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.POST,
            TranslationWithPatternAPIURI, noRegionJsonReq);

      logger.info(responseStr);
      @SuppressWarnings("unchecked")
      Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(responseStr).get("response");
      int code = (int) dataMap.get("code");
      Assert.assertTrue(code==200);
      
   }

   @Test
   public void testTranslationWithPatternRegion() throws Exception {

      String responseStr = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.POST,
            TranslationWithPatternAPIURI, regionJsonReq);
      logger.info(responseStr);
      @SuppressWarnings("unchecked")
      Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(responseStr).get("response");
      int code = (int) dataMap.get("code");
      Assert.assertTrue(code==200);
   }
   
   
  
   
   @Test
   public void testTranslationWithPatternWithoutRegionGet() throws Exception {

      String responseStr = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET,
    		  noRegionParaReq);

      logger.info(responseStr);
      @SuppressWarnings("unchecked")
      Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(responseStr).get("response");
      int code = (int) dataMap.get("code");
      Assert.assertTrue(code==200);
      
   }

   @Test
   public void testTranslationWithPatternRegionGet() throws Exception {

      String responseStr = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET,
    		  regionParaReq);
      logger.info(responseStr);
      @SuppressWarnings("unchecked")
      Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(responseStr).get("response");
      int code = (int) dataMap.get("code");
      Assert.assertTrue(code==200);
   }
   
   @Test
   public void testTranslationWithPatternRegionVersionFallback() throws Exception {

      String responseStr = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.POST,
            TranslationWithPatternAPIURI, regionJsonReqVersionFallback);
      logger.info(responseStr);
      @SuppressWarnings("unchecked")
      Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(responseStr).get("response");
      int code = (int) dataMap.get("code");
      Assert.assertTrue(code==604);
   }
   
   @Test
   public void testTranslationWithPatternRegionGetVersionFallback() throws Exception {

      String responseStr = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.GET,
    		  versionFallfackRegionParaReq);
      logger.info(responseStr);
      @SuppressWarnings("unchecked")
      Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(responseStr).get("response");
      int code = (int) dataMap.get("code");
      Assert.assertTrue(code==604);
   }

  
}
