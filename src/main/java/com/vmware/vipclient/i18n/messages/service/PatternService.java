/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.PatternCacheItem;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Locale;

/**
 * The class represents date formatting
 */
public class PatternService {
    Logger logger = LoggerFactory.getLogger(PatternService.class);

    public JSONObject getPatternsByCategory(String locale, String category) {
        PatternCacheItem cacheItem = getPatternsByLocale(locale);
        JSONObject patterns = new JSONObject(cacheItem.getCachedData());
        return (JSONObject) patterns.get(category);
    }

    public JSONObject getPatterns(String locale) {
        PatternCacheItem cacheItem = getPatternsByLocale(locale);
        if (!cacheItem.getCachedData().isEmpty()) {
            return new JSONObject(cacheItem.getCachedData());
        }
        Iterator<Locale> fallbackLocalesIter = LocaleUtility.getFallbackLocales().iterator();
        while (fallbackLocalesIter.hasNext()) {
            String fallbackLocale = fallbackLocalesIter.next().toLanguageTag();
            if(fallbackLocale.equalsIgnoreCase(locale))
                continue;
            logger.info("Can't find pattern for locale [{}], look for fallback locale [{}] pattern as fallback!", locale, fallbackLocale);
            cacheItem = getPatternsByLocale(fallbackLocale);
            if (!cacheItem.getCachedData().isEmpty()) {
                return new JSONObject(cacheItem.getCachedData());
            }
        }
        return null;
    }

    public PatternCacheItem getPatternsByLocale(String locale) {
        locale = locale.replace("_", "-");
        PatternCacheItem cacheItem = null;
        logger.debug("Look for pattern from cache for locale [{}]!", locale);
        FormattingCacheService formattingCacheService = new FormattingCacheService();
        cacheItem = formattingCacheService.getPatterns(locale);// key
        if (cacheItem != null) {
            if (cacheItem.isExpired()) { // cacheItem has expired
                // Update the cache in a separate thread
                populateCacheTask(locale, cacheItem);
            }
            logger.debug("Find pattern from cache for locale [{}]!", locale);
            return cacheItem;
        }
        cacheItem = new PatternCacheItem();
        getPatternsFromDS(locale, cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        formattingCacheService.addPatterns(locale, cacheItem);
        logger.debug("Pattern is cached for locale [{}]!\n\n", locale);
        return cacheItem;
    }

    public JSONObject getPatterns(String language, String region) {
        PatternCacheItem cacheItem = getPatternsByLanguageRegion(language, region);
        if (!cacheItem.getCachedData().isEmpty()) {
            return new JSONObject(cacheItem.getCachedData());
        }
        Iterator<Locale> fallbackLocalesIter = LocaleUtility.getFallbackLocales().iterator();
        while (fallbackLocalesIter.hasNext()) {
            String fallbackLocale = fallbackLocalesIter.next().toLanguageTag();
            if(fallbackLocale.equalsIgnoreCase(new Locale(language, region).toLanguageTag()))
                continue;
            logger.info("Can't find pattern for language [{}] region [{}], look for fallback locale [{}] pattern as fallback!", language, region, fallbackLocale);
            cacheItem = getPatternsByLocale(fallbackLocale);
            if (!cacheItem.getCachedData().isEmpty()) {
                return new JSONObject(cacheItem.getCachedData());
            }
        }
        return null;
    }

    public PatternCacheItem getPatternsByLanguageRegion(String language, String region) {
        language = language.replace("_", "-");
        PatternCacheItem cacheItem = null;
        logger.debug("Look for pattern from cache for language [{}], region [{}]!", language, region);
        FormattingCacheService formattingCacheService = new FormattingCacheService();
        cacheItem = formattingCacheService.getPatterns(language, region);// key
        if (cacheItem != null) {
            if (cacheItem.isExpired()) { // cacheItem has expired
                // Update the cache in a separate thread
                populateCacheTask(language, region, cacheItem);
            }
            logger.debug("Find pattern from cache for language [{}], region [{}]!", language, region);
            return cacheItem;
        }
        cacheItem = new PatternCacheItem();
        getPatternsFromDS(language, region, cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        formattingCacheService.addPatterns(language, region, cacheItem);
        logger.debug("Pattern is cached for language [{}], region [{}]!\n\n", language, region);
        return cacheItem;
    }

    private void getPatternsFromDS(String locale, PatternCacheItem cacheItem, ListIterator<DataSourceEnum> msgSourceQueueIter) {
        if (!msgSourceQueueIter.hasNext())
            return;
        long timestampOld = cacheItem.getTimestamp();
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        dataSource.createPatternOpt().getPatterns(locale, cacheItem);
        long timestampNew = cacheItem.getTimestamp();
        if (timestampNew == timestampOld) {
            logger.debug(FormatUtils.format(ConstantsMsg.GET_PATTERNS_FAILED, locale, dataSource.toString()));
        }
        // Skip this block if timestamp is not 0 (which means cacheItem is in the cache) regardless if cacheItem is expired or not.
        // Otherwise, try the next dataSource in the queue.
        if (timestampNew == 0) {
            if (msgSourceQueueIter.hasNext()) {
                getPatternsFromDS(locale, cacheItem, msgSourceQueueIter);
            }else{
                logger.debug(FormatUtils.format(ConstantsMsg.GET_PATTERNS_FAILED_ALL, locale));
            }
        }
    }

    private void getPatternsFromDS(String language, String region, PatternCacheItem cacheItem, ListIterator<DataSourceEnum> msgSourceQueueIter) {
        if (!msgSourceQueueIter.hasNext())
            return;
        long timestampOld = cacheItem.getTimestamp();
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        dataSource.createPatternOpt().getPatterns(language, region, cacheItem);
        long timestampNew = cacheItem.getTimestamp();
        if (timestampNew == timestampOld) {
            logger.debug(FormatUtils.format(ConstantsMsg.GET_PATTERNS_FAILED_1, language, region, dataSource.toString()));
        }
        // Skip this block if timestamp is not 0 (which means cacheItem is in the cache) regardless if cacheItem is expired or not.
        // Otherwise, try the next dataSource in the queue.
        if (timestampNew == 0) {
            if (msgSourceQueueIter.hasNext()) {
                getPatternsFromDS(language, region, cacheItem, msgSourceQueueIter);
            }else{
                logger.debug(FormatUtils.format(ConstantsMsg.GET_PATTERNS_FAILED_ALL_1, language, region));
            }
        }
    }

    private void populateCacheTask(String locale, PatternCacheItem cacheItem) {
        Runnable runnable = () -> {
            try {
                // Pass cacheItem to getPatternsFromDS so that:
                // 1. A previously stored etag, if any, can be used for the next HTTP request.
                // 2. CacheItem properties such as etag, timestamp and maxAgeMillis can be refreshed
                // 	 with new properties from the next HTTP response.
                getPatternsFromDS(locale, cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
            } catch (Exception e) {
                // To make sure that the thread will close
                // even when an exception is thrown
                logger.error(e.getMessage());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void populateCacheTask(String language, String region, PatternCacheItem cacheItem) {
        Runnable runnable = () -> {
            try {
                // Pass cacheItem to getPatternsFromDS so that:
                // 1. A previously stored etag, if any, can be used for the next HTTP request.
                // 2. CacheItem properties such as etag, timestamp and maxAgeMillis can be refreshed
                // 	 with new properties from the next HTTP response.
                getPatternsFromDS(language, region, cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
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
