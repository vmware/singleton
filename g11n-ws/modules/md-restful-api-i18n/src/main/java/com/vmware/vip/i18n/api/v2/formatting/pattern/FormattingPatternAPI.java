/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.formatting.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.i18n.api.base.utils.CommonUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.vmware.i18n.l2.service.pattern.IPatternService;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.i18n.api.base.BaseAction;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController("v2-FormattingPatternAPI")
public class FormattingPatternAPI extends BaseAction {

    @Autowired
    IPatternService patternService;

    /**
     * Get i18n pattern by specific locale and scope
     */
    @ApiOperation(value = APIOperation.FORMAT_PATTERN_GET_VALUE, notes = APIOperation.FORMAT_PATTERN_GET_NOTES)
    @RequestMapping(value = APIV2.FORMAT_PATTERN_GET, method = RequestMethod.GET, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getI18nPattern(
            @ApiParam(name = APIParamName.LOCALE, required = true, value = APIParamValue.LOCALE) @PathVariable(value = APIParamName.LOCALE) String locale,
            @ApiParam(name = APIParamName.SCOPE, required = true, value = APIParamValue.SCOPE) @RequestParam(value = APIParamName.SCOPE, required = true) String scope
    ) throws Exception {
        List<String> categories = new ArrayList<>(Arrays.asList(scope.split(",")));
        if (!CommonUtility.checkParams(categories, locale)) {
            return super.handleResponse(APIResponseStatus.BAD_REQUEST, "Parameter error");
        }

        Map<String, Object> patternMap = patternService.getPattern(locale, categories);
        if (CommonUtil.isEmpty(patternMap.get(ConstantsKeys.CATEGORIES))) {
            return super.handleResponse(APIResponseStatus.INTERNAL_NO_RESOURCE_ERROR, "Pattern file not found or parse failed.");
        }

        return super.handleResponse(APIResponseStatus.OK, patternMap);
    }

    /**
     * Get i18n pattern with language, region and scope parameter.
     */
    @ApiOperation(value = APIOperation.FORMAT_PATTERN_VALUE, notes = APIOperation.FORMAT_PATTERN_NOTES)
    @GetMapping(value = APIV2.FORMAT_PATTERN_WITH_LANGUAGE, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getI18nPatternWithLanguageAndRegion(
            @ApiParam(name = APIParamName.LANGUAGE, required = true, value = APIParamValue.LANGUAGE) @RequestParam(value = APIParamName.LANGUAGE, required = true) String language,
            @ApiParam(name = APIParamName.REGION, required = true, value = APIParamValue.REGION) @RequestParam(value = APIParamName.REGION, required = true) String region,
            @ApiParam(name = APIParamName.SCOPE, required = true, value = APIParamValue.SCOPE) @RequestParam(value = APIParamName.SCOPE, required = true) String scope
    ) throws VIPCacheException {
        List<String> categories = new ArrayList<>(Arrays.asList(scope.split(",")));
        if (!CommonUtility.checkParams(categories, language, region)) {
            return super.handleResponse(APIResponseStatus.BAD_REQUEST, "Parameter error");
        }

        Map<String, Object> patternMap = patternService.getPatternWithLanguageAndRegion(language, region, categories);
        if (!(boolean)patternMap.get("isExistPattern")) {
            return super.handleResponse(APIResponseStatus.INTERNAL_NO_RESOURCE_ERROR, "'No mapping language found for region!");
        }
        patternMap.remove("isExistPattern");
        return super.handleResponse(APIResponseStatus.OK, patternMap);
    }
}
