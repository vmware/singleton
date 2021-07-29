package com.vmware.i18n.utils;

import com.vmware.i18n.PatternConfig;
import com.vmware.i18n.common.CLDRConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathUtils {
    private static Logger logger = LoggerFactory.getLogger(PathUtils.class);

    public static String getResourcePath() {
        String resourcePath = CLDRConstants.RESOURCES_PATH; //this is the resource path when debug source code in IDE
        if (PatternConfig.getInstance().getPatternPath() != null) {
            resourcePath = PatternConfig.getInstance().getPatternPath(); //this is the configed resource path when format bundles are unbinded with source code
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
