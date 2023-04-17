/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import com.vmware.vipclient.i18n.filters.URLParamUtils;

public class URLParamUtilsTest {
    @Test
    public void testGetParamFromURI(){
        List<String> list =new ArrayList<String>();
//        list.add(URLParamUtils.getParamFromURI(null, "component"));
//        list.add(URLParamUtils.getParamFromURI("", "component"));
        //test not contain locale
//        list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n", "component"));
        //test only contain locale
//         list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n/component", "component"));
        list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n/component/", "component"));
        //list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n/component?sdf=34", "component"));
//         list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n/componentss/", "component"));
        list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS", "component"));
        list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS/", "component"));
        list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS /locale/en", "component"));
        list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS/locale/en?", "component"));
        list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS/locale/en?pseudo=false", "component"));
        list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n/component/JS ?pseudo=false", "component"));
        //test contain spaces
        //list.add(URLParamUtils.getParamFromURI(" locale = en & component=default", "component"));
        list.add(URLParamUtils.getParamFromURI("https://10.126.59.186/i18n/ component / JS ", "component"));
        for(String component : list) {
            System.out.println("component: " + component);
        }

    }

    @Test
    public void testGetParamFromQuery(){
        List<String> list =new ArrayList<String>();
        //list.add(URLParamUtils.getParamFromQuery(null, "locale"));
        //list.add(URLParamUtils.getParamFromQuery("", "locale"));
        //test not contain locale
        //list.add(URLParamUtils.getParamFromQuery("eeeeewrewe=321&rew", "locale"));
        //test only contain locale
        // list.add(URLParamUtils.getParamFromQuery("locale", "locale"));
        // list.add(URLParamUtils.getParamFromQuery("locale=", "locale"));
        // list.add(URLParamUtils.getParamFromQuery("localepath=", "locale"));
        list.add(URLParamUtils.getParamFromQuery("locale=en", "locale"));
        list.add(URLParamUtils.getParamFromQuery("locale=en&", "locale"));
        list.add(URLParamUtils.getParamFromQuery("locale=en&component=default", "locale"));
        //test contain spaces
        list.add(URLParamUtils.getParamFromQuery(" locale = en & component=default", "locale"));
        for(String locale : list) {
            System.out.println("locale: " + locale);
        }
    }
}
