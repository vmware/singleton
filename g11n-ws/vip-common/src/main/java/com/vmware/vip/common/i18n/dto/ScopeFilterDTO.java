/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.exceptions.ValidationException;

import java.io.Serializable;
import java.util.*;

public class ScopeFilterDTO implements Serializable {

    private boolean reverse;
    private Map<String, List<List<String>>> filters = new HashMap<>();

    private ScopeFilterDTO(){}

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public Map<String, List<List<String>>> getFilters() {
        return filters;
    }

    public void addScopeFilterStr(String scopeFilterStr){
        String[] filterSplit = scopeFilterStr.split(ConstantsChar.UNDERLINE);
        List<String> filterStrList = new ArrayList<>(filterSplit.length-1);
        for(int idx =1; idx < filterSplit.length; idx++){
            filterStrList.add(filterSplit[idx]);
        }
        if (filters.containsKey(filterSplit[0])){
            filters.get(filterSplit[0]).add(filterStrList);
        }else {
            List<List<String>> filtersList = new ArrayList<>();
            filtersList.add(filterStrList);
            filters.put(filterSplit[0], filtersList);
        }
    }


    public static ScopeFilterDTO generateScopeFilterWithValidation(List<String> categories, String reqScopeFilter) throws ValidationException {
        if(reqScopeFilter == null || reqScopeFilter.isEmpty()){
            return null;
        }
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
            scopeFilterDTO.addScopeFilterStr(scopeFilterArr[index]);
        }
        for (String key : scopeFilterDTO.getFilters().keySet()) {
            if (!categories.contains(key)) {
                throw new ValidationException(ConstantsMsg.SCOPE_FILTER_NOT_VALIDATE);
            }
        }
        return scopeFilterDTO;
    }


}
