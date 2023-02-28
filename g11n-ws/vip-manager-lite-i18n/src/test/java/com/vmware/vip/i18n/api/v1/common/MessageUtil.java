/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.common;

public class MessageUtil {

    public static String getSuccessString(String uri, String responseString) {
        return "request '" + uri + "' success, result :" + responseString;

    }

    public static String getFailureString(String uri, int status) {
        return "request '" + uri + "' failed, status code :" + status;
    }

}
