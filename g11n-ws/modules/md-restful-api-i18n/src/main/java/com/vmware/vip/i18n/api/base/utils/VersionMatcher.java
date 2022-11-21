/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vmware.vip.core.messages.exception.L3APIException;

public class VersionMatcher {
    private VersionMatcher() {

    }

    /**
     * Get the the matched version from the version list by comparing to the input version
     *
     * @param productName
     * @param version
     * @param productsAndVersions
     * @return a matched version, if there's no matched version then return input version
     */
    public static String getMatchedVersion(final String version, final List<String> vList) {
            
            String mv = "";
            
            if (vList != null) {
                if (vList.contains(version)) {
                    return version;
                }
                for (String v : vList) {
                    if (compareVersion(v, version) == -1 && compareVersion(v, mv) == 1) {
                        mv = v;
                    }
                }
                if(StringUtils.isEmpty(mv) && vList.size() ==1) {
                	return vList.get(0);
                }     
            }
        
        return StringUtils.isEmpty(mv) ? version : mv;
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
        if (!StringUtils.isEmpty(source) && StringUtils.isEmpty(target)) {
            return 1;
        }

        String f = filterVersion(source, target);

        String[] s = f.split("\\.");
        String[] t = target.split("\\.");
        int b = 0;
        if (s.length == t.length) {
            for (int i = 0; i < s.length; i++) {
                if (Integer.parseInt(s[i]) > Integer.parseInt(t[i])) {
                    b = 1;
                    break;
                } else if (Integer.parseInt(s[i]) < Integer.parseInt(t[i])) {
                    b = -1;
                    break;
                }
            }
        }

        if (b == 0) {
            if (source.length() < target.length()) {
                b = -1;
            } else if (source.length() > target.length()) {
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
     * @return e.g originVersion = 2.0, requestVersion = 1.0.0, will return 2.0.0; e.g originVersion
     *         = 2.0.0, requestVersion = 1.0, will return 2.0
     */
    private static String filterVersion(final String originVersion, final String requestVersion) {
        String filteredVersion = originVersion;
        int o = originVersion.split("\\.").length;
        int r = requestVersion.split("\\.").length;
        if (o < r) {
            for (int i = 0; i < (r - o); i++) {
                filteredVersion = new StringBuilder(filteredVersion).append(".0").toString();
            }
        } else if (o > r) {
            for (int i = 0; i < (o - r); i++) {
                filteredVersion = filteredVersion.substring(0, filteredVersion.lastIndexOf('.'));
            }
        }
        return filteredVersion;
    }
}
