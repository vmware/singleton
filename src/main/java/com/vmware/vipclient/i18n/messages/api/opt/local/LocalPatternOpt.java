/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import com.vmware.i18n.PatternUtil;
import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Map;

public class LocalPatternOpt {

    public JSONObject getPatternsByLocale(String locale) {
        locale = locale.replace("_", "-");
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
