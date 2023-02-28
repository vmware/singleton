/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.dto;

public class LocaleDataDTO {
    private String locale;

    // Whether the LocaleID needs to be displayed
    private boolean displayLocaleID = true;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public boolean isDisplayLocaleID() {
        return displayLocaleID;
    }

    public void setDisplayLocaleID(boolean displayLocaleID) {
        this.displayLocaleID = displayLocaleID;
    }
}
