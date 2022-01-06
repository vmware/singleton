/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormatCacheItem;
import com.vmware.vipclient.i18n.base.cache.TranslationCacheManager;
import com.vmware.vipclient.i18n.base.cache.LocaleCacheItem;
import com.vmware.vipclient.i18n.base.cache.PatternCacheItem;

import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

import java.util.Map;

public class FormattingCacheService {

    public FormattingCacheService() {

    }

    public void addPatterns(String locale, PatternCacheItem o) {
        String cacheKey = getPatternsCacheKey(locale);
        addFormattings(cacheKey, o);
    }

    public void addPatterns(String language, String region, PatternCacheItem o) {
        String cacheKey = getPatternsCacheKey(language, region);
        addFormattings(cacheKey, o);
    }

    public void addSupportedLanguages(BaseDTO dto, String locale, LocaleCacheItem o) {
        String cacheKey = getSupportedLanguagesCacheKey(dto, locale);
        addFormattings(cacheKey, o);
    }

    public void addRegions(String locale, LocaleCacheItem o) {
        String cacheKey = getRegionsCacheKey(locale);
        addFormattings(cacheKey, o);
    }

    public PatternCacheItem getPatterns(String locale) {
        String cacheKey = getPatternsCacheKey(locale);
        return (PatternCacheItem) getFormattings(cacheKey);
    }

    public PatternCacheItem getPatterns(String language, String region) {
        String cacheKey = getPatternsCacheKey(language, region);
        return (PatternCacheItem) getFormattings(cacheKey);
    }

    public LocaleCacheItem getSupportedLanguages(BaseDTO dto, String locale) {
        String cacheKey = getSupportedLanguagesCacheKey(dto, locale);
        return (LocaleCacheItem) getFormattings(cacheKey);
    }

    public LocaleCacheItem getRegions(String locale) {
        String cacheKey = getRegionsCacheKey(locale);
        return (LocaleCacheItem) getFormattings(cacheKey);
    }

    private String getPatternsCacheKey(String locale){
        return ConstantsKeys.PATTERNS_PREFIX + locale;
    }

    private String getPatternsCacheKey(String language, String region){
        return ConstantsKeys.PATTERNS_PREFIX + language + "-" + region;
    }

    private String getSupportedLanguagesCacheKey(BaseDTO dto, String locale){
        return ConstantsKeys.DISPNS_PREFIX + dto.getProductID() + ConstantsKeys.UNDERLINE + dto.getVersion() + ConstantsKeys.UNDERLINE + locale;
    }

    private String getRegionsCacheKey(String locale){
        return ConstantsKeys.REGIONS_PREFIX + locale;
    }

    private void addFormattings(String key, FormatCacheItem o) {
        if (null != key && null != o) {
            Cache c = TranslationCacheManager.getInstance()
                    .getCache(VIPCfg.CACHE_L2);
            if (c != null) {
                c.put(key, o);
            }
        }
    }

    private FormatCacheItem getFormattings(String key) {
        Map<String, String> o = null;
        Cache c = TranslationCacheManager.getInstance()
                .getCache(VIPCfg.CACHE_L2);
        if (c != null) {
            return (FormatCacheItem) c.get(key);
        }
        return null;
    }

}
