/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

public class LocaleService {

    Logger                      logger        = LoggerFactory.getLogger(LocaleService.class.getName());
    public static final String DISPN_PREFIX  = "dispn_";

    public LocaleService() {
    }

    public Map<String, String> getRegionsFromCLDR(String locale){
        locale = locale.replace("_", "-").toLowerCase();
        Map<String, String> regionMap = null;
        logger.debug("Look for region list from cache for locale [{}]", locale);
        FormattingCacheService formattingCacheService = new FormattingCacheService();
        regionMap = formattingCacheService.getRegions(locale);
        if (regionMap != null) {
            logger.debug("Find regions from cache for locale [{}]!", locale);
            return regionMap;
        }
        regionMap = getRegionsFromDS(locale, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        if (regionMap != null) {
            logger.debug("Find the regions for locale [{}].\n", locale);
            formattingCacheService.addRegions(locale, regionMap);
            logger.debug("Regions is cached for locale [{}]!\n\n", locale);
            return regionMap;
        }
        return regionMap;
    }

    private Map<String, String> getRegionsFromDS(String locale, ListIterator<DataSourceEnum> msgSourceQueueIter) {
        Map<String, String> regions = null;
        if (!msgSourceQueueIter.hasNext())
            return regions;
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        regions = dataSource.createLocaleOpt().getRegions(locale);
        if (regions == null || regions.isEmpty()) {
            regions = getRegionsFromDS(locale, msgSourceQueueIter);
        }
        return regions;
    }

    public Map<String, String> getSupportedDisplayNames(String locale) {
        locale = locale.replace("_", "-").toLowerCase();
        Map<String, String> dispMap = new HashMap<String, String>();
        logger.debug("Look for displayNames from cache for locale [{}]", locale);
        String productName = VIPCfg.getInstance().getProductName();
        String version = VIPCfg.getInstance().getVersion();
        FormattingCacheService formattingCacheService = new FormattingCacheService();
        dispMap = formattingCacheService.getSupportedLanguages(locale);
        if (dispMap != null) {
            logger.debug("Find displayNames from cache for product [{}], version [{}], locale [{}]!", productName, version, locale);
            return dispMap;
        }
        //cacheItem = new FormatCacheItem();
        dispMap = getSupportedLanguagesFromDS(locale, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        if (dispMap != null && dispMap.size() > 0) {
            logger.debug("Find the displayNames for product [{}], version [{}], locale [{}].\n", productName, version, locale);
            formattingCacheService.addSupportedLanguages(locale, dispMap);
            logger.debug("DisplayNames is cached for product [{}], version [{}], locale [{}]!\n\n", productName, version, locale);
            return dispMap;
        }
        return dispMap;
    }


    private Map<String, String> getSupportedLanguagesFromDS(String locale, ListIterator<DataSourceEnum> msgSourceQueueIter) {
        Map<String, String> dispMap = new HashMap<String, String>();
        if (!msgSourceQueueIter.hasNext())
            return dispMap;
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        dispMap = dataSource.createLocaleOpt().getSupportedLanguages(locale);
        if (dispMap == null || dispMap.isEmpty()) {
            dispMap = getSupportedLanguagesFromDS(locale, msgSourceQueueIter);
        }
        return dispMap;
    }
}
