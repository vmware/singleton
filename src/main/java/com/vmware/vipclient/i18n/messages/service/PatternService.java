/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.i18n.dto.LocaleDataDTO;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.messages.api.opt.local.LocalPatternOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.RemotePatternOpt;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Locale;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

/**
 * The class represents date formatting
 */
public class PatternService {
    Logger logger = LoggerFactory.getLogger(PatternService.class);
    private static final String PATTERNS_PREFIX = "patterns_";

    public JSONObject getPatternsByCategory(String locale, String category) {
        JSONObject patterns = getPatterns(locale);
        return (JSONObject) patterns.get(category);
    }

    public JSONObject getPatterns(String locale) {
        if(locale == null || locale.isEmpty()) {
            logger.warn("Locale is empty!");
            return null;
        }
        locale = locale.replace("_", "-").toLowerCase();
        JSONObject patterns = null;
        logger.debug("Look for pattern from cache for locale [{}]!", locale);
        String cacheKey = PATTERNS_PREFIX + locale;
        patterns = new PatternCacheService().lookForPatternsFromCache(cacheKey);// key
        if (patterns != null) {
            logger.debug("Find pattern from cache for locale [{}]!", locale);
            return patterns;
        }
        patterns = getPatternsFromDS(locale);
        if (patterns != null) {
            logger.debug("Find the pattern for locale [{}].\n", locale);// [datetime] and
            new PatternCacheService().addPatterns(cacheKey, patterns);
            logger.debug("Pattern is cached for locale [{}]!\n\n", locale);
            return patterns;
        }
        if (!LocaleUtility.isDefaultLocale(locale)) {
            logger.info("Can't find pattern for locale [{}], look for English pattern as fallback!", locale);
            patterns = getPatterns(ConstantsKeys.EN);
        }
        return patterns;
    }

    public JSONObject getPatterns(String language, String region) {
        if((language == null || language.isEmpty()) && (region == null || region.isEmpty())) {
            logger.warn("Both language and region are empty!");
            return null;
        }
        language = language.replace("_", "-").toLowerCase();
        region = region.toLowerCase();
        JSONObject patterns = null;
        String key = PATTERNS_PREFIX + language + "-" + region;
        logger.debug("Look for pattern from cache for language [{}], region [{}]!", language, region);
        patterns = new PatternCacheService().lookForPatternsFromCache(key);// key
        if (patterns != null) {
            logger.debug("Find pattern from cache for language [{}], region [{}]!", language, region);
            return patterns;
        }
        patterns = getPatternsFromDS(language, region);
        if (patterns != null) {
            logger.debug("Find the pattern for language [{}], region [{}].\n", language, region);// [datetime]
            // and
            new PatternCacheService().addPatterns(key, patterns);
            logger.debug("Pattern is cached for language [{}], region [{}]!\n\n", language, region);
            return patterns;
        }
        if (!LocaleUtility.isDefaultLocale(new Locale(language, region))) {
            logger.info("Can't find pattern for language [{}] region [{}], look for English pattern as fallback!", language, region);
            patterns = getPatterns(ConstantsKeys.EN);
        }
        return patterns;
    }

    private JSONObject getPatternsFromDS(String locale) {
        JSONObject patterns = null;
        if (LocaleUtility.isDefaultLocale(locale)) {
            logger.debug("Look for pattern from local bundle for locale [{}]!", locale);
            patterns = new LocalPatternOpt().getEnPatterns(ConstantsKeys.EN);
        } else {
            if (VIPCfg.getInstance().getMsgOriginsQueue().get(0) == DataSourceEnum.VIP) {
                logger.debug("Look for pattern from Singleton Service for locale [{}]!", locale);
                patterns = new RemotePatternOpt().getPatternsByLocale(locale);
            } else {
                logger.debug("Look for pattern from local bundle for locale [{}]!", locale);
                String normalizedLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
                logger.debug("Normalized locale for locale [{}] is [{}]", locale, normalizedLocale);
                patterns = new LocalPatternOpt().getPatternsByLocale(normalizedLocale);
            }
        }
        return patterns;
    }

    private JSONObject getPatternsFromDS(String language, String region) {
        JSONObject patterns = null;
        Locale locale = new Locale(language, region);
        if (LocaleUtility.isDefaultLocale(locale)) {
            logger.debug("Look for pattern from local bundle for language [{}], region [{}]!", language, region);
            patterns = new LocalPatternOpt()
                    .getEnPatterns(ConstantsKeys.EN);
        } else {
            if (VIPCfg.getInstance().getMsgOriginsQueue().get(0) == DataSourceEnum.VIP) {
                logger.debug("Look for pattern from Singleton Service for language [{}], region [{}]!", language, region);
                patterns = new RemotePatternOpt().getPatternsByLocale(language, region);
            } else {
                logger.debug("Look for pattern from local bundle for language [{}], region [{}]!", language, region);
                LocaleDataDTO resultData = CommonUtil.getLocale(language, region);
                String localeStr = resultData.getLocale();
                logger.debug("Normalized locale for language [{}], region [{}] is [{}]", language, region, localeStr);
                patterns = new LocalPatternOpt().getPatternsByLocale(localeStr);
            }
        }
        return patterns;
    }
}
