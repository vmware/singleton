/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base.utils;

import java.util.Locale;

public class LocaleUtility {

    /**
     * Normalize the locale from string.
     * e.g. zh_HANS_CN -> zh_CN, zh_HANT_TW -> zh_TW
     *
     * @param locale
     *        the locale name for transformation.
     * @return
     */
    public static Locale normalizeLocaleFromString(String locale) {
        String parts[] = locale.split("_");
        if (parts.length == 1)
            return new Locale(parts[0]);
        else if (parts.length == 2) {
            if (parts[1].equalsIgnoreCase("HANS")) {
                return new Locale(parts[0], "CN");
            } else if (parts[1].equalsIgnoreCase("HANT")) {
                return new Locale(parts[0], "TW");
            } else {
                return new Locale(parts[0], parts[1]);
            }
        } else if (parts.length == 3 && parts[1].equalsIgnoreCase("HANS")) {
            return new Locale(parts[0], parts[2]);
        } else if (parts.length == 3 && parts[1].equalsIgnoreCase("HANT")) {
            return new Locale(parts[0], parts[2]);
        } else
            return new Locale(parts[0], parts[1], parts[2]);
    }
}
