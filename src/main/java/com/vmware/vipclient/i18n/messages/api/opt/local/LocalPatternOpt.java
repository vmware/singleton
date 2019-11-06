/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.local;

import java.util.Map;

import org.json.simple.JSONObject;

import com.vmware.vipclient.i18n.l2.common.PatternKeys;
import com.vmware.vipclient.i18n.util.PatternBundleUtil;

public class LocalPatternOpt {

    public JSONObject getPatternsByLocale(String locale) {
        Map<String, Object> patterns = PatternBundleUtil.readJSONFile(locale);
        if (patterns == null) {
            return null;
        } else {
            return (JSONObject) patterns.get(PatternKeys.CATEGORIES);
        }
    }
}
