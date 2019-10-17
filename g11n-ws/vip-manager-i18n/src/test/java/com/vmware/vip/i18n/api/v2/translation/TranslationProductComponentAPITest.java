/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.translation;

import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.vip.BootApplication;
import com.vmware.vip.common.utils.JSONUtils;
import com.vmware.vip.i18n.api.v1.common.CacheUtil;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class TranslationProductComponentAPITest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    
    private static String SingleComponentTranslationAPIURI="/i18n/api/v2/translation/products/SampleProject/versions/1.0.0/locales/es/components/component1?checkTranslationStatus=false&machineTranslation=false&pseudo=false";
    private static String MultComponentTranslationAPIURI1="/i18n/api/v2/translation/products/SampleProject/versions/1.0.0/multlocales/en,es/multcomponents/component1,component2?pseudo=false";
    private static String MultComponentTranslationAPIURI2="/i18n/api/v2/translation/products/SampleProject/versions/1.0.0/multlocales/en/multcomponents/component1,component5?pseudo=false";
    private static String MultComponentTranslationAPIURI3="/i18n/api/v2/translation/products/SampleProject/versions/1.0.0/multlocales/en,zh-Hants/multcomponents/component1?pseudo=false";
    private static String MultComponentTranslationAPIURI4="/i18n/api/v2/translation/products/SampleProject/versions/1.0.0/multlocales/zh-Hants/multcomponents/component1,component2?pseudo=false";
    
    
    @Before
    public void setup() throws Exception {
        String authenticationResult=RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.POST, ConstantsForTest.AuthenticationAPIURI);
        CacheUtil.cacheSessionAndToken(webApplicationContext, authenticationResult);
    }

    /**
     * * @throws Exception
     *  get result when translation only can't include component5
     */
    @Test
    public void testMultiComponents2() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, MultComponentTranslationAPIURI2);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==404L);
    }
    
    /**
     * * @throws Exception
     *  get result when translation only don't have zh-Hants language 
     */
    @Test
    public void testMultiComponents3() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, MultComponentTranslationAPIURI3);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==404L);
    }
    
    /**
     * * @throws Exception
     *  get result when translation component1 and component2 don't have zh-Hants language 
     */
    @Test
    public void testMultiComponents4() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, MultComponentTranslationAPIURI4);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==404L);
    }
    

    @Test
    public void testSingleComponent() throws Exception {
      String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, SingleComponentTranslationAPIURI);
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
    public void testMultiComponents1() throws Exception {
        String json = RequestUtil.sendRequest(webApplicationContext,ConstantsForTest.GET, MultComponentTranslationAPIURI1);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) JSONUtils.getMapFromJson(json).get("response");
        long code = (long) dataMap.get("code");
        Assert.assertTrue(code==200L);
    }
}
