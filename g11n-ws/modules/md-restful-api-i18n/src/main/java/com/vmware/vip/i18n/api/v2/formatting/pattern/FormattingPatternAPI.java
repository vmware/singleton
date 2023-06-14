/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.formatting.pattern;

import com.vmware.i18n.l2.service.pattern.IPatternService;
import com.vmware.i18n.utils.CommonUtil;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.utils.RegExpValidatorUtils;
import com.vmware.vip.i18n.api.base.BaseAction;
import com.vmware.vip.i18n.api.base.utils.CommonUtility;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
            @ApiParam(name = APIParamName.SCOPE, required = true, value = APIParamValue.SCOPE) @RequestParam(value = APIParamName.SCOPE, required = true) String scope,
            @ApiParam(name = APIParamName.SCOPE_FILTER, required = false, value = APIParamValue.SCOPE_FILTER) @RequestParam(value = APIParamName.SCOPE_FILTER, required = false) String scopeFilter
    ) throws Exception {
        List<String> categories = CommonUtility.getCategoriesByEnum(scope, true);
        if (CommonUtil.isEmpty(categories)) {
            return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(), ConstantsMsg.PATTERN_NOT_VALIDATE, null);
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

    /**
     * Get i18n pattern with language, region and scope parameter.
     */
    @ApiOperation(value = APIOperation.FORMAT_PATTERN_VALUE, notes = APIOperation.FORMAT_PATTERN_NOTES)
    @GetMapping(value = APIV2.FORMAT_PATTERN_WITH_LANGUAGE, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getI18nPatternWithLanguageAndRegion(
            @ApiParam(name = APIParamName.LANGUAGE, required = true, value = APIParamValue.LANGUAGE) @RequestParam(value = APIParamName.LANGUAGE, required = true) String language,
            @ApiParam(name = APIParamName.REGION, required = true, value = APIParamValue.REGION) @RequestParam(value = APIParamName.REGION, required = true) String region,
            @ApiParam(name = APIParamName.SCOPE, required = true, value = APIParamValue.SCOPE) @RequestParam(value = APIParamName.SCOPE, required = true) String scope,
            @ApiParam(name = APIParamName.SCOPE_FILTER, required = false, value = APIParamValue.SCOPE_FILTER) @RequestParam(value = APIParamName.SCOPE_FILTER, required = false) String scopeFilter
    ) throws VIPCacheException {
        if (!CommonUtil.isEmpty(scopeFilter) && !RegExpValidatorUtils.startLetterAndCommValidchar(scopeFilter)) {
            return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(), ConstantsMsg.SCOPE_FILTER_NOT_VALIDATE, null);
        }

        List<String> categories = CommonUtility.getCategoriesByEnum(scope, true);
        if (CommonUtil.isEmpty(categories)) {
        	return super.handleResponse(APIResponseStatus.BAD_REQUEST.getCode(), ConstantsMsg.PATTERN_NOT_VALIDATE, null);
        }

        Map<String, Object> patternMap = patternService.getPatternWithLanguageAndRegion(language, region, categories, scopeFilter);

        int emptyCount = getEmptyCategoryCount(patternMap, categories);
        if(emptyCount == 0) {
            return super.handleResponse(APIResponseStatus.OK, patternMap);
        }else if(emptyCount == categories.size()) {
            return super.handleResponse(APIResponseStatus.INTERNAL_NO_RESOURCE_ERROR.getCode(), ConstantsMsg.NO_PATTERN_FOUND, null);
        }else{
            return super.handleResponse(APIResponseStatus.MULTTRANSLATION_PART_CONTENT.getCode(), ConstantsMsg.PART_PATTERN_FOUND, patternMap);
        }
    }

    private int getEmptyCategoryCount(Map<String, Object> patternMap, List<String> categories){
        int count =0;
        Map<String, Object> categoriesMap = (Map<String, Object>) patternMap.get(ConstantsKeys.CATEGORIES);
        for(String category : categories){
            Object cateData = categoriesMap.get(category);
            if(cateData == null)
                count++;
        }
        return count;
    }

}
