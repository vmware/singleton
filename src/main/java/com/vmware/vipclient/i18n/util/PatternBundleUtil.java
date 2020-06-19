/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import org.json.simple.JSONObject;

public class PatternBundleUtil {

    private static final String JSON_MESSAGES = "level2/{0}/pattern.json";

    public static JSONObject readJSONFile(String locale) {
        JSONObject jsonObj = null;
        String basePath = PatternBundleUtil.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath();
        String filePath = FormatUtils.format(JSON_MESSAGES, locale);
        if (basePath.lastIndexOf(".jar") > 0) {
            jsonObj = FileUtil.readJarJsonFile(basePath, filePath);
        } else {
            jsonObj = FileUtil.readLocalJsonFile(filePath);
        }
        return jsonObj;
    }
}
