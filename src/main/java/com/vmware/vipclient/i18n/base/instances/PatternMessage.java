/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.instances;

import com.vmware.vipclient.i18n.messages.service.FormattingCacheService;
import com.vmware.vipclient.i18n.messages.service.PatternService;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * provide api to get pattern data from remote or locale
 */
public class PatternMessage implements Message {
    Logger logger = LoggerFactory.getLogger(PatternMessage.class);

    public PatternMessage() {
        super();
    }

    /**
     * get whole pattern data for formatting
     * 
     * @param locale
     * @return
     */
    public JSONObject getPatternMessage(Locale locale) {
        JSONObject patterns = null;
        PatternService ps = new PatternService();
        patterns = ps.getPatterns(locale.toLanguageTag());
        if (patterns != null) {
            return patterns;
        }
        if (!LocaleUtility.isDefaultLocale(locale)) {
            logger.info("Can't find pattern for locale [{}], look for English pattern as fallback!", locale);
            patterns = ps.getPatterns(LocaleUtility.getDefaultLocale().toLanguageTag());
            if (patterns != null) {
                new FormattingCacheService().addPatterns(locale.toLanguageTag(), patterns);
                logger.debug("Default locale's pattern is cached for locale [{}]!\n\n", locale);
                return patterns;
            }
        }
        return null;
    }

    /**
     * get whole pattern data for formatting
     * 
     * @param locale
     * @return
     */
    public JSONObject getPatternMessage(String language, String region) {
        JSONObject patterns = null;
        PatternService ps = new PatternService();
        patterns = ps.getPatterns(language, region);
        if (patterns != null) {
            return patterns;
        }
        if (!LocaleUtility.isDefaultLocale(new Locale(language, region))) {
            logger.info("Can't find pattern for language [{}] region [{}], look for English pattern as fallback!", language, region);
            patterns = ps.getPatterns(LocaleUtility.getDefaultLocale().toLanguageTag());
            if (patterns != null) {
                new FormattingCacheService().addPatterns(language, region, patterns);
                logger.debug("Default locale's pattern is cached for language [{}], region [{}]!\n\n", language, region);
                return patterns;
            }
        }
        return null;
    }
}
