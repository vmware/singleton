/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base.utils;

import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.core.messages.exception.L3APIException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
    
    
    /**
     * Get the the matched version from the version list by comparing to the input version
     *
     * @param productName
     * @param version
     * @param productsAndVersions 
     * @return a matched version, if there's no matched version then return input version
     */
    public static String getMatchedVersion(final String productName, final String version,  final Map<String, String[]> productsAndVersions) throws L3APIException{
        String mv = "";
        if(productsAndVersions != null) {
            String[] vList = productsAndVersions.get(productName);
            if(vList != null) {
                if(Arrays.asList(vList).contains(version)) {
                    return version;
                }
                for(String v : vList) {
                    if(compareVersion(v, version) == -1 && compareVersion(v, mv) == 1) {
                        mv = v;
                    }
                }
            }
        }
        return StringUtils.isEmpty(mv)? version : mv;
    }
    
    
    /**
     * Compare source version and target version
     *
     * @param source
     * @param target
     * @return 0, equal; -1 less than; 1 bigger than
     */
    private static int compareVersion(final String source, final String target) {
        if (StringUtils.equals(source, target)) {
            return 0;
        }
        if(!StringUtils.isEmpty(source) && StringUtils.isEmpty(target)) {
            return 1;
        }

        String f = filterVersion(source, target);

        String[] s = f.split("\\.");
        String[] t = target.split("\\.");
        int b = 0;
        if(s.length == t.length) {
            for(int i = 0; i < s.length; i++) {
                if(Integer.parseInt(s[i]) > Integer.parseInt(t[i])) {
                    b = 1;
                    break;
                } else if(Integer.parseInt(s[i]) < Integer.parseInt(t[i])) {
                    b = -1;
                    break;
                }
            }
        }

        if(b == 0) {
            if(source.length() < target.length()) {
                b = -1;
            } else if(source.length() > target.length()){
                b = 1;
            }
        }
        return b;
    }

    /**
     * Filter the version number, and append insufficient subversion or remove extra subversion
     *
     * @param originVersion
     * @param requestVersion
     * @return e.g  originVersion = 2.0, requestVersion = 1.0.0, will return 2.0.0;
     *         e.g  originVersion = 2.0.0, requestVersion = 1.0, will return 2.0
     */
    private static String filterVersion(final String originVersion, final String requestVersion) {
        String filteredVersion = originVersion;
        int o = originVersion.split("\\.").length;
        int r = requestVersion.split("\\.").length;
        if(o < r) {
            for(int i=0; i < (r - o); i++) {
                filteredVersion = new StringBuilder(filteredVersion).append(".0").toString();
            }
        } else if (o > r) {
            for(int i=0; i < (o - r); i++) {
                filteredVersion = filteredVersion.substring(0, filteredVersion.lastIndexOf('.'));
            }
        }
        return filteredVersion;
    }
}
