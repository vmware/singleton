/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.MessageOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentBasedOpt extends BaseOpt implements Opt, MessageOpt {
    private final Logger      logger = LoggerFactory.getLogger(ComponentBasedOpt.class.getName());
    private MessagesDTO dto    = null;
    
    public ComponentBasedOpt(final MessagesDTO dto) {
        this.dto = dto;
    }

    @Override
    public void getComponentMessages(MessageCacheItem cacheItem) {
        String url = V2URL.getComponentTranslationURL(this.dto,
                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());
        if (ConstantsKeys.LATEST.equals(this.dto.getLocale())) {
            url = url.replace("pseudo=false", "pseudo=true");
        }
        
        Map<String, String> headers = new HashMap<String, String>();
        if (cacheItem.getEtag() != null)
        	headers.put(URLUtils.IF_NONE_MATCH_HEADER, cacheItem.getEtag());
        
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester()
        		.request(url, ConstantsKeys.GET,null, headers);
        
        Integer responseCode = (Integer) response.get(URLUtils.RESPONSE_CODE);
        
        if (responseCode != null && (responseCode.equals(HttpURLConnection.HTTP_OK) || 
        		responseCode.equals(HttpURLConnection.HTTP_NOT_MODIFIED))) {
        	long timestamp = 0;
        	String etag = null;
        	Long maxAgeMillis = null;

        	if (response.get(URLUtils.RESPONSE_TIMESTAMP) != null)
        	    timestamp = (long) response.get(URLUtils.RESPONSE_TIMESTAMP);
        	if (response.get(URLUtils.HEADERS) != null)
        	    etag = URLUtils.createEtagString((Map<String, List<String>>) response.get(URLUtils.HEADERS));
	        if (response.get(URLUtils.MAX_AGE_MILLIS) != null)
                maxAgeMillis = (Long) response.get(URLUtils.MAX_AGE_MILLIS);

	        if (responseCode.equals(HttpURLConnection.HTTP_OK)) {
		        JSONObject respObj = (JSONObject) JSONValue.parse((String) response.get(URLUtils.BODY));
		        try {
	        		if (getResponseCode(respObj) == 200) {
				        Map<String,String> messages = this.getMsgsJson(response);
				        if (messages != null) {
				        	cacheItem.setCacheItem(this.dto.getLocale(), messages, etag, timestamp, maxAgeMillis);
				        }
        			}
	        	} catch (Exception e) {
	        		logger.error("Failed to get messages");
	        	}
	        } else {
                cacheItem.setCacheItem(this.dto.getLocale(), etag, timestamp, maxAgeMillis);
            }
        } 
    }

    private JSONObject getMsgsJson(Map<String, Object> response) {
    	if (response != null && response.get(URLUtils.RESPONSE_CODE) != null 
    			&& response.get(URLUtils.RESPONSE_CODE).equals(HttpURLConnection.HTTP_OK)) {
	    	String responseStr = (String) response.remove(URLUtils.BODY);
			if (null == responseStr || responseStr.equals(""))
				return null;
			else {
				if (ConstantsKeys.LATEST.equals(this.dto.getLocale())) {
					responseStr = responseStr.replace(ConstantsKeys.PSEUDOCHAR, "");
				}
	
				JSONObject msgObject = (JSONObject) this.getMessagesFromResponse(responseStr,
	                ConstantsKeys.MESSAGES);
	
				return msgObject;
			}
    	}
    	return null;
    }

    public String postString() {
    	Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(V2URL
                .getKeyTranslationURL(this.dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.POST, this.dto.getSource());
    	String responseStr = (String) response.get(URLUtils.BODY);
        Object o = this.getMessagesFromResponse(responseStr,
                ConstantsKeys.TRANSLATION);
        if (o != null)
            return (String) o;
        else {
            Object m = this.getStatusFromResponse(responseStr, ConstantsKeys.MESSAGE);
            if (m != null) {
                this.logger.warn((String) m);
            }
            return "";
        }
    }

    /**
     * post a set of sources to remote
     *
     * @param sourceSet
     * @return
     * @throws 
     */
    public String postSourceSet(final String sourceSet) {
        String status = "";
        if (sourceSet == null || "".equalsIgnoreCase(sourceSet))
            return status;
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                V2URL.getPostKeys(this.dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.POST, sourceSet);
        String responseStr = (String) response.get(URLUtils.BODY);
        Object o = this.getStatusFromResponse(responseStr, ConstantsKeys.CODE);
        if (o != null) {
            status = o.toString();
        }
        return status;
    }

    public String getTranslationStatus() {
        String status = "";
        Map<String, String> params = new HashMap<>();
        params.put("checkTranslationStatus", "true");
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(V2URL
                .getComponentTranslationURL(this.dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.GET, params);
        String responseStr = (String) response.get(URLUtils.BODY);
        if (null == responseStr || responseStr.equals(""))
            return status;
        else {
            Object o = this.getStatusFromResponse(responseStr, ConstantsKeys.CODE);
            if (o != null) {
                status = o.toString();
            }
        }
        return status;
    }

}
