/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
/**
 * 
 *
 * @author shihu
 *
 */
public class AccessTokenUtils {

	public static String getToken(String url, String appid, String appSecre, String grantType) {
		// TODO Auto-generated method stub
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid",appid);
		params.put("appsecret", appSecre);
		params.put("grant_type", grantType);
		
		String result = HttpRequester.sendGet(url, params, null);
		
		JSONObject jobj = JSONObject.parseObject(result);
		boolean ok = jobj.getBoolean("success");
		if(ok) {
			JSONObject data = jobj.getJSONObject("data");
			return data.getString("access_token");
		}
		
		
		
		return null;
	}
	
	public static String regenerateToken(String url, String appid, String appSecre, String grantType) {
		// TODO Auto-generated method stub
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid",appid);
		params.put("appsecret", appSecre);
		params.put("grant_type", grantType);
		
		String result = HttpRequester.sendGet(url, params, null);
		
		JSONObject jobj = JSONObject.parseObject(result);
		boolean ok = jobj.getBoolean("success");
		if(ok) {
			JSONObject data = jobj.getJSONObject("data");
			return data.getString("access_token");
		}
		
		
		
		return null;
	}

}
