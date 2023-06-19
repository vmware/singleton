/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.common;

public enum DayPeriodEnum {
    AM(0, "am"),
    PM(1, "pm"),
    AMALTVARIANT(2, "am-alt-variant"),
    PMALTVARIANT(3, "pm-alt-variant"),
    MIDNIGHT(4, "midnight"),
    NOON(5, "noon"),
    MORNING1(6, "morning1"),
    MORNING2(7, "morning2"),
    AFTERNOON1(8, "afternoon1"),
    AFTERNOON2(9, "afternoon2"),
    EVENING1(10, "evening1"),
    EVENING2(11, "evening2"),
    NIGHT1(12, "night1"),
    NIGHT2(13, "night2");

    private Integer index;
    private String dayPeriod;

    DayPeriodEnum(Integer index, String dayPeriod) {
        this.index = index;
        this.dayPeriod = dayPeriod;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getDayPeriod() {
        return dayPeriod;
    }

    public void setDayPeriod(String dayPeriod) {
        this.dayPeriod = dayPeriod;
    }
}
