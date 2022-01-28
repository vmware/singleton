/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.locale;

import java.io.Serializable;

import com.vmware.vip.common.utils.LocaleUtils;

/**
 * Dto objects for locale data encapsulation
 */
public class LocaleDTO implements Serializable {

    private static final long serialVersionUID = -4708263251618377813L;

    private String displayName;

    private String locale;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = LocaleUtils.normalizeToLanguageTag(locale);
    }

}
