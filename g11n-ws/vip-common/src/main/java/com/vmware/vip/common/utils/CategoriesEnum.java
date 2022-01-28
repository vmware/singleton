/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import org.apache.commons.lang3.StringUtils;


public enum CategoriesEnum {
    DATES(1, "dates"),
    NUMBERS(2, "numbers"),
    PLURALS(4, "plurals"),
    MEASUREMENTS(8, "measurements"),
    CURRENCIES(16, "currencies"),
    DATE_FIELDS(32, "dateFields");

    private Integer type;

    private String text;

    CategoriesEnum(Integer type, String text) {
        this.type = type;
        this.text = text;
    }

    public Integer getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public static CategoriesEnum getCategoriesEnumByText(String text){
        if (!StringUtils.isEmpty(text)){
            for(CategoriesEnum categoriesEnum : CategoriesEnum.values()){
                if(text.toUpperCase().equals(categoriesEnum.text.toUpperCase())){
                    return categoriesEnum;
                }
            }
        }
        return null;
    }

}
