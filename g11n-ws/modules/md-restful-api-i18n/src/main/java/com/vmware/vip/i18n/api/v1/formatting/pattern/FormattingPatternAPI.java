/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.formatting.pattern;

import java.util.List;
import java.util.Map;

import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.utils.RegExpValidatorUtils;
import com.vmware.vip.i18n.api.base.utils.CommonUtility;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.i18n.l2.service.pattern.IPatternService;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.i18n.api.base.BaseAction;

@RestController
public class FormattingPatternAPI extends BaseAction {

    @Autowired
    IPatternService patternService;

    /**
     * Get i18n pattern by specific locale and scope
     *
     * @param locale A string specified by the product to represent a specific locale, in [language]_[country (region)] format. e.g. ja_JP, zh_CN.
     * @param scope  pattern category string. e.g. dates,numbers,plurals,measurements, split by ','
     * @return APIResponseDTO The object which represents response status.
     */
    @Operation(summary = APIOperation.FORMAT_PATTERN_GET_VALUE, description = APIOperation.FORMAT_PATTERN_GET_NOTES)
    @RequestMapping(value = APIV1.PATTERN, method = RequestMethod.GET, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getI18nPattern(
            @Parameter(name = APIParamName.LOCALE, required = true, description = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = true) String locale,
            @Parameter(name = APIParamName.SCOPE, required = true, description = APIParamValue.SCOPE) @RequestParam(value = APIParamName.SCOPE, required = true) String scope,
            @Parameter(name = APIParamName.SCOPE_FILTER, required = false, description = APIParamValue.SCOPE_FILTER) @RequestParam(value = APIParamName.SCOPE_FILTER, required = false) String scopeFilter
    ) throws Exception {
        List<String> categories = CommonUtility.getCategoriesByEnum(scope, true);
        if (CommonUtil.isEmpty(categories)) {
            return super.handleResponse(APIResponseStatus.BAD_REQUEST, "Parameter error");
        }

        if (!CommonUtil.isEmpty(scopeFilter) && !RegExpValidatorUtils.startLetterAndCommValidchar(scopeFilter)) {
            return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(), ConstantsMsg.SCOPE_FILTER_NOT_VALIDATE, null);
        }

        Map<String, Object> patternMap = patternService.getPattern(locale, categories, scopeFilter);
        if (CommonUtil.isEmpty(patternMap.get(ConstantsKeys.CATEGORIES))) {
            return super.handleResponse(APIResponseStatus.INTERNAL_NO_RESOURCE_ERROR, "Pattern file not found or parse failed.");
        }

        return super.handleResponse(APIResponseStatus.OK, patternMap);
    }

}
