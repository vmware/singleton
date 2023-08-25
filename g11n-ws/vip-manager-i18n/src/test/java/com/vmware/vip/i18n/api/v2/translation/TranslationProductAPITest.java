/*
 * Copyright 2019-2023 VMware, Inc.
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

import com.vmware.vip.BootApplication;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.utils.JSONUtils;
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TranslationProductAPITest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    
    private static String comp1_en="{\r\n" + 
            "    \"component\" : \"component1\",\r\n" + 
            "    \"messages\": {\r\n" + 
            "        \"sample.apple\" : \"apple\",\r\n" + 
            "        \"sample.banana\" : \"banana\",\r\n" + 
            "        \"sample.cat\" : \"cat\",\r\n" + 
            "        \"sample.dog\" : \"dog\",\r\n" + 
            "        \"sample.egg\" : \"egg\",\r\n" + 
            "        \"sample.fly\" : \"fly\",\r\n" + 
            "        \"sample.giant\" : \"giant\"\r\n" + 
            "    },\r\n" + 
            "    \"locale\" : \"en\"\r\n" + 
            "}";
    private static String comp1_es="{\r\n" + 
            "  \"component\" : \"component1\",\r\n" + 
            "  \"messages\" : {\r\n" + 
            "    \"sample.apple\" : \"manzana\",\r\n" + 
            "    \"sample.banana\" : \"plátano\",\r\n" + 
            "    \"sample.cat\" : \"gato\",\r\n" + 
            "    \"sample.dog\" : \"perro\",\r\n" + 
            "    \"sample.egg\" : \"huevo\",\r\n" + 
            "    \"sample.fly\" : \"volar\",\r\n" + 
            "    \"sample.giant\" : \"gigante\"\r\n" + 
            "  },\r\n" + 
            "  \"locale\" : \"es\"\r\n" + 
            "}";
    private static String comp2_en="{\r\n" + 
            "    \"component\" : \"component2\",\r\n" + 
            "    \"messages\": {\r\n" + 
            "        \"sample.one\" : \"one\",\r\n" + 
            "        \"sample.two\" : \"two\",\r\n" + 
            "        \"sample.three\" : \"three\",\r\n" + 
            "        \"sample.four\" : \"four\",\r\n" + 
            "        \"sample.five\" : \"five\"\r\n" + 
            "    },\r\n" + 
            "    \"locale\" : \"en\"\r\n" + 
            "}";
    private static String comp2_es="{\r\n" + 
            "  \"component\" : \"component2\",\r\n" + 
            "  \"messages\" : {\r\n" + 
            "    \"sample.one\" : \"uno\",\r\n" + 
            "    \"sample.two\" : \"dos\",\r\n" + 
            "    \"sample.three\" : \"tres\",\r\n" + 
            "    \"sample.four\" : \"cuatro\",\r\n" + 
            "    \"sample.five\" : \"cinco\"\r\n" + 
            "  },\r\n" + 
            "  \"locale\" : \"es\"\r\n" + 
            "}";
   
    
    
    private static String singleComponentTranslationAPIURI="/i18n/api/v2/translation/products/MULTCOMP/versions/1.0.0/locales/es/components/component1?checkTranslationStatus=false&machineTranslation=false&pseudo=false";
    private static String multComponentTranslationAPIURI1="/i18n/api/v2/translation/products/MULTCOMP/versions/1.0.0?pseudo=false";
    private static String multComponentTranslationAPIURI2="/i18n/api/v2/translation/products/MULTCOMP/versions/1.0.0?locales=en,es&&components=component1,component2&&pseudo=false";
    private static String multComponentTranslationAPIURI3="/i18n/api/v2/translation/products/MULTCOMP/versions/1.0.0?locales=en,zh-Hants&&components=component1,component5&&pseudo=false";
    private static String multComponentTranslationAPIURI4="/i18n/api/v2/translation/products/MULTCOMP/versions/1.0.0?locales=zh-Hants&&components=component1,component5&&pseudo=false";  
    private static String multComponentTranslationAPIURI5="/i18n/api/v2/translation/products/MULTCOMP/versions/1.0.0?components=component1,component2&&pseudo=false";
    private static String multComponentTranslationAPIURI6="/i18n/api/v2/translation/products/MULTCOMP/versions/1.0.0?locales=en&&pseudo=false";
    private static String multComponentTranslationAPIURI7="/i18n/api/v2/translation/products/MULTCOMP/versions/1.0.0?components=component1,component7&&pseudo=false";
    private static String versionFallbackMultComponentTranslationAPIURI1="/i18n/api/v2/translation/products/MULTCOMP/versions/2.0.0?pseudo=false";
    private static String versionFallbackSingleComponentTranslationAPIURI="/i18n/api/v2/translation/products/MULTCOMP/versions/2.0.0/locales/es/components/component1?checkTranslationStatus=false&machineTranslation=false&pseudo=false";
    private static String versionFallbackSupportLanguageList = "/i18n/api/v2/locale/supportedLanguageList?displayLanguage=es&productName=MULTCOMP&version=2.0.0";
    private static String versionFallbackComponentlist ="/i18n/api/v2/translation/products/MULTCOMP/versions/2.0.0/componentlist";
    private static String versionFallbackLocalelist="/i18n/api/v2/translation/products/MULTCOMP/versions/2.0.0/localelist";
    private static String versionList="/i18n/api/v2/translation/products/MULTCOMP/versionlist";
    private static String versionListNoProduct="/i18n/api/v2/translation/products/NULLMULTCOMP/versionlist";
    private static String versionListNull="/i18n/api/v2/translation/products/MULTCOMPTEST/versionlist";


    @Before
    public void setup() throws Exception {
        String authenticationResult=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.POST, ConstantsForTest.AuthenticationAPIURI);
        CacheUtil.cacheSessionAndToken(webApplicationContext, authenticationResult);
        String comp1_enPath = "."+File.separator + ConstantsFile.L10N_BUNDLES_PATH
              +"MULTCOMP"+  File.separator  + "1.0.0" + File.separator + "component1" +File.separator
                + "messages_en.json";
        FileUtils.write(new File(comp1_enPath), comp1_en, "UTF-8", false);
        String comp1_esPath = "."+File.separator + ConstantsFile.L10N_BUNDLES_PATH
                +"MULTCOMP"+  File.separator  + "1.0.0" + File.separator + "component1" +File.separator
                  + "messages_es.json";
        FileUtils.write(new File(comp1_esPath), comp1_es, "UTF-8", false);
        String comp2_enPath = "."+File.separator + ConstantsFile.L10N_BUNDLES_PATH
                +"MULTCOMP"+  File.separator  + "1.0.0" + File.separator + "component2" +File.separator
                  + "messages_en.json";
        FileUtils.write(new File(comp2_enPath), comp2_en, "UTF-8", false);
        String comp2_esPath = "."+File.separator + ConstantsFile.L10N_BUNDLES_PATH
                +"MULTCOMP"+  File.separator  + "1.0.0" + File.separator + "component2" +File.separator
                  + "messages_es.json";
        FileUtils.write(new File(comp2_esPath), comp2_es, "UTF-8", false);

        String product_esPath = "."+File.separator + ConstantsFile.L10N_BUNDLES_PATH
                +"MULTCOMPTEST"+  File.separator;
        File productTestFile = new File(product_esPath);
        productTestFile.deleteOnExit();
        productTestFile.mkdirs();


    }

    /**
     * * @throws Exception 
     * get the mult-local and mult-component case
     */
    @Test
    public void testMultiComponents1() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, multComponentTranslationAPIURI1);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==200L);
    }
    

    
    
    
    
    /**
     * * @throws Exception
     *  get result when translation only can't include component5
     */
    @Test
    public void testMultiComponents2() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, multComponentTranslationAPIURI2);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==200L);
    }
    
    /**
     * * @throws Exception
     *  get result when translation only don't have zh-Hants language 
     */
    @Test
    public void testMultiComponents3() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, multComponentTranslationAPIURI3);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==207L);
    }
    
    /**
     * * @throws Exception
     *  get result when translation component1 and component2 don't have zh-Hants language 
     */
    @Test
    public void testMultiComponents4() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, multComponentTranslationAPIURI4);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==404L);
    }
    
    @Test
    public void testMultiComponents5() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, multComponentTranslationAPIURI5);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==200L);
    }
    
    @Test
    public void testMultiComponents6() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, multComponentTranslationAPIURI6);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==200L);
    }
    @Test
    public void testMultiComponents7() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, multComponentTranslationAPIURI7);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==207L);
    }

    @Test
    public void testSingleComponent() throws Exception {
      String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, singleComponentTranslationAPIURI);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==200L);
       
    }

    /**
     * * @throws Exception 
     * get the mult-local and mult-component case
     */
    @Test
    public void testVersionFallbackMultiComponents1() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, versionFallbackMultComponentTranslationAPIURI1);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==604L);
    }
    
    @Test
    public void testVersionFallbackSingleComponent() throws Exception {
      String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, versionFallbackSingleComponentTranslationAPIURI);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==604L);
       
    }
    
    @Test
    public void testVersionSupportLanguageList() throws Exception {
      String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, versionFallbackSupportLanguageList);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==604L);
       
    }
    
    @Test
    public void testVersionComponentList() throws Exception {
      String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, versionFallbackComponentlist);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==604L);
       
    }
    
    @Test
    public void testVersionLocaleList() throws Exception {
      String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, versionFallbackLocalelist);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==604L);
       
    }

    @Test
    public void testVersionList() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, versionList);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==200L);

    }
    @Test
    public void testVersionListNoProduct() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, versionListNoProduct);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");

        Assert.assertTrue(code==404L);

    }
    @Test
    public void testVersionListIsNull() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, versionListNull);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");

        Assert.assertTrue(code==200L);

    }

 
}
