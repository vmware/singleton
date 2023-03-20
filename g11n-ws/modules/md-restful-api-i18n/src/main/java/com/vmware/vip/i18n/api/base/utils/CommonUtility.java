/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base.utils;

import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.utils.CategoriesEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonUtility {
  private CommonUtility() {}

    /**
     * Get categories by CategoriesEnum
     * @param scope
     * @param perfectMatch
     * If perfectMatchFlag = true and not found from the CategoriesEnum, return null
     * If perfectMatchFlag = false and not found from the CategoriesEnum，return the original value
     *
     * @return
     */
    public static List<String> getCategoriesByEnum(String scope, boolean perfectMatch){
        String[] categories = scope.split(ConstantsChar.COMMA);
        List<String> categoryList = new ArrayList<>();
        for (String cat : categories) {
            CategoriesEnum categoriesEnum = CategoriesEnum.getCategoriesEnumByText(cat);
            if (categoriesEnum == null) {
                if (perfectMatch) {
                    return null;
                }
                categoryList.add(cat);
            } else {
                categoryList.add(categoriesEnum.getText());
            }
        }
        return categoryList;
    }
}
