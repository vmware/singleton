/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.HttpRequester;
import com.vmware.vipclient.i18n.base.cache.PatternCacheItem;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.messages.api.opt.PatternOpt;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class RemotePatternOpt extends RemoteL2BaseOpt implements PatternOpt{
    Logger logger = LoggerFactory.getLogger(RemotePatternOpt.class);

    public void getPatterns(String locale, PatternCacheItem cacheItem) {
        logger.debug("Look for pattern from Singleton Service for locale [{}]!", locale);
        HttpRequester httpRequester = VIPCfg.getInstance().getVipService().getHttpRequester();
        getPatternsFromRemote(locale, V2URL.getPatternURL(locale,
                    httpRequester.getBaseURL()), ConstantsKeys.GET, null, cacheItem);
    }

    public void getPatterns(String language, String region, PatternCacheItem cacheItem) {
        logger.debug("Look for pattern from Singleton Service for language [{}], region [{}]!", language, region);
        HttpRequester httpRequester = VIPCfg.getInstance().getVipService().getHttpRequester();
        getPatternsFromRemote(language+"-"+region, V2URL.getPatternURL(language, region,
                    httpRequester.getBaseURL()), ConstantsKeys.GET, null, cacheItem);
    }

    private void getPatternsFromRemote(String locale, String url, String method, Object requestData, PatternCacheItem cacheItem) {

        Map<String, Object> response = getResponse(url, method, requestData, cacheItem);

        Integer responseCode = (Integer) response.get(URLUtils.RESPONSE_CODE);

        if (responseCode != null && (responseCode.equals(HttpURLConnection.HTTP_OK) ||
                responseCode.equals(HttpURLConnection.HTTP_NOT_MODIFIED))) {

            long timestamp = response.get(URLUtils.RESPONSE_TIMESTAMP) != null ? (long) response.get(URLUtils.RESPONSE_TIMESTAMP) : System.currentTimeMillis();
            String etag = URLUtils.createEtagString((Map<String, List<String>>) response.get(URLUtils.HEADERS));
            Long maxAgeMillis = response.get(URLUtils.MAX_AGE_MILLIS) != null ? (Long) response.get(URLUtils.MAX_AGE_MILLIS) : null;

            if (responseCode.equals(HttpURLConnection.HTTP_OK)) {
                try {
                    String responseBody = (String) response.get(URLUtils.BODY);
                    Map<String, Object> patterns = getPatternsFromResponse(responseBody);
                    if (patterns != null) {
                        logger.debug("Found the pattern from Singleton Service for locale [{}].\n", locale);
                        cacheItem.set(patterns, etag, timestamp, maxAgeMillis);
                    }else{
                        logger.warn("Didn't find the pattern from Singleton Service for locale [{}].\n", locale);
                    }
                } catch (Exception e) {
                    logger.error("Failed to get pattern data from remote!", e);
                }
            }else{
                logger.debug("There is no update on Singleton Service for the pattern of locale [{}].\n", locale);
                cacheItem.set(etag, timestamp, maxAgeMillis);
            }
        }
    }

    private Map<String, Object> getPatternsFromResponse(String responseBody) {
        Map<String, Object> categoriesObj = null;
        JSONObject dataObj = (JSONObject) getDataFromResponse(responseBody);
        if (dataObj != null && dataObj instanceof JSONObject) {
            categoriesObj = ((JSONObject) dataObj.get(PatternKeys.CATEGORIES)).toMap();
        }
        return categoriesObj;
    }
}
