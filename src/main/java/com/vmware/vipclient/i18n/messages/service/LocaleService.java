/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormatCacheItem;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.JSONUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

public class LocaleService {

    Logger                      logger        = LoggerFactory.getLogger(LocaleService.class.getName());
    private static final String REGION_PREFIX = "region_";
    public static final String DISPN_PREFIX  = "dispn_";

    public LocaleService() {
    }

    public Map<String, Map<String, String>> getTerritoriesFromCLDR(
            List<String> languages) {
        Map<String, Map<String, String>> respMap = new HashMap<String, Map<String, String>>();
        for (String language : languages) {
            if(language != null && !language.isEmpty()) {
                Map<String, String> regionMap = getTerritoriesFromCLDR(language);
                respMap.put(language, regionMap);
            }
        }
        return respMap;
    }

    public Map<String, String> getTerritoriesFromCLDR(String language){
        language = language.replace("_", "-").toLowerCase();
        Map<String, String> regionMap = null;
        logger.trace("Look for region list of '" + language + "' from cache");
        Cache c = VIPCfg.getInstance().getCacheManager().getCache(VIPCfg.CACHE_L2);
        if (c != null) {
            FormatCacheItem cacheItem = (FormatCacheItem) c.get(REGION_PREFIX
                    + language);
            regionMap = cacheItem == null ? null : cacheItem.getCachedData();
        }
        if (regionMap != null) {
            logger.debug("Find regions from cache for locale [{}]!", language);
            return regionMap;
        }
        Map<String, String> tmpMap = getRegionsFromDS(language, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        if (c != null && tmpMap != null) {
            logger.debug("Find the regions for locale [{}].\n", language);
            regionMap = JSONUtils.map2SortMap(tmpMap);
            FormatCacheItem cacheItem = new FormatCacheItem(regionMap);
            c.put(REGION_PREFIX + language, cacheItem);
            logger.debug("Regions is cached for locale [{}]!\n\n", language);
            return regionMap;
        }
        if (!LocaleUtility.isDefaultLocale(language)) {
            logger.info("Can't find regions for locale [{}], look for English regions as fallback!", language);
            regionMap = getTerritoriesFromCLDR(LocaleUtility.getDefaultLocale().toLanguageTag());
        }
        return regionMap;
    }

    private Map<String, String> getRegionsFromDS(String language, ListIterator<DataSourceEnum> msgSourceQueueIter) {
        Map<String, String> regions = null;
        if (!msgSourceQueueIter.hasNext())
            return regions;
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        regions = dataSource.createLocaleOpt().getRegions(language);
        if (regions == null || regions.isEmpty()) {
            regions = getRegionsFromDS(language, msgSourceQueueIter);
        }
        return regions;
    }

    public Map<String, String> getDisplayNamesFromCLDR(String language) {
        Map<String, String> dispMap = new HashMap<String, String>();
        if(language == null || language.isEmpty()) {
            logger.warn("Locale is empty!");
            return dispMap;
        }
        dispMap = getSupportedDisplayNames(language);
        if ((dispMap == null || dispMap.isEmpty()) && !LocaleUtility.isDefaultLocale(language)) {
            logger.info("Can't find regions for locale [{}], look for English regions as fallback!", language);
            Locale fallbackLocale = LocaleUtility.getDefaultLocale();
            dispMap = getSupportedDisplayNames(fallbackLocale.toLanguageTag());
        }
        return dispMap;
    }

    private Map<String, String> getSupportedDisplayNames(String language) {
        language = language.replace("_", "-").toLowerCase();
        Map<String, String> dispMap = new HashMap<String, String>();
        logger.trace("Look for displayNames from cache");
        Cache c = VIPCfg.getInstance().getCacheManager()
                .getCache(VIPCfg.CACHE_L2);
        String productName = VIPCfg.getInstance().getProductName();
        String version = VIPCfg.getInstance().getVersion();
        String cacheKey = productName + ConstantsKeys.UNDERLINE + version + ConstantsKeys.UNDERLINE + DISPN_PREFIX + language;
        if (c != null) {
            FormatCacheItem cacheItem = (FormatCacheItem) c.get(cacheKey);
            dispMap = cacheItem == null ? null : cacheItem.getCachedData();
        }
        if (dispMap != null) {
            logger.debug("Find displayNames from cache for product [{}], version [{}], locale [{}]!", productName, version, language);
            return dispMap;
        }
        //cacheItem = new FormatCacheItem();
        dispMap = getSupportedLanguagesFromDS(language, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        if (dispMap != null && dispMap.size() > 0) {
            logger.debug("Find the displayNames for product [{}], version [{}], locale [{}].\n", productName, version, language);
            dispMap = JSONUtils.map2SortMap(dispMap);
            c.put(cacheKey, new FormatCacheItem(dispMap));
            logger.debug("DisplayNames is cached for product [{}], version [{}], locale [{}]!\n\n", productName, version, language);
            return dispMap;
        }
        return null;
    }


    private Map<String, String> getSupportedLanguagesFromDS(String language, ListIterator<DataSourceEnum> msgSourceQueueIter) {
        Map<String, String> dispMap = new HashMap<String, String>();
        if (!msgSourceQueueIter.hasNext())
            return dispMap;
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        dispMap = dataSource.createLocaleOpt().getSupportedLanguages(language);
        if (dispMap == null || dispMap.isEmpty()) {
            dispMap = getSupportedLanguagesFromDS(language, msgSourceQueueIter);
        }
        return dispMap;
    }
}
