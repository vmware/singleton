/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.util;

import java.text.FieldPosition;
import java.util.Locale;

import com.vmware.vipclient.i18n.l2.text.MessageFormat;

public class FormatUtils {

    private FormatUtils() {

    }

    /**
     * format a string with placeholder by the input arguments
     *
     * @param pattern
     *            a string contains placeholder
     * @param arguments
     *            used to format the pattern string
     * @return a formatted string
     */
    public static String format(String pattern, Object... arguments) {
        String escaped = pattern.replaceAll("'", "''");
        return java.text.MessageFormat.format(escaped, arguments);
    }

    public static String format(String pattern, Locale locale, Object... arguments) {
        MessageFormat messageFormat = new MessageFormat(pattern, locale);
        return messageFormat.format(arguments, new StringBuilder(), new FieldPosition(0)).toString();
    }
}
