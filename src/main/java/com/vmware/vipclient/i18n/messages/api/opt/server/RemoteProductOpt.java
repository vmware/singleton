/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.ProductOpt;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RemoteProductOpt extends BaseOpt implements ProductOpt {

    private BaseDTO dto = null;

    public RemoteProductOpt(BaseDTO dto) {
        this.dto = dto;
    }

    /**
     * get supported components from vip(non-Javadoc)
     *
     */
    public List<String> getComponents() {
        JSONArray msgObject = new JSONArray();
        String responseStr = "";
        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(
                V2URL.getComponentListURL(dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.GET, null);
        responseStr = (String) response.get(URLUtils.BODY);
        if (null != responseStr && !responseStr.equals("")) {
            Object dataObj = this.getMessagesFromResponse(responseStr,
                    ConstantsKeys.COMPONENTS);
            if (dataObj != null) {
                msgObject = (JSONArray) dataObj;
            }
        }
        return msgObject.toList().stream()
        	    .map(obj -> obj == null ? null : obj.toString())
        	    .collect(Collectors.toList());
    }

    /**
     * get supported locales from vip(non-Javadoc)
     *
     */
    public void getSupportedLocales(MessageCacheItem cacheItem) {
        Map<String, String> headers = new HashMap<String, String>();
        if (cacheItem.getEtag() != null)
            headers.put(URLUtils.IF_NONE_MATCH_HEADER, cacheItem.getEtag());

        Map<String, Object> response = VIPCfg.getInstance().getVipService().getHttpRequester().request(V2URL.getSupportedLocaleListURL(
                dto, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()), ConstantsKeys.GET, null, headers);

        Integer responseCode = (Integer) response.get(URLUtils.RESPONSE_CODE);
        if (responseCode != null && (responseCode.equals(HttpURLConnection.HTTP_OK) ||
                responseCode.equals(HttpURLConnection.HTTP_NOT_MODIFIED))) {

            long timestamp = response.get(URLUtils.RESPONSE_TIMESTAMP) == null ?
                    System.currentTimeMillis() : (long) response.get(URLUtils.RESPONSE_TIMESTAMP);

            String etag = URLUtils.createEtagString((Map<String, List<String>>) response.get(URLUtils.HEADERS));

            Long maxAgeMillis = response.get(URLUtils.MAX_AGE_MILLIS) == null ? null : (Long) response.get(URLUtils.MAX_AGE_MILLIS);

            if (responseCode.equals(HttpURLConnection.HTTP_OK)) {
                String responseStr = (String) response.get(URLUtils.BODY);
                if (responseStr != null && !responseStr.isEmpty()) {
                    Object dataObj = this.getMessagesFromResponse(responseStr, ConstantsKeys.LOCALES);
                    if (dataObj != null) {
                        List<String> supportedLocales = ((JSONArray) dataObj).toList().stream()
                        	    .map(obj -> obj == null ? null : obj.toString())
                        	    .collect(Collectors.toList());
                        Map<String, String> languageTags = new HashMap<>();
                        if (!supportedLocales.isEmpty()) {
                            for (String languageTag : supportedLocales) {
                                languageTags.put(languageTag, languageTag);
                            }
                            cacheItem.setCacheItem(languageTags, etag, timestamp, maxAgeMillis);
                        }
                    }
                }
            } else {
                cacheItem.setCacheItem(null, etag, timestamp, maxAgeMillis);
            }
        }
    }
}
