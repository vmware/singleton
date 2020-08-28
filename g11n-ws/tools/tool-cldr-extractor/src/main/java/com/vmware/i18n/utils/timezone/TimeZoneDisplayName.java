/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils.timezone;

import java.io.Serializable;

public class TimeZoneDisplayName implements Serializable {
 
    private static final long serialVersionUID = 7629330919740825568L;
    public String getLongStandard() {
        return longStandard;
    }
    public void setLongStandard(String longStandard) {
        this.longStandard = longStandard;
    }
    public String getLongDaylight() {
        return longDaylight;
    }
    public void setLongDaylight(String longDaylight) {
        this.longDaylight = longDaylight;
    }
    public String getLongGeneric() {
        return longGeneric;
    }
    public void setLongGeneric(String longGeneric) {
        this.longGeneric = longGeneric;
    }
    public String getShortStandard() {
        return shortStandard;
    }
    public void setShortStandard(String shortStandard) {
        this.shortStandard = shortStandard;
    }
    public String getShortDaylight() {
        return shortDaylight;
    }
    public void setShortDaylight(String shortDaylight) {
        this.shortDaylight = shortDaylight;
    }
    public String getShortGeneric() {
        return shortGeneric;
    }
    public void setShortGeneric(String shortGeneric) {
        this.shortGeneric = shortGeneric;
    }
    private String longStandard;
    private String longDaylight;
    private String longGeneric;
    private String shortStandard;
    private String shortDaylight;
    private String shortGeneric;

}
