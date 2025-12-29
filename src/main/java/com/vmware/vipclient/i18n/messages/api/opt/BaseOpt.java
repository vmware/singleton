/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt;

import org.json.JSONObject;

import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.JSONUtils;

public class BaseOpt {
    protected String     responseStr;
    protected JSONObject responseJsonObj = null;

    /*
     * get messages from response string with JSON format
     */
    public Object getMessagesFromResponse(String responseStr, String node) {
        Object msgsObj = null;
        try {
            JSONObject responseObj = new JSONObject(responseStr);
            if (responseObj != null) {
                JSONObject dataObj = (JSONObject) JSONUtils.getFromJSONObject(responseObj, ConstantsKeys.DATA);
                msgsObj = JSONUtils.getFromJSONObject(dataObj, node);
                return msgsObj;
            }
        } catch (Exception e) {
            return msgsObj;
        }
        return msgsObj;
    }

    /**
     * get the status from response body
     * 
     * @param responseStr
     * @param node
     * @return
     */
    public Object getStatusFromResponse(String responseStr, String node) {
        Object msgObject = null;
        if (responseStr == null || responseStr.equalsIgnoreCase(""))
            return msgObject;
        try {
            JSONObject responseObj = new JSONObject(responseStr);
            if (responseObj != null) {
                Object obj = JSONUtils.getFromJSONObject(responseObj, ConstantsKeys.RESPONSE);
                if (obj != null && !obj.toString().equalsIgnoreCase("")) {
                    JSONObject dataObj = (JSONObject) obj;
                    if (dataObj != null) {
                        msgObject = JSONUtils.getFromJSONObject(dataObj, node);
                    }
                }
            }
        } catch (Exception e) {
        }
        return msgObject;
    }

    protected void parseServerResponse() throws Exception {
        if (null == responseJsonObj) {
            responseJsonObj = new JSONObject(responseStr);
        }
    }

    public Object getDataPart(JSONObject obj) {
        return JSONUtils.getFromJSONObject(obj, ConstantsKeys.DATA);
    }
    
    public String getLocale(JSONObject obj) {
    	return (String) JSONUtils.getFromJSONObject(((JSONObject)getDataPart(obj)), ConstantsKeys.LOCALE);
    }

    public JSONObject getResponsePart(JSONObject obj) {
        return ((JSONObject) JSONUtils.getFromJSONObject(obj, ConstantsKeys.RESPONSE));
    }

    public int getResponseCode(JSONObject obj) {
        return Integer.parseInt(JSONUtils.getFromJSONObject(getResponsePart(obj), ConstantsKeys.CODE).toString());
    }
    
    public String getResponseMessage(JSONObject obj) {
        return (String) JSONUtils.getFromJSONObject(getResponsePart(obj), ConstantsKeys.MESSAGE);
    }

    public boolean isSuccess(int statusCode) {
        return statusCode >= 200 && statusCode <= 299;
    }
}
