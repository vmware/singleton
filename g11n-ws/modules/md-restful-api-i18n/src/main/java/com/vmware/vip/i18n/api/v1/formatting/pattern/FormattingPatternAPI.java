/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.formatting.pattern;

import com.vmware.i18n.l2.service.pattern.IPatternService;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.api.rest.*;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.i18n.api.base.BaseAction;
import com.vmware.vip.i18n.api.base.utils.CommonUtility;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @ApiOperation(value = APIOperation.FORMAT_PATTERN_GET_VALUE, notes = APIOperation.FORMAT_PATTERN_GET_NOTES)
    @RequestMapping(value = APIV1.PATTERN, method = RequestMethod.GET, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getI18nPattern(
            @ApiParam(name = APIParamName.LOCALE, required = true, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = true) String locale,
            @ApiParam(name = APIParamName.SCOPE, required = true, value = APIParamValue.SCOPE) @RequestParam(value = APIParamName.SCOPE, required = true) String scope,
            @ApiParam(name = APIParamName.SCOPE_FILTER, required = false, value = APIParamValue.SCOPE_FILTER) @RequestParam(value = APIParamName.SCOPE_FILTER, required = false) String scopeFilter
    ) throws Exception {
        List<String> categories = CommonUtility.getCategoriesByEnum(scope, true);
        if (CommonUtil.isEmpty(categories)) {
            return super.handleResponse(APIResponseStatus.BAD_REQUEST, "Parameter error");
        }
        Map<String, Object> patternMap = patternService.getPattern(locale, categories, scopeFilter);
        if (CommonUtil.isEmpty(patternMap.get(ConstantsKeys.CATEGORIES))) {
            return super.handleResponse(APIResponseStatus.INTERNAL_NO_RESOURCE_ERROR, "Pattern file not found or parse failed.");
        }

        return super.handleResponse(APIResponseStatus.OK, patternMap);
    }

}
