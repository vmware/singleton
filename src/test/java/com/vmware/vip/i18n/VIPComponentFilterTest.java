/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.vmware.vipclient.i18n.exceptions.VIPClientInitException;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import com.vmware.vipclient.i18n.filters.URLParamUtils;

public class VIPComponentFilterTest {
    private TranslationMessage translation;
    private VIPCfg gc = VIPCfg.getInstance();

    @Before
    public void init(){
        if (gc.getVipService() == null) {
            try {
                gc.initialize("vipconfig");
            } catch (VIPClientInitException e) {
                //logger.error(e.getMessage());
            }
            gc.initializeVIPService();
        }
        gc.createTranslationCache(MessageCache.class);
        I18nFactory i18n = I18nFactory.getInstance(gc);
        translation = (TranslationMessage) i18n.getMessageInstance(TranslationMessage.class);
    }

    @Test
    public void testDoFilter() {
        String uri = "https://localhost/i18n/component/JS?locale=zh-CN";
        String queryStr = "locale=zh-CN";
        String locale = null;
        String component = null;
        try {
            component = URLParamUtils.getParamFromURI(uri, "component");
            System.out.println("component: " + component);
            locale = URLParamUtils.getParamFromQuery(queryStr, "locale");
            System.out.println("locale: " + locale);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String messages = "{}";
        if ((component != null && !"".equalsIgnoreCase(component)) && (locale != null && !"".equalsIgnoreCase(locale))) {
            if (!LocaleUtility.isDefaultLocale(locale)) {
                Map<String, String> ctmap = translation.getMessages(LocaleUtility.fmtToMappedLocale(locale), component);
                if (ctmap != null) {
                    System.out.println("size: " + ctmap.size());
                    messages = JSONObject.toJSONString(ctmap);
                }
            }
        }
        System.out.println("translations: " + messages);
    }
}
