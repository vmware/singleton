/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.JSONUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RemoteLocaleOpt implements LocaleOpt{

    private Logger logger = LoggerFactory.getLogger(RemoteLocaleOpt.class.getName());

    public RemoteLocaleOpt() {
    }

    public Map<String, String> getRegions(String language) {
        logger.debug("Look for regions from Singleton Service for locale [{}]!", language);
    	Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                V2URL.getRegionListURL(language, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.GET, null);
    	String responseData = (String) response.get(URLUtils.BODY);
        if(responseData == null || "".equalsIgnoreCase(responseData))
            return null;
        Map<String, String> respMap = null;
        try {
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(responseData);
            JSONArray jsonArray = (JSONArray) jsonObject.get(ConstantsKeys.DATA);
            if (jsonArray != null && !jsonArray.isEmpty()) {
                Map<String, Object> regionMap = JSONUtils.getMapFromJson(jsonArray.get(0).toString());
                respMap = (Map<String, String>) regionMap.get(ConstantsKeys.TERRITORIES);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return respMap;
    }

    @Override
    public Map<String, String> getSupportedLanguages(String language) {
        logger.debug("Look for supported languages from Singleton Service for product [{}], version [{}], locale [{}]!",
                VIPCfg.getInstance().getProductName(), VIPCfg.getInstance().getVersion(), language);
    	Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester()
                .request(
                        V2URL.getSupportedLanguageListURL(language,
                                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                        ConstantsKeys.GET, null);
    	String responseData = (String) response.get(URLUtils.BODY);
        Map<String, String> dispMap = null;
        try {
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(responseData);
            Object data = jsonObject.get(ConstantsKeys.DATA);
            if (data == null || "".equals(data)) {
                return dispMap;
            }
            JSONObject jsonData = (JSONObject) data;
            JSONArray jsonArray = (JSONArray) jsonData.get(ConstantsKeys.LANGUAGES);
            if (jsonArray != null && !jsonArray.isEmpty()) {
                dispMap = new HashMap<String, String>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    Map<String, Object> tmpMap = JSONUtils.getMapFromJson(jsonArray.get(i).toString());
                    dispMap.put(tmpMap.get(ConstantsKeys.LANGUAGE_TAG).toString(),
                            tmpMap.get(ConstantsKeys.DISPLAY_NAME).toString());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return dispMap;
    }

}
