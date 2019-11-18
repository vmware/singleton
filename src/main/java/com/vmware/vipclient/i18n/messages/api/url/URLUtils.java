/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.url;

import java.util.List;

/**
 * 
 * Encapsulates some methods related to vIP Server.
 *
 */
public class URLUtils {

    private URLUtils() {

    }

    public static String appendParamToURL(final StringBuilder u, String key,
            String value) {
        if (u.toString().indexOf('?') >= 0 && u.toString().indexOf('=') >= 0) {
            if ("".equalsIgnoreCase(key)) {
                u.append("&").append(value);
            } else {
                u.append("&").append(key).append("=").append(value);
            }
        } else {
            if ("".equalsIgnoreCase(key)) {
                u.append("?").append(value);
            } else {
                u.append("?").append(key).append("=").append(value);
            }
        }
        return u.toString();
    }

    /**
     * Is the target String in list
     * 
     * @param list
     * @param containStr
     * @return if contain return true, else return false.
     */
    public static boolean isStringInListIgnoreCase(List<String> list,
            String targetStr) {
        for (String str : list) {
            if (null != str && str.equalsIgnoreCase(targetStr)) {
                return true;
            }
        }
        return false;
    }
}
