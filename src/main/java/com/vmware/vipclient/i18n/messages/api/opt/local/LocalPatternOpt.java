/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.i18n.PatternUtil;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.util.PatternBundleUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

public class LocalPatternOpt {
    private static final String JSON_PATTERNS = "level2/pattern/{0}/pattern.json";

    public JSONObject getEnPatterns(String locale) {
        Map<String, Object> patterns = PatternBundleUtil.readJSONFile(JSON_PATTERNS, locale);
        if (patterns == null) {
            return null;
        } else {
            return (JSONObject) patterns.get(PatternKeys.CATEGORIES);
        }
    }

    public JSONObject getPatternsByLocale(String locale) {
        if(locale == null || "".equalsIgnoreCase(locale))
            return null;
        String patternStr = PatternUtil.getPatternFromLib(locale, null);
        Map<String, Object> patterns = null;
        try {
            patterns = (Map<String, Object>) new JSONParser().parse(patternStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (patterns == null) {
            return null;
        } else {
            return (JSONObject) patterns.get(PatternKeys.CATEGORIES);
        }
    }
}
