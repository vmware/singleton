/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.filters.URLParamUtils;
import com.vmware.vipclient.i18n.messages.service.PatternService;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONObject;
import org.junit.Test;

import java.util.Map;

public class VIPPatternFilterTest {
    @Test
    public void testDoFilter() {
        //test valid locale
        String queryStr = "locale=zh-CN";
        doFilter(queryStr);

        //test empty locale
        queryStr = "";
        doFilter(queryStr);

        //test invalid locale
        queryStr = "locale=NSFTW";
        doFilter(queryStr);
    }

    private void doFilter(String queryStr){
        String locale = null;
        try {
            locale = URLParamUtils.getParamFromQuery(queryStr, "locale");
            System.out.println("locale: " + locale);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String messages = "{}";
        if ((locale != null && !"".equalsIgnoreCase(locale))) {
            if (!LocaleUtility.isDefaultLocale(locale)) {
                Map<String, String> ctmap = new PatternService().getPatterns(locale);
                if (ctmap != null) {
                    System.out.println("size: " + ctmap.size());
                    messages = JSONObject.toJSONString(ctmap);
                }
            }
        }
        System.out.println("patterns: " + messages);
    }

}