/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.LocaleCacheItem;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.messages.dto.LocaleDTO;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
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
        LocaleCacheItem cacheItem = getRegionsByLocale(locale);
        if (!cacheItem.getCachedData().isEmpty()) {
            return cacheItem.getCachedData();
        }
        Iterator<Locale> fallbackLocalesIter = LocaleUtility.getFallbackLocales().iterator();
        while (fallbackLocalesIter.hasNext()) {
            String fallbackLocale = fallbackLocalesIter.next().toLanguageTag();
            if(fallbackLocale.equalsIgnoreCase(locale))
                continue;
            logger.info("Can't find regions for locale [{}], look for fallback locale [{}] regions as fallback!", locale, fallbackLocale);
            cacheItem = getRegionsByLocale(fallbackLocale);
            if (!cacheItem.getCachedData().isEmpty()) {
                break;
            }
        }
        return cacheItem.getCachedData();
    }

    public LocaleCacheItem getRegionsByLocale(String locale){
        if(locale != null && !locale.isEmpty())
            locale = locale.replace("_", "-").toLowerCase();
        LocaleCacheItem cacheItem = null;
        logger.debug("Look for region list from cache for locale [{}]", locale);
        FormattingCacheService formattingCacheService = new FormattingCacheService();
        cacheItem = formattingCacheService.getRegions(locale);
        if (cacheItem != null) {
            if (cacheItem.isExpired()) { // cacheItem has expired
                // Update the cache in a separate thread
                populateRegionsCache(locale, cacheItem);
            }
            logger.debug("Find regions from cache for locale [{}]!", locale);
            return cacheItem;
        }
        cacheItem = new LocaleCacheItem();
        getRegionsFromDS(locale, cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        if(!cacheItem.getCachedData().isEmpty()) {
            formattingCacheService.addRegions(locale, cacheItem);
            logger.debug("Regions is cached for locale [{}]!\n\n", locale);
        }
        return cacheItem;
    }

    private void getRegionsFromDS(String locale, LocaleCacheItem cacheItem, ListIterator<DataSourceEnum> msgSourceQueueIter) {
        if (!msgSourceQueueIter.hasNext()) {
            logger.error(ConstantsMsg.GET_REGIONS_FAILED_ALL, locale);
            return;
        }
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        dataSource.createLocaleOpt(dto).getRegions(locale, cacheItem);
        if (cacheItem.getCachedData().isEmpty()) {
            logger.warn(ConstantsMsg.GET_REGIONS_FAILED, locale, dataSource.toString());
            getRegionsFromDS(locale, cacheItem, msgSourceQueueIter);
        }
    }

    public Map<String, String> getDisplayNames(String locale) {
        LocaleCacheItem cacheItem = getSupportedDisplayNamesByLocale(locale);
        if(!cacheItem.getCachedData().isEmpty()){
            return cacheItem.getCachedData();
        }
        Iterator<Locale> fallbackLocalesIter = LocaleUtility.getFallbackLocales().iterator();
        while (fallbackLocalesIter.hasNext()) {
            String fallbackLocale = fallbackLocalesIter.next().toLanguageTag();
            if(fallbackLocale.equalsIgnoreCase(locale))
                continue;
            logger.info("Can't find supported languages for locale [{}], look for fallback locale [{}] languages as fallback!", locale, fallbackLocale);
            cacheItem = getSupportedDisplayNamesByLocale(fallbackLocale);
            if (!cacheItem.getCachedData().isEmpty()) {
                break;
            }
        }
        return cacheItem.getCachedData();
    }

    public LocaleCacheItem getSupportedDisplayNamesByLocale(String locale) {
        if(locale != null && !locale.isEmpty())
            locale = locale.replace("_", "-").toLowerCase();
        LocaleCacheItem cacheItem = null;
        logger.debug("Look for displayNames from cache for locale [{}]", locale);
        FormattingCacheService formattingCacheService = new FormattingCacheService();
        cacheItem = formattingCacheService.getSupportedLanguages(dto, locale);
        if (cacheItem != null) {
            if (cacheItem.isExpired()) { // cacheItem has expired
                // Update the cache in a separate thread
                populateSupportedLanguagesCache(locale, cacheItem);
            }
            logger.debug("Find displayNames from cache for product [{}], version [{}], locale [{}]!", dto.getProductID(), dto.getVersion(), locale);
            return cacheItem;
        }
        cacheItem = new LocaleCacheItem();
        getSupportedLanguagesFromDS(locale, cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        if(!cacheItem.getCachedData().isEmpty()) {
            formattingCacheService.addSupportedLanguages(dto, locale, cacheItem);
            logger.debug("DisplayNames is cached for product [{}], version [{}], locale [{}]!\n\n", dto.getProductID(), dto.getVersion(), locale);
        }
        return cacheItem;
    }


    private void getSupportedLanguagesFromDS(String locale, LocaleCacheItem cacheItem, ListIterator<DataSourceEnum> msgSourceQueueIter) {
        Map<String, String> dispMap = new HashMap<String, String>();
        if (!msgSourceQueueIter.hasNext()) {
            logger.error(ConstantsMsg.GET_LANGUAGES_FAILED_ALL, locale);
            return;
        }
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        dataSource.createLocaleOpt(dto).getSupportedLanguages(locale, cacheItem);
        if (cacheItem.getCachedData().isEmpty()) {
            logger.warn(ConstantsMsg.GET_LANGUAGES_FAILED, locale, dataSource.toString());
            getSupportedLanguagesFromDS(locale, cacheItem, msgSourceQueueIter);
        }
    }

    private void populateRegionsCache(String locale, LocaleCacheItem cacheItem) {
        Runnable runnable = () -> {
            try {
                // Pass cacheItem to getRegionsFromDS so that:
                // 1. A previously stored etag, if any, can be used for the next HTTP request.
                // 2. CacheItem properties such as etag, timestamp and maxAgeMillis can be refreshed
                // 	 with new properties from the next HTTP response.
                getRegionsFromDS(locale, cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
            } catch (Exception e) {
                // To make sure that the thread will close
                // even when an exception is thrown
                logger.error(e.getMessage());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void populateSupportedLanguagesCache(String locale, LocaleCacheItem cacheItem) {
        Runnable runnable = () -> {
            try {
                // Pass cacheItem to getSupportedLanguagesFromDS so that:
                // 1. A previously stored etag, if any, can be used for the next HTTP request.
                // 2. CacheItem properties such as etag, timestamp and maxAgeMillis can be refreshed
                // 	 with new properties from the next HTTP response.
                getSupportedLanguagesFromDS(locale, cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
            } catch (Exception e) {
                // To make sure that the thread will close
                // even when an exception is thrown
                logger.error(e.getMessage());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
