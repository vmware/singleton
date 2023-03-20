/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.common;

import com.vmware.i18n.utils.CommonUtil;

public enum OfficialStatusEnum {
    OFFICIAL(0, "official"),
    DEFACTOOFFICAL(1, "de_facto_official"),
    OTHER(2, "other");

    private Integer type;

    private String text;

    OfficialStatusEnum(Integer type, String text) {
        this.type = type;
        this.text = text;
    }

    public Integer getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public static OfficialStatusEnum getOfficialStatusEnumByText(String text){
        if (!CommonUtil.isEmpty(text)){
            for(OfficialStatusEnum officialStatusEnum : OfficialStatusEnum.values()){
                if(text.equals(officialStatusEnum.text)){
                    return officialStatusEnum;
                }
            }
        }
        return OTHER;
    }

}
