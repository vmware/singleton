/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class BaseOpt {
    protected String     responseStr;
    protected JSONObject responseJsonObj = null;

    /*
     * get messages from response string with JSON format
     */
    public Object getMessagesFromResponse(String responseStr, String node) {
        Object msgsObj = null;
        try {
            JSONObject responseObj = (JSONObject) JSONValue
                    .parseWithException(responseStr);
            if (responseObj != null) {
                JSONObject dataObj = (JSONObject) responseObj.get(ConstantsKeys.DATA);
                msgsObj = dataObj.get(node);
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
            JSONObject responseObj = (JSONObject) JSONValue
                    .parseWithException(responseStr);
            if (responseObj != null) {
                Object obj = responseObj.get(ConstantsKeys.RESPONSE);
                if (obj != null && !obj.toString().equalsIgnoreCase("")) {
                    JSONObject dataObj = (JSONObject) obj;
                    if (dataObj != null) {
                        msgObject = dataObj.get(node);
                    }
                }
            }
        } catch (Exception e) {
        }
        return msgObject;
    }

    protected void parseServerResponse() throws ParseException {
        if (null == responseJsonObj) {
            responseJsonObj = (JSONObject) JSONValue.parseWithException(responseStr);
        }
    }

    public Object getDataPart(JSONObject obj) {
        return obj.get(ConstantsKeys.DATA);
    }
    
    public String getLocale(JSONObject obj) {
    	return (String) ((JSONObject)getDataPart(obj)).get(ConstantsKeys.LOCALE);
    }

    public JSONObject getResponsePart(JSONObject obj) {
        return ((JSONObject) obj.get(ConstantsKeys.RESPONSE));
    }

    public int getResponseCode(JSONObject obj) {
        return Integer.parseInt(getResponsePart(obj).get(ConstantsKeys.CODE).toString());
    }
    
    public String getResponseMessage(JSONObject obj) {
        return (String) getResponsePart(obj).get(ConstantsKeys.MESSAGE);
    }

    public boolean isSuccess(int statusCode) {
        return statusCode >= 200 && statusCode <= 299;
    }
}
