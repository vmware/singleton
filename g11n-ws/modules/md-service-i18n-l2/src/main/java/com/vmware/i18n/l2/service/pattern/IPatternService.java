/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.pattern;

import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.i18n.dto.ScopeFilterDTO;
import com.vmware.vip.core.messages.exception.L2APIException;

import java.util.List;
import java.util.Map;

public interface IPatternService {

    /**
     * Get i18n pattern data according to the locale and categoryList value
     * @param locale
     * @param categoryList dates,numbers,plurals,measurements,currencies, split by ','
     * @param scopeFilter a String for filtering out the pattern data, separated by commas and underline.
     * @return SingleComponentDTO Object
     */

    public Map<String, Object> getPattern(String locale, List<String> categoryList, String scopeFilter) throws L2APIException, VIPCacheException ;


    /**
     * Get i18n pattern data according to the language, region and categoryList value
     * @param language
     * @param region
     * @param categoryList dates,numbers,plurals,measurements,currencies, split by ','
     * @param scopeFilter a String for filtering out the pattern data, separated by commas and underline.
     * @return SingleComponentDTO Object
     */
    Map<String, Object> getPatternWithLanguageAndRegion(String language, String region, List<String> categoryList, String scopeFilter) throws L2APIException, VIPCacheException;
}
