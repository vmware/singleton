/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.HttpRequester;
import com.vmware.vipclient.i18n.base.cache.PatternCacheItem;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.messages.api.opt.PatternOpt;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RemotePatternOpt extends L2RemoteBaseOpt implements PatternOpt{
    Logger logger = LoggerFactory.getLogger(RemotePatternOpt.class);

    public void getPatterns(String locale, PatternCacheItem cacheItem) {
        logger.debug("Look for pattern from Singleton Service for locale [{}]!", locale);
        HttpRequester httpRequester = VIPCfg.getInstance().getVipService().getHttpRequester();
        getPatternsFromRemote(V2URL.getPatternURL(locale,
                    httpRequester.getBaseURL()), ConstantsKeys.GET, null, cacheItem);
    }

    public void getPatterns(String language, String region, PatternCacheItem cacheItem) {
        logger.debug("Look for pattern from Singleton Service for language [{}], region [{}]!", language, region);
        HttpRequester httpRequester = VIPCfg.getInstance().getVipService().getHttpRequester();
        getPatternsFromRemote(V2URL.getPatternURL(language, region,
                    httpRequester.getBaseURL()), ConstantsKeys.GET, null, cacheItem);
    }

    private void getPatternsFromRemote(String url, String method, Object requestData, PatternCacheItem cacheItem) {
        Map<String, Object> data = (Map<String, Object>) getDataFromResponse(url, method, requestData, cacheItem);
        if (null != data && !data.isEmpty()) {
            Map<String, Object> categoriesData = this.getCategoriesFromData(data);
            if (categoriesData != null) {
                cacheItem.addCachedData(categoriesData);
            }
        }
    }

    private Map<String, Object> getCategoriesFromData(Map<String, Object> dataObj) {
        Map<String, Object> categoriesObj = null;
        if (dataObj != null && dataObj instanceof JSONObject) {
            Object obj = ((JSONObject) dataObj).get(PatternKeys.CATEGORIES);
            if (obj != null && obj instanceof JSONObject) {
                categoriesObj = (Map<String, Object>) obj;
            }
        }
        return categoriesObj;
    }
}
