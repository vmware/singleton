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
    public void testGetParamFromURI() {
        String noComponentErrorMsg = "URI doesn't contain required parameter 'component'!";
        String noComponentValueErrorMsg = "URI doesn't provide value for required parameter 'component'!";
        String[] testedURIs = {
                //test noComponentErrorMsg
                (String) null,
                "",
                "https://10.126.59.186/i18n",//test not contain 'component' parameter

                //test noComponentValueErrorMsg
                "https://10.126.59.186/i18n/component",//test only contain 'component' parameter
                "https://10.126.59.186/i18n/component?sdf=34",
                "https://10.126.59.186/i18n/componentss/",
                " locale = en & component=default",//test contain spaces

                "https://10.126.59.186/i18n/component/",
                "https://10.126.59.186/i18n/component/JS",
                "https://10.126.59.186/i18n/component/JS/",
                "https://10.126.59.186/i18n/ component / JS ",
                "https://10.126.59.186/i18n/component/JS /locale/en",
                "https://10.126.59.186/i18n/component/JS /locale/en?",
                "https://10.126.59.186/i18n/component/JS/locale/en?pseudo=false",
                "https://10.126.59.186/i18n/component/JS ?pseudo=false"};
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < testedURIs.length; i++) {
            try {
                list.add(FilterUtils.getParamFromURI(testedURIs[i], "component"));
            } catch (VIPJavaClientException e) {
                if (i < 3) {
                    Assert.assertEquals(noComponentErrorMsg, e.getMessage());
                } else if (i < 7) {
                    Assert.assertEquals(noComponentValueErrorMsg, e.getMessage());
                }
            }
        }
        for (int j = 0; j < list.size(); j++) {
            String component = list.get(j);
            System.out.println(component);
            if (j == 0) {
                Assert.assertEquals("", component);
            } else {
                Assert.assertEquals("JS", component);
            }
        }
    }

    @Test
    public void testGetParamFromQuery() {
        String noLocaleErrorMsg = "Request parameter 'locale' is required!";
        String noLocaleValueErrorMsg = "Value of request parameter 'locale' must not be empty!";
        String[] testedQueryStrs = {
                (String) null,
                "",
                "eeeeewrewe=321&rew",//test not contain locale
                "localepath=",
                "locale",//test only contain locale
                "locale=",
                "locale=en",
                "locale=en&",
                "locale=en&component=default",
                " locale = en & component=default"//test contain spaces
        };
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < testedQueryStrs.length; i++) {
            try {
                list.add(FilterUtils.getParamFromQuery(testedQueryStrs[i], "locale"));
            } catch (VIPJavaClientException e) {
                if (i < 4) {
                    Assert.assertEquals(noLocaleErrorMsg, e.getMessage());
                } else {
                    Assert.assertEquals(noLocaleValueErrorMsg, e.getMessage());
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            String locale = list.get(i);
            if (i == 0) {
                Assert.assertEquals("", locale);
            } else {
                Assert.assertEquals("en", locale);
            }
        }
    }
}
