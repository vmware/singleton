/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.dto.LocaleDataDTO;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.messages.api.opt.PatternOpt;
import com.vmware.vipclient.i18n.base.cache.PatternCacheItem;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

public class LocalPatternOpt implements PatternOpt{
    Logger logger = LoggerFactory.getLogger(LocalPatternOpt.class);

    public void getPatterns(String locale, PatternCacheItem cacheItem) {
        logger.debug("Look for pattern from local bundle for locale [{}]!", locale);
        String normalizedLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
        logger.debug("Normalized locale for locale [{}] is [{}]", locale, normalizedLocale);
        getPatternsByLocale(normalizedLocale, cacheItem);
    }

    @Override
    public void getPatterns(String language, String region, PatternCacheItem cacheItem) {
        logger.debug("Look for pattern from local bundle for language [{}], region [{}]!", language, region);
        LocaleDataDTO resultData = CommonUtil.getLocale(language, region);
        String normalizedLocale = resultData.getLocale();
        logger.debug("Normalized locale for language [{}], region [{}] is [{}]", language, region, normalizedLocale);
        getPatternsByLocale(normalizedLocale, cacheItem);
    }

    private void getPatternsByLocale(String normalizedLocale, PatternCacheItem cacheItem) {
        if(normalizedLocale == null || normalizedLocale.isEmpty())
            return;
        try {
            String patternStr = PatternUtil.getPatternFromLib(normalizedLocale, null);
            JSONObject jsonObject = new JSONObject(patternStr);
            Map<String, Object> patterns = jsonObject.toMap();
            if(patterns != null && (patterns.get(PatternKeys.CATEGORIES) != null)) {
                logger.debug("Found the pattern from local bundle for locale [{}].\n", normalizedLocale);
                cacheItem.set((Map<String, Object>) patterns.get(PatternKeys.CATEGORIES), System.currentTimeMillis());
            }else{
                logger.warn("Didn't find the pattern from local bundle for locale [{}].\n", normalizedLocale);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
}
