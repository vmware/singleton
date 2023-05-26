/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import com.vmware.vip.common.constants.ConstantsChar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScopeFilterDTO implements Serializable {

    private boolean reverse;
    private Map<String, List<String>> filters = new HashMap<>();

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public Map<String, List<String>> getFilters() {
        return filters;
    }

    public void addScopeFilter(String scopeFilterStr){
        String[] filterSplit = scopeFilterStr.split(ConstantsChar.UNDERLINE);
        if (filters.containsKey(filterSplit[0])){
            filters.get(filterSplit[0]).add(filterSplit[1]);
        }else {
            List<String> list = new ArrayList<>();
            list.add(filterSplit[1]);
            filters.put(filterSplit[0], list);
        }
    }
}
