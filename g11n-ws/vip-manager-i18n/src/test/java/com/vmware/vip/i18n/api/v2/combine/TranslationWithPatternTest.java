/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.combine;

import java.io.File;
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
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TranslationWithPatternTest {
   private static Logger logger = LoggerFactory.getLogger(TranslationWithPatternTest.class);

   private static String noRegionJsonReq = "{\n" + "  \"combine\": 2,\n" + "  \"components\": [\n"
         + "    \"default\"\n" + "  ],\n" + "  \"language\": \"en-US\",\n"
         + "  \"productName\": \"Testing\",\n" + "  \"pseudo\": \"false\",\n"
         + "  \"scope\": \"dates,numbers\",\n" + "  \"version\": \"2.0.0\"\n" + "}";
   private static String regionJsonReq =
         "{\n" + "  \"combine\": 1,\n" + "  \"components\": [\n" + "    \"default\"\n" + "  ],\n"
               + "  \"language\": \"en\",\n" + "  \"productName\": \"Testing\",\n"
               + "  \"region\":\"US\",\n" + "  \"pseudo\": \"false\",\n"
               + "  \"scope\": \"dates,numbers\",\n" + "  \"version\": \"2.0.0\"\n" + "}";

   private static String TranslationWithPatternAPIURI =
         "/i18n/api/v2/combination/translationsAndPattern";


   private static String msg = "{\n" + "    \"component\" : \"default\",\n"
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
            + "1.5.0" + File.separator + "default" +File.separator
            + "messages_en.json";

      FileUtils.write(new File(filepath), msg, "UTF-8", false);


   }

   @Test
   public void testTranslationWithPatternWithoutRegion() throws Exception {

      String responseStr = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.POST,
            TranslationWithPatternAPIURI, noRegionJsonReq);

      logger.info(responseStr);
      Assert.assertNotNull(responseStr);
      
   }

   @Test
   public void testTranslationWithPatternRegion() throws Exception {

      String responseStr = RequestUtil.sendRequest(webApplicationContext, ConstantsForTest.POST,
            TranslationWithPatternAPIURI, regionJsonReq);
      logger.info(responseStr);
      Assert.assertNotNull(responseStr);
   }

}
