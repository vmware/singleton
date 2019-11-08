/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.JSONUtils;

public class LocaleOpt {

    private Logger logger = LoggerFactory.getLogger(LocaleOpt.class.getName());

    public LocaleOpt() {
    }

    public Map<String, String> getTerritoriesFromCLDR(String language) {
        String responseData = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                V2URL.getRegionListURL(language, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.GET, null);
        Map<String, String> respMap = null;
        try {
            JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(responseData);
            JSONArray jsonArray = (JSONArray) jsonObject.get(ConstantsKeys.DATA);
            if (jsonArray != null && !jsonArray.isEmpty()) {
                Map<String, Object> regionMap = JSONUtils.getMapFromJson(jsonArray.get(0).toString());
                respMap = (Map<String, String>) regionMap.get(ConstantsKeys.TERRITORIES);
            }
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return respMap;
    }

    public Map<String, String> getDisplayNamesFromCLDR(String language) {
        String responseData = VIPCfg.getInstance().getVipService().getHttpRequester()
                .request(
                        V2URL.getSupportedLanguageListURL(language,
                                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                        ConstantsKeys.GET, null);
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
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return dispMap;
    }

}
