/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
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
        JSONObject patterns = getPatternsByLocale(locale);
        return (JSONObject) patterns.get(category);
    }

    public JSONObject getPatterns(String locale) {
        JSONObject patterns = getPatternsByLocale(locale);
        if (patterns != null) {
            return patterns;
        }
        Iterator<Locale> fallbackLocalesIter = LocaleUtility.getFallbackLocales().iterator();
        while (fallbackLocalesIter.hasNext()) {
            String fallbackLocale = fallbackLocalesIter.next().toLanguageTag();
            if(fallbackLocale.equalsIgnoreCase(locale))
                continue;
            logger.info("Can't find pattern for locale [{}], look for fallback locale [{}] pattern as fallback!", locale, fallbackLocale);
            patterns = getPatternsByLocale(fallbackLocale);
            if (patterns != null) {
                new FormattingCacheService().addPatterns(locale, patterns);
                logger.debug("Fallback locale [{}] pattern is cached for locale [{}]!\n\n", fallbackLocale, locale);
                return patterns;
            }
        }
        return null;
    }

    public JSONObject getPatternsByLocale(String locale) {
        if(locale != null && !locale.isEmpty())
            locale = locale.replace("_", "-");
        JSONObject patterns = null;
        logger.debug("Look for pattern from cache for locale [{}]!", locale);
        FormattingCacheService formattingCacheService = new FormattingCacheService();
        patterns = (JSONObject) formattingCacheService.getPatterns(locale);// key
        if (patterns != null) {
            logger.debug("Find pattern from cache for locale [{}]!", locale);
            return patterns;
        }
        patterns = getPatternsFromDS(locale, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        if (patterns != null) {
            logger.debug("Find the pattern for locale [{}].\n", locale);// [datetime] and
            formattingCacheService.addPatterns(locale, patterns);
            logger.debug("Pattern is cached for locale [{}]!\n\n", locale);
            return patterns;
        }
        return null;
    }

    public JSONObject getPatterns(String language, String region) {
        JSONObject patterns = getPatternsByLanguageRegion(language, region);
        if (patterns != null) {
            return patterns;
        }
        Iterator<Locale> fallbackLocalesIter = LocaleUtility.getFallbackLocales().iterator();
        while (fallbackLocalesIter.hasNext()) {
            String fallbackLocale = fallbackLocalesIter.next().toLanguageTag();
            if(fallbackLocale.equalsIgnoreCase(new Locale(language, region).toLanguageTag()))
                continue;
            logger.info("Can't find pattern for language [{}] region [{}], look for fallback locale [{}] pattern as fallback!", language, region, fallbackLocale);
            patterns = getPatternsByLocale(fallbackLocale);
            if (patterns != null) {
                new FormattingCacheService().addPatterns(language, region, patterns);
                logger.debug("Fallback locale [{}] pattern is cached for language [{}], region [{}]!\n\n", fallbackLocale, language, region);
                return patterns;
            }
        }
        return null;
    }

    public JSONObject getPatternsByLanguageRegion(String language, String region) {
        language = language.replace("_", "-");
        JSONObject patterns = null;
        logger.debug("Look for pattern from cache for language [{}], region [{}]!", language, region);
        FormattingCacheService formattingCacheService = new FormattingCacheService();
        patterns = (JSONObject) formattingCacheService.getPatterns(language, region);// key
        if (patterns != null) {
            logger.debug("Find pattern from cache for language [{}], region [{}]!", language, region);
            return patterns;
        }
        patterns = getPatternsFromDS(language, region, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        if (patterns != null) {
            logger.debug("Find the pattern for language [{}], region [{}].\n", language, region);// [datetime]
            // and
            formattingCacheService.addPatterns(language, region, patterns);
            logger.debug("Pattern is cached for language [{}], region [{}]!\n\n", language, region);
            return patterns;
        }
        return null;
    }

    private JSONObject getPatternsFromDS(String locale, ListIterator<DataSourceEnum> msgSourceQueueIter) {
        JSONObject patterns = null;
        if (!msgSourceQueueIter.hasNext())
            return patterns;
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        patterns = dataSource.createPatternOpt().getPatterns(locale);
        if (patterns == null || patterns.isEmpty()) {
            patterns = getPatternsFromDS(locale, msgSourceQueueIter);
        }
        return patterns;
    }

    private JSONObject getPatternsFromDS(String language, String region, ListIterator<DataSourceEnum> msgSourceQueueIter) {
        JSONObject patterns = null;
        if (!msgSourceQueueIter.hasNext())
            return patterns;
        DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
        patterns = dataSource.createPatternOpt().getPatterns(language, region);
        if (patterns == null || patterns.isEmpty()) {
            patterns = getPatternsFromDS(language, region, msgSourceQueueIter);
        }
        return patterns;
    }
}
