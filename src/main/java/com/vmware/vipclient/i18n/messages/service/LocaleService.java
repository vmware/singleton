/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.messages.dto.LocaleDTO;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

public class LocaleService {

    Logger                      logger        = LoggerFactory.getLogger(LocaleService.class.getName());
    private LocaleDTO dto = null;

    public LocaleService() {
    }

    public LocaleService(LocaleDTO dto) {
        this.dto = dto;
    }

    public Map<String, String> getRegions(String locale){
        Map<String, String> regionMap = getRegionsByLocale(locale);
        if (regionMap != null) {
            return regionMap;
        }
        if (!LocaleUtility.isDefaultLocale(locale)) {
            logger.info("Can't find regions for locale [{}], look for default locale's regions as fallback!", locale);
            regionMap = getRegionsByLocale(LocaleUtility.getDefaultLocale().toLanguageTag());
            if (regionMap != null) {
                new FormattingCacheService().addRegions(locale, regionMap);
                logger.debug("Default locale's regions is cached for locale [{}]!\n\n", locale);
            }
        }
        return regionMap;
    }

    public Map<String, String> getRegionsByLocale(String locale){
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
        if (!msgSourceQueueIter.hasNext()) {
            logger.error(ConstantsMsg.GET_REGIONS_FAILED_ALL);
            return regions;
        }
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        regions = dataSource.createLocaleOpt(dto).getRegions(locale);
        if (regions == null || regions.isEmpty()) {
            logger.debug(ConstantsMsg.GET_REGIONS_FAILED, dataSource.toString());
            regions = getRegionsFromDS(locale, msgSourceQueueIter);
        }
        return regions;
    }

    public Map<String, String> getDisplayNames(String locale) {
        Map<String, String> dispMap = new HashMap<String, String>();
        dispMap = getSupportedDisplayNamesByLocale(locale);
        if(dispMap != null && !dispMap.isEmpty()){
            return dispMap;
        }
        if (locale != null && !locale.isEmpty() && !LocaleUtility.isDefaultLocale(locale)) {
            logger.info("Can't find supported languages for locale [{}], look for default locale's languages as fallback!", locale);
            Locale fallbackLocale = LocaleUtility.getDefaultLocale();
            dispMap = getSupportedDisplayNamesByLocale(fallbackLocale.toLanguageTag());
            if (dispMap != null && dispMap.size() > 0) {
                new FormattingCacheService().addSupportedLanguages(dto, locale, dispMap);
                logger.debug("Default locale's displayNames is cached for product [{}], version [{}], locale [{}]!\n\n",
                        dto.getProductID(), dto.getVersion(), locale);
            }
        }
        return dispMap;
    }

    public Map<String, String> getSupportedDisplayNamesByLocale(String locale) {
        locale = locale.replace("_", "-").toLowerCase();
        Map<String, String> dispMap = new HashMap<String, String>();
        logger.debug("Look for displayNames from cache for locale [{}]", locale);
        FormattingCacheService formattingCacheService = new FormattingCacheService();
        dispMap = formattingCacheService.getSupportedLanguages(dto, locale);
        if (dispMap != null) {
            logger.debug("Find displayNames from cache for product [{}], version [{}], locale [{}]!", dto.getProductID(), dto.getVersion(), locale);
            return dispMap;
        }
        //cacheItem = new FormatCacheItem();
        dispMap = getSupportedLanguagesFromDS(locale, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        if (dispMap != null && dispMap.size() > 0) {
            logger.debug("Find the displayNames for product [{}], version [{}], locale [{}].\n", dto.getProductID(), dto.getVersion(), locale);
            formattingCacheService.addSupportedLanguages(dto, locale, dispMap);
            logger.debug("DisplayNames is cached for product [{}], version [{}], locale [{}]!\n\n", dto.getProductID(), dto.getVersion(), locale);
            return dispMap;
        }
        return dispMap;
    }


    private Map<String, String> getSupportedLanguagesFromDS(String locale, ListIterator<DataSourceEnum> msgSourceQueueIter) {
        Map<String, String> dispMap = new HashMap<String, String>();
        if (!msgSourceQueueIter.hasNext()) {
            logger.error(ConstantsMsg.GET_LANGUAGES_FAILED_ALL);
            return dispMap;
        }
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        dispMap = dataSource.createLocaleOpt(dto).getSupportedLanguages(locale);
        if (dispMap == null || dispMap.isEmpty()) {
            logger.debug(ConstantsMsg.GET_LANGUAGES_FAILED, dataSource.toString());
            dispMap = getSupportedLanguagesFromDS(locale, msgSourceQueueIter);
        }
        return dispMap;
    }
}
