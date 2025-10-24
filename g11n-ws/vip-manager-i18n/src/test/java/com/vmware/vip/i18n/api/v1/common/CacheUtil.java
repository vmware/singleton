/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.common;

import org.json.JSONObject;
import org.json.JSONException;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.vip.common.utils.JSONUtils;

public class CacheUtil {
    
    public static void cacheKey(WebApplicationContext webApplicationContext,String result) {
        JSONObject jsonObj=JSONUtils.string2JSON(result);
        JSONObject data= null;
        String key = null;
        try {
            data=(JSONObject) jsonObj.get("data");
            key=(String) data.get("key");
        } catch (JSONException e) {
        	
        }
        MockServletContext application = (MockServletContext) webApplicationContext.getServletContext();
        application.setAttribute("key", key);
    }
    
    public static void cacheSessionAndToken(WebApplicationContext webApplicationContext,String authenticationResult) {
        JSONObject jsonObj=JSONUtils.string2JSON(authenticationResult);
        String sessionID = null;
        String token = null;
        try {
            sessionID=(String) jsonObj.get("sessionID");
            token=(String) jsonObj.get("token");
        } catch (JSONException e) {
        	
        }
        MockServletContext application = (MockServletContext) webApplicationContext.getServletContext();
        application.setAttribute("sessionID", sessionID);
        application.setAttribute("token", token);
    }  
}
