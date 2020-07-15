/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import org.json.simple.JSONObject;

public class PatternBundleUtil {

    public static JSONObject readJSONFile(String filePath, String locale) {
        JSONObject jsonObj = null;
        String basePath = PatternBundleUtil.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath();
        filePath = FormatUtils.format(filePath, locale);
        if (basePath.lastIndexOf(".jar") > 0) {
            jsonObj = FileUtil.readJarJsonFile(basePath, filePath);
        } else {
            jsonObj = FileUtil.readLocalJsonFile(filePath);
        }
        return jsonObj;
    }
}
