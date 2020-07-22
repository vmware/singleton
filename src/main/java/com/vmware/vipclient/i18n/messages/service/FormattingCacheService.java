/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormatCacheItem;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.JSONUtils;
import org.json.simple.JSONObject;

import java.util.Map;

public class FormattingCacheService {

    public FormattingCacheService() {
    }

    public void addPatterns(String locale, JSONObject o) {
        String cacheKey = getPatternsCacheKey(locale);
        addFormattings(cacheKey, o);
    }

    public void addPatterns(String language, String region, JSONObject o) {
        String cacheKey = getPatternsCacheKey(language, region);
        addFormattings(cacheKey, o);
    }

    public void addSupportedLanguages(String locale, Map<String, String> o) {
        o = JSONUtils.map2SortMap(o);
        String cacheKey = getSupportedLanguagesCacheKey(locale);
        addFormattings(cacheKey, o);
    }

    public void addLanguagesNames(String locale, Map<String, String> o) {
        String cacheKey = getLanguagesNamesCacheKey(locale);
        addFormattings(cacheKey, o);
    }

    public void addRegions(String locale, Map<String, String> o) {
        o = JSONUtils.map2SortMap(o);
        String cacheKey = getRegionsCacheKey(locale);
        addFormattings(cacheKey, o);
    }

    public JSONObject getPatterns(String locale) {
        String cacheKey = getPatternsCacheKey(locale);
        return getFormattingPatterns(cacheKey);
    }

    public JSONObject getPatterns(String language, String region) {
        String cacheKey = getPatternsCacheKey(language, region);
        return getFormattingPatterns(cacheKey);
    }

    public Map<String, String> getSupportedLanguages(String locale) {
        String cacheKey = getSupportedLanguagesCacheKey(locale);
        return getFormattings(cacheKey);
    }

    public Map<String, String> getLanguagesNames(String locale) {
        String cacheKey = getLanguagesNamesCacheKey(locale);
        return getFormattings(cacheKey);
    }

    public Map<String, String> getRegions(String locale) {
        String cacheKey = getRegionsCacheKey(locale);
        return getFormattings(cacheKey);
    }

    private String getPatternsCacheKey(String locale){
        return ConstantsKeys.PATTERNS_PREFIX + locale;
    }

    private String getPatternsCacheKey(String language, String region){
        return ConstantsKeys.PATTERNS_PREFIX + language + "-" + region;
    }

    private String getSupportedLanguagesCacheKey(String locale){
        String productName = VIPCfg.getInstance().getProductName();
        String version = VIPCfg.getInstance().getVersion();
        return ConstantsKeys.DISPNS_PREFIX + productName + ConstantsKeys.UNDERLINE + version + ConstantsKeys.UNDERLINE + locale;
    }

    private String getLanguagesNamesCacheKey(String locale){
        return ConstantsKeys.LANGUAGES_PREFIX + locale;
    }

    private String getRegionsCacheKey(String locale){
        return ConstantsKeys.REGIONS_PREFIX + locale;
    }

    private JSONObject getFormattingPatterns(String key) {
        Map<String, String> o = getFormattings(key);
        if (o != null) {
            return new JSONObject(o);
        }
        return null;
    }

    private void addFormattings(String key, Map<String, String> o) {
        if (null != key && null != o) {
            Cache c = VIPCfg.getInstance().getCacheManager()
                    .getCache(VIPCfg.CACHE_L2);
            if (c != null) {
                c.put(key, new FormatCacheItem(o));
            }
        }
    }

    private Map<String, String> getFormattings(String key) {
        Map<String, String> o = null;
        Cache c = VIPCfg.getInstance().getCacheManager()
                .getCache(VIPCfg.CACHE_L2);
        if (c != null) {
        	FormatCacheItem cacheItem = (FormatCacheItem) c.get(key);
        	if (cacheItem != null)
        		o = cacheItem.getCachedData();
        }
        return o;
    }

}
