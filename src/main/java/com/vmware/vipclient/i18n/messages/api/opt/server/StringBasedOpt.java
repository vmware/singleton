/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.messages.api.opt.KeyBasedOpt;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringBasedOpt extends BaseOpt implements Opt, KeyBasedOpt {
    private final Logger logger = LoggerFactory.getLogger(StringBasedOpt.class);

    private MessagesDTO dto = null;

    public StringBasedOpt(MessagesDTO dto) {
        this.dto = dto;
    }

    public JSONObject getComponentMessages() {
    	Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(V2URL
                .getComponentTranslationURL(dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.GET, null);
    	String responseStr = (String) response.get(URLUtils.BODY);
        if (null == responseStr || responseStr.equals("")) {
            return null;
        } else {
            Object dataObj = this.getMessagesFromResponse(responseStr,
                    ConstantsKeys.MESSAGES);
            JSONObject msgObject = null;
            if (dataObj != null && !("").equalsIgnoreCase(dataObj.toString())) {
                msgObject = (JSONObject) dataObj;
            }
            return msgObject;
        }
    }

    public String getString() {
        JSONObject jo = this.getComponentMessages();
        String k = dto.getKey();
        Object v = jo.get(k);
        return (v == null ? "" : (String) v);
    }

    public String postString() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("source", this.dto.getSource());
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(V2URL
                .getKeyTranslationURL(dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.POST, params);
        String responseStr = (String) response.get(URLUtils.BODY);
        Object o = this.getMessagesFromResponse(responseStr,
                ConstantsKeys.TRANSLATION);
        if (o != null)
            return (String) o;
        else
            return "";
    }

    /**
     * post a set of sources to remote
     * 
     * @param sourceSet
     * @return
     * @throws 
     */
    public String postSourceSet(String sourceSet) {
        String status = "";
        if (sourceSet == null || "".equalsIgnoreCase(sourceSet)) {
            return status;
        }
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                V2URL.getPostKeys(dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.POST, sourceSet);
        String responseStr = (String) response.get(URLUtils.BODY);
        Object o = this.getStatusFromResponse(responseStr, ConstantsKeys.CODE);
        if (o != null)
            status = o.toString();
        return status;
    }

    public String getTranslationStatus() {
        String status = "";
        Map<String, String> params = new HashMap<String, String>();
        params.put("checkTranslationStatus", "true");
        String getURL = V2URL.getComponentTranslationURL(dto,
                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(getURL, ConstantsKeys.GET,
                params);
        String responseStr = (String) response.get(URLUtils.BODY);
        if (null == responseStr || responseStr.equals("")) {
            return status;
        } else {
            Object o = this.getMessagesFromResponse(responseStr, ConstantsKeys.STATUS);
            if (o != null) {
                status = o.toString();
            }
        }
        return status;
    }

    public void getMultiVersionKeyMessages(MessageCacheItem cacheItem) {
        String url = V2URL.getMultiVersionKeyTranslationURL(this.dto,
                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());

        Map<String, String> headers = new HashMap<String, String>();
        if (cacheItem.getEtag() != null)
            headers.put(URLUtils.IF_NONE_MATCH_HEADER, cacheItem.getEtag());

        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester()
                .request(url, ConstantsKeys.GET,null, headers);

        Integer responseCode = (Integer) response.get(URLUtils.RESPONSE_CODE);

        if (responseCode != null && (responseCode.equals(HttpURLConnection.HTTP_OK) ||
                responseCode.equals(HttpURLConnection.HTTP_NOT_MODIFIED))) {

            long timestamp = response.get(URLUtils.RESPONSE_TIMESTAMP) == null ?
                    System.currentTimeMillis() : (long) response.get(URLUtils.RESPONSE_TIMESTAMP);
            String etag = URLUtils.createEtagString((Map<String, List<String>>) response.get(URLUtils.HEADERS));
            Long maxAgeMillis = response.get(URLUtils.MAX_AGE_MILLIS) == null ? null : (Long) response.get(URLUtils.MAX_AGE_MILLIS);

            if (responseCode.equals(HttpURLConnection.HTTP_OK)) {
                JSONObject respObj = (JSONObject) JSONValue.parse((String) response.get(URLUtils.BODY));
                try {
                    int businessCode = getResponseCode(respObj);
                    if (isSuccess(businessCode)) {
                        JSONArray dataArray = (JSONArray) this.getDataPart(respObj);
                        Map<String,String> messages = new HashMap<>();
                        for(Object obj : dataArray){
                            JSONObject keyTranslationObj = (JSONObject) obj;
                            messages.put((String) keyTranslationObj.get(ConstantsKeys.VERSION), (String) keyTranslationObj.get(ConstantsKeys.TRANSLATION));
                        }
                        if (messages != null) {
                            cacheItem.setCacheItem(messages, etag, timestamp, maxAgeMillis);
                        }
                    }else{
                        logger.warn(String.format(ConstantsMsg.SERVER_RETURN_ERROR, businessCode,  getResponseMessage(respObj)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                cacheItem.setCacheItem(etag, timestamp, maxAgeMillis);
            }
        }
    }
}
