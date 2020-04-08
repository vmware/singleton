/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.HttpRequester;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class ComponentBasedOpt extends BaseOpt implements Opt {
    private final Logger      logger = LoggerFactory.getLogger(ComponentBasedOpt.class.getName());
    private MessagesDTO dto    = null;

    public ComponentBasedOpt(final MessagesDTO dto) {
        this.dto = dto;
    }

    public Map<String, Object> getComponentMessages(Map<String, Object> cacheProps) {
        String url = V2URL.getComponentTranslationURL(this.dto,
                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());
        if (ConstantsKeys.LATEST.equals(this.dto.getLocale())) {
            url = url.replace("pseudo=false", "pseudo=true");
        }
        HttpRequester requester = VIPCfg.getInstance().getVipService().getHttpRequester();
        Map<String, String> headers = new HashMap<String, String>();
        URLUtils.addIfNoneMatchHeader (cacheProps, headers);
        Map<String, Object> response = requester.request(url, ConstantsKeys.GET,
        		null, headers);
        
        return response;
    }

    public JSONObject getMsgsJson(Map<String, Object> response) {
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
    
    public String getString() {
    	Map<String, Object> response = this.getComponentMessages(null);
		
		JSONObject jo = this.getMsgsJson(response);
		String k = this.dto.getKey();
        Object v = jo.get(k);
        return (v == null ? "" : (String) v);
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
