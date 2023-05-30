/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.filters.FilterUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FilterUtilsTest {
    @Test
    public void testGetParamFromURI(){
        String noComponentErrorMsg= "URI doesn't contain required parameter 'component'!";
        String noComponentValueErrorMsg = "URI doesn't provide value for required parameter 'component'!";
        List<String> list =new ArrayList<String>();
        try {
            list.add(FilterUtils.getParamFromURI((String) null, "component"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noComponentErrorMsg, e.getMessage());
        }
        try {
            list.add(FilterUtils.getParamFromURI("", "component"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noComponentErrorMsg, e.getMessage());
        }
        try {
            //test not contain locale
            list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n", "component"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noComponentErrorMsg, e.getMessage());
        }
        try {
            //test only contain locale
            list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n/component", "component"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noComponentValueErrorMsg, e.getMessage());
        }
        try {
            list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n/component?sdf=34", "component"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noComponentValueErrorMsg, e.getMessage());
        }
        try {
            list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n/componentss/", "component"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noComponentValueErrorMsg, e.getMessage());
        }
        list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n/component/", "component"));
        list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS", "component"));
        list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS/", "component"));
        list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS /locale/en", "component"));
        list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS/locale/en?", "component"));
        list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS/locale/en?pseudo=false", "component"));
        list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS ?pseudo=false", "component"));
        try {
            //test contain spaces
            list.add(FilterUtils.getParamFromURI(" locale = en & component=default", "component"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noComponentValueErrorMsg, e.getMessage());
        }
        list.add(FilterUtils.getParamFromURI("https://10.126.59.186/i18n/ component / JS ", "component"));
        for(int i=0; i<list.size(); i++) {
            String component = list.get(i);
            if(i == 0) {
                Assert.assertEquals("", component);
            }else {
                Assert.assertEquals("JS", component);
            }
        }
    }

    @Test
    public void testGetParamFromQuery(){
        String noLocaleErrorMsg= "Request parameter 'locale' is required!";
        String noLocaleValueErrorMsg = "Value of request parameter 'locale' must not be empty!";
        List<String> list =new ArrayList<String>();
        try {
            list.add(FilterUtils.getParamFromQuery((String) null, "locale"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noLocaleErrorMsg, e.getMessage());
        }
        try {
            list.add(FilterUtils.getParamFromQuery("", "locale"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noLocaleErrorMsg, e.getMessage());
        }
        try {
            //test not contain locale
            list.add(FilterUtils.getParamFromQuery("eeeeewrewe=321&rew", "locale"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noLocaleErrorMsg, e.getMessage());
        }
        try {
            list.add(FilterUtils.getParamFromQuery("localepath=", "locale"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noLocaleErrorMsg, e.getMessage());
        }
        try {
            //test only contain locale
            list.add(FilterUtils.getParamFromQuery("locale", "locale"));
        } catch (VIPJavaClientException e) {
            Assert.assertEquals(noLocaleValueErrorMsg, e.getMessage());
        }
        list.add(FilterUtils.getParamFromQuery("locale=", "locale"));
        list.add(FilterUtils.getParamFromQuery("locale=en", "locale"));
        list.add(FilterUtils.getParamFromQuery("locale=en&", "locale"));
        list.add(FilterUtils.getParamFromQuery("locale=en&component=default", "locale"));
        //test contain spaces
        list.add(FilterUtils.getParamFromQuery(" locale = en & component=default", "locale"));
        for(int i=0; i<list.size(); i++) {
            String locale = list.get(i);
            if(i == 0) {
                Assert.assertEquals("", locale);
            }else {
                Assert.assertEquals("en", locale);
            }
        }
    }
}
