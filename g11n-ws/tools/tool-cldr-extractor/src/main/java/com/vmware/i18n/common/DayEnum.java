/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.common;

public enum DayEnum {
    SUN(0, "sun"),
    MON(1, "mon"),
    TUE(2, "tue"),
    WEN(3, "wen"),
    THU(4, "thu"),
    FRI(5, "fri"),
    SAT(6, "sat");

    private Integer index;
    private String day;

    DayEnum(Integer index, String day){
        this.index = index;
        this.day = day;
    }

    public Integer getIndex() {
        return index;
    }

    public String getDay() {
        return day;
    }

    public static Integer getIndexByDay(String day){
        if(day == null || day.isEmpty())
            return null;
        for(DayEnum dayEnum : DayEnum.values()){
            if(dayEnum.getDay().equals(day)){
                return dayEnum.getIndex();
            }
        }
        return null;
    }
}
