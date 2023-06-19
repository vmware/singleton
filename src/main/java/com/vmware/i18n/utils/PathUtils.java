/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils;

import com.vmware.i18n.PatternConfig;
import com.vmware.i18n.common.CLDRConstants;

public class PathUtils {

    public static String getResourcePath() {
        String resourcePath = CLDRConstants.RESOURCES_PATH; //this is the resource path when debug source code in IDE
        if (PatternConfig.getInstance().getPatternPath() != null) {
            resourcePath = PatternConfig.getInstance().getPatternPath(); //this is the configed resource path when format bundles are unbinded with source code
        }else if (CLDRConstants.JSON_PATH.lastIndexOf(".jar") > 0) {
            resourcePath = CLDRConstants.JSON_PATH; //this is the resource path when format bundles are binded with source code
        }
        return resourcePath;
    }

    public static String getCoreResourcePath(){
        String resourcePath = CLDRConstants.RESOURCES_PATH; //this is the resource path when debug source code in IDE
        if (CLDRConstants.JSON_PATH.lastIndexOf(".jar") > 0) {
            resourcePath = CLDRConstants.JSON_PATH; //this is the resource path when format bundles are binded with source code
        }
        return resourcePath;
    }
}
