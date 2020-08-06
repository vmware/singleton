/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.i18n.PatternUtil;
import com.vmware.i18n.dto.LocaleDataDTO;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.messages.api.opt.PatternOpt;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localeAliasesMap;
import static com.vmware.i18n.pattern.service.impl.PatternServiceImpl.localePathMap;

public class LocalPatternOpt implements PatternOpt{
    Logger logger = LoggerFactory.getLogger(LocalPatternOpt.class);

    public JSONObject getPatterns(String locale) {
        logger.debug("Look for pattern from local bundle for locale [{}]!", locale);
        String normalizedLocale = CommonUtil.getCLDRLocale(locale, localePathMap, localeAliasesMap);
        logger.debug("Normalized locale for locale [{}] is [{}]", locale, normalizedLocale);
        return getPatternsByLocale(normalizedLocale);
    }

    @Override
    public JSONObject getPatterns(String language, String region) {
        logger.debug("Look for pattern from local bundle for language [{}], region [{}]!", language, region);
        LocaleDataDTO resultData = CommonUtil.getLocale(language, region);
        String normalizedLocale = resultData.getLocale();
        logger.debug("Normalized locale for language [{}], region [{}] is [{}]", language, region, normalizedLocale);
        return getPatternsByLocale(normalizedLocale);
    }

    private JSONObject getPatternsByLocale(String normalizedLocale) {
        if(normalizedLocale == null || normalizedLocale.isEmpty())
            return null;
        try {
            Map<String, Object> patterns = null;
            String patternStr = PatternUtil.getPatternFromLib(normalizedLocale, null);
            patterns = (Map<String, Object>) new JSONParser().parse(patternStr);
            return (JSONObject) patterns.get(PatternKeys.CATEGORIES);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return null;
    }
}
