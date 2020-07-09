/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.HttpRequester;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RemotePatternOpt {
    Logger logger = LoggerFactory.getLogger(RemotePatternOpt.class);

    public JSONObject getPatternsByLocale(String locale) {
        String responseStr = "";
        String i18nScope = VIPCfg.getInstance().getI18nScope();
        HttpRequester httpRequester = VIPCfg.getInstance().getVipService().getHttpRequester();
        if (i18nScope != null && !"".equalsIgnoreCase(i18nScope)) {
        	Map<String, Object> response = httpRequester.request(V2URL.getPatternURL(locale,
                    httpRequester.getBaseURL()), ConstantsKeys.GET, null);
        	responseStr = (String) response.get(URLUtils.BODY);
        }
        if (null == responseStr || responseStr.equals("")) {
            return null;
        } else {
            Object dataObj = this.getCategoriesFromResponse(responseStr, PatternKeys.CATEGORIES);
            JSONObject msgObject = null;
            if (dataObj != null) {
                msgObject = (JSONObject) dataObj;
            }
            return msgObject;
        }
    }

    public JSONObject getPatternsByLocale(String language, String region) {
        String responseStr = "";
        String i18nScope = VIPCfg.getInstance().getI18nScope();
        HttpRequester httpRequester = VIPCfg.getInstance().getVipService().getHttpRequester();
        if (i18nScope != null && !"".equalsIgnoreCase(i18nScope)) {
        	Map<String, Object> response = httpRequester.request(V2URL.getPatternURL(language, region,
                    httpRequester.getBaseURL()), ConstantsKeys.GET, null);
        	responseStr = (String) response.get(URLUtils.BODY);
        }
        if (null == responseStr || responseStr.equals("")) {
            return null;
        } else {
            Object dataObj = this.getCategoriesFromResponse(responseStr, PatternKeys.CATEGORIES);
            JSONObject msgObject = null;
            if (dataObj != null) {
                msgObject = (JSONObject) dataObj;
            }
            return msgObject;
        }
    }

    private Object getCategoriesFromResponse(String responseStr, String node) {
        Object msgObject = null;
        try {
            JSONObject responseObj = (JSONObject) JSONValue.parseWithException(responseStr);
            if (responseObj != null) {
                Object dataObj = responseObj.get(ConstantsKeys.DATA);
                if (dataObj != null && dataObj instanceof JSONObject) {
                    msgObject = ((JSONObject) dataObj).get(node);
                }
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            logger.error(e.getMessage());
        }
        return msgObject;
    }
}
