/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class BaseOpt {

	/*
	 * get messages from response string with JSON format
	 */
	public Object getMessagesFromResponse(String responseStr, String node) {
		JSONObject msgsObj = null;
		try {
			JSONObject responseObj = (JSONObject) JSONValue
					.parseWithException(responseStr);
			if (responseObj != null) {
				JSONObject dataObj = (JSONObject) responseObj.get(ConstantsKeys.DATA);
				msgsObj = (JSONObject) dataObj.get(node);
				return msgsObj;
			}
		} catch (Exception e) {
		}
		return msgsObj;
	}
	
	/**
	 * get the status from response body
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
}
