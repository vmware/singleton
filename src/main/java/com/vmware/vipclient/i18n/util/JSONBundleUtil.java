/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import java.nio.file.Path;

import org.json.JSONObject;

public class JSONBundleUtil {
	
	@Deprecated
    private static final String JSON_MESSAGES = "l10n/bundles/{0}/{1}/{2}/messages_{3}.json";
    
    public static JSONObject getMessages(Path path) {
    	JSONObject obj = FileUtil.readJson(path);
    	if (obj == null) 
    		return null;
    	return (JSONObject) obj.get("messages");
    }
    
    @Deprecated
    public static JSONObject getMessages(String locale, String productName,
            String version, String component) {
        JSONObject jsonMsgs = null;
        JSONObject obj = JSONBundleUtil.readJSONFile(productName, version,
                component, locale);
        if (obj != null) {
            Object messages = obj.get("messages");
            if (messages != null) {
                jsonMsgs = (JSONObject) messages;
            }
        }
        return jsonMsgs;
    }
    
    @Deprecated
    private static JSONObject readJSONFile(String productName, String version,
            String component, String locale) {
        JSONObject jsonObj = null;
        String basePath = JSONBundleUtil.class.getProtectionDomain()
                .getCodeSource().getLocation().getPath();
        String l10nPath = FormatUtils.format(JSON_MESSAGES, productName, version,
                component, locale);
        if (basePath.lastIndexOf(".jar") > 0) {
            jsonObj = FileUtil.readJarJsonFile(basePath, l10nPath);
        } else {
            jsonObj = FileUtil.readLocalJsonFile(l10nPath);
        }
        return jsonObj;
    }
}
