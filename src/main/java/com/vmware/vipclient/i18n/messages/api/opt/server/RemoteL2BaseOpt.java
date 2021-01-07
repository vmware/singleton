/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormatCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RemoteL2BaseOpt extends BaseOpt{
    private final Logger      logger = LoggerFactory.getLogger(RemoteL2BaseOpt.class.getName());


    public RemoteL2BaseOpt() {

    }

    public Map<String, Object> getResponse(String url, String method, Object requestData, FormatCacheItem cacheItem) {
        Map<String, String> headers = new HashMap<String, String>();
        if (cacheItem.getEtag() != null)
        	headers.put(URLUtils.IF_NONE_MATCH_HEADER, cacheItem.getEtag());
        
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester()
        		.request(url, method, requestData, headers);
        return response;
    }

	public Object getDataFromResponse(String responseBody){
		if (null == responseBody || responseBody.equals(""))
			return null;
		try {
			JSONObject respBody = (JSONObject) JSONValue.parse(responseBody);
			if (respBody != null && getResponseCode(respBody) == 200) {
				return respBody.get(ConstantsKeys.DATA);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
}
