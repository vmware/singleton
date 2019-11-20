/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base.utils;

import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.common.utils.CategoriesEnum;

import java.util.ArrayList;
import java.util.List;

public class CommonUtility {
  private CommonUtility() {}
    /**
     * Check param
     * @param categories
     * @return
     */
    public static boolean checkParams(List<String> categories, String... args){
        List<String> newCategories = new ArrayList<>();
        for (String cat : categories) {
            CategoriesEnum categoriesEnum = CategoriesEnum.getCategoriesEnumByText(cat);
            if (categoriesEnum == null) {
                return false;
            }
            newCategories.add(categoriesEnum.getText());
        }
        categories.clear();
        categories.addAll(newCategories);
        for (String param : args){
            if (CommonUtil.isEmpty(param)){
                return false;
            }
        }

        return true;
    }
}
