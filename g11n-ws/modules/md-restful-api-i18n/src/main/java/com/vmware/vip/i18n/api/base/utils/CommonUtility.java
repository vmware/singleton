/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base.utils;

import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.common.constants.ConstantsKeys;

import java.util.Arrays;
import java.util.List;

public class CommonUtility {
  private CommonUtility() {}
    /**
     * Check param
     * @param categories
     * @return
     */
    public static boolean checkParams(String[] categories, String... args){
        List<String> catList = Arrays.asList(ConstantsKeys.ALL_CATEGORY);
        for (String cat : categories) {
            if (!catList.contains(cat)) {
                return false;
            }
        }

        for (String param : args){
            if (CommonUtil.isEmpty(param)){
                return false;
            }
        }

        return true;
    }
}