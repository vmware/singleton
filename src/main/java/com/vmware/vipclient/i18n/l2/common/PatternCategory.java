/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.l2.common;

public enum PatternCategory {
    NUMBERS("numbers"),
    CURRENCIES("currencies"),
    DATES("dates"),
    PLURALS("plurals"),
    MEASUREMENTS("measurements"),
    SUPPLEMENTAL("supplemental");

    private final String value;

    private PatternCategory(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
