/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base.utils;

import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.i18n.dto.ScopeFilterDTO;
import com.vmware.vip.common.utils.CategoriesEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    public static ScopeFilterDTO generateScopeFilterWithValidation(List<String> categories, String reqScopeFilter) throws  RuntimeException{
        boolean reverse = false;
        String scopeFilterTrim = reqScopeFilter.trim();
        if (scopeFilterTrim.startsWith(ConstantsChar.REVERSE)) {
            scopeFilterTrim = scopeFilterTrim.substring(2, scopeFilterTrim.length()-1);
            reverse = true;
        }
        ScopeFilterDTO scopeFilterDTO = new ScopeFilterDTO();
        scopeFilterDTO.setReverse(reverse);
        String[] scopeFilterArr = scopeFilterTrim.split(ConstantsChar.COMMA);
        for (int index =0; index < scopeFilterArr.length; index++){
            scopeFilterDTO.addScopeFilter(scopeFilterArr[index]);
        }
        scopeFilterDTO.getFilters().keySet().forEach(key -> {
            if (!categories.contains(key)){
                throw new RuntimeException();
            }
        });
        return scopeFilterDTO;
    }
}
