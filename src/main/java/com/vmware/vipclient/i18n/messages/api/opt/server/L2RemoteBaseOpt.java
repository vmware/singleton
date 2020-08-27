/*
 * Copyright 2019-2020 VMware, Inc.
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

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class L2RemoteBaseOpt extends BaseOpt{
    private final Logger      logger = LoggerFactory.getLogger(L2RemoteBaseOpt.class.getName());


    public L2RemoteBaseOpt() {

    }

    public Object getDataFromResponse(String url, String method, Object requestData, FormatCacheItem cacheItem) {
        Map<String, String> headers = new HashMap<String, String>();
        if (cacheItem.getEtag() != null)
        	headers.put(URLUtils.IF_NONE_MATCH_HEADER, cacheItem.getEtag());
        
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester()
        		.request(url, method, requestData, headers);
        
        Integer responseCode = (Integer) response.get(URLUtils.RESPONSE_CODE);
        
        if (responseCode != null && (responseCode.equals(HttpURLConnection.HTTP_OK) || 
        		responseCode.equals(HttpURLConnection.HTTP_NOT_MODIFIED))) {
        	
        	if (response.get(URLUtils.RESPONSE_TIMESTAMP) != null)
	        	cacheItem.setTimestamp((long) response.get(URLUtils.RESPONSE_TIMESTAMP) );
        	if (response.get(URLUtils.HEADERS) != null)
	        	cacheItem.setEtag(URLUtils.createEtagString((Map<String, List<String>>) response.get(URLUtils.HEADERS)));
	        if (response.get(URLUtils.MAX_AGE_MILLIS) != null)
	        	cacheItem.setMaxAgeMillis((Long) response.get(URLUtils.MAX_AGE_MILLIS));
			      
	        if (responseCode.equals(HttpURLConnection.HTTP_OK)) {
				try {
					String responseStr = (String) response.get(URLUtils.BODY);
					if (null == responseStr || responseStr.equals(""))
						return null;
		        	JSONObject respBody = (JSONObject) JSONValue.parse(responseStr);
	        		if (respBody!=null && getResponseCode(respBody) == 200){
				        return respBody.get(ConstantsKeys.DATA);
        			}
	        	} catch (Exception e) {
	        		logger.error("Failed to get L2 data from remote!");
	        	}
	        }

        }
		return null;
    }
}
