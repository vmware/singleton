/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.messages.api.opt.local.LocalPatternOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.RemotePatternOpt;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class represents date formatting
 */
public class PatternService {
    Logger logger = LoggerFactory.getLogger(PatternService.class);

    public JSONObject getPatternsByCategory(String locale, String category) {
        JSONObject patterns = getPatterns(locale);
        return (JSONObject) patterns.get(category);
    }

    public JSONObject getPatterns(String locale) {
        JSONObject patterns = null;
        logger.debug("Look for pattern from cache!");
        patterns = new PatternCacheService().lookForPatternsFromCache(locale);// key
        if (patterns == null) {
            patterns = getPatternsFromBundle(locale);
            if ((patterns == null) && !LocaleUtility.isDefaultLocale(locale)) {
                patterns = getPatternsFromBundle(ConstantsKeys.EN);
            }
            if (null != patterns) {
                logger.info("Got the pattern  with   locale [{}].\n", locale);// [datetime] and
                logger.info("Cache pattern!\n\n");
                new PatternCacheService().addPatterns(locale, patterns);
            }
        }
        return patterns;
    }

    public JSONObject getPatterns(String language, String region) {
        JSONObject patterns = null;
        logger.debug("Look for pattern from cache!");
        String key = language + "_" + region;
        patterns = new PatternCacheService().lookForPatternsFromCache(key);// key
        if (patterns == null) {
            patterns = getPatternsFromBundle(language, region);
            if (null != patterns) {
                logger.info("Got the pattern  with   language [{}] region [{}].\n", language, region);// [datetime]
                // and
                logger.info("Cache pattern!\n\n");
                new PatternCacheService().addPatterns(key, patterns);
            }
        }
        return patterns;
    }

    private JSONObject getPatternsFromBundle(String locale) {
        JSONObject patterns = null;
        if (LocaleUtility.isDefaultLocale(locale)) {
            logger.debug("Got pattern from local bundle!");
            patterns = new LocalPatternOpt()
                    .getPatternsByLocale(ConstantsKeys.EN);
        } else {
            if (VIPCfg.getInstance().getMsgOriginsQueue().get(0) == DataSourceEnum.VIP) {
                patterns = new RemotePatternOpt().getPatternsByLocale(locale);
            } else {
                patterns = new LocalPatternOpt().getPatternsByLocale(locale);
            }
        }
        return patterns;
    }

    private JSONObject getPatternsFromBundle(String language, String region) {
        JSONObject patterns = null;
        if (VIPCfg.getInstance().getMsgOriginsQueue().get(0) == DataSourceEnum.VIP) {
            patterns = new RemotePatternOpt().getPatternsByLocale(language, region);
        } else {
            patterns = new LocalPatternOpt().getPatternsByLocale(ConstantsKeys.EN);
        }

        return patterns;
    }
}
