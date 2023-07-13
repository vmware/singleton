/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.formatting.date;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.i18n.l2.service.date.DateDTO;
import com.vmware.i18n.l2.service.date.IDateFormatService;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.i18n.api.base.BaseAction;


/**
 * Provide RESTful API to manipulate the date by specific locale and pattern.
 *
 */
@RestController
public class FormattingDateAPI extends BaseAction{

    @Autowired
    IDateFormatService dateFormatService;

    /**
     * Get localized date by specific locale and pattern
     *
     * @param locale
     *        A string specified by the product to represent a specific locale, in [language]_[country (region)] format. e.g. ja_JP, zh_CN.
     * @param longDate
     *        The time stamp of java format. e.g. 1472728030290.
     * @param pattern
     *        The pattern specified by the product. e.g. [YEAR = "y",QUARTER = "QQQQ"], ABBR_QUARTER =
	 *        "QQQ",YEAR_QUARTER = "yQQQQ",YEAR_ABBR_QUARTER = "yQQQ" and so on. 
     * @param request
     *        Extends the ServletRequest interface to provide request information for HTTP servlets.
     * @return APIResponseDTO 
     *         The object which represents response status.
     */
    @Operation(summary = APIOperation.FORMAT_DATE_GET_VALUE, description = APIOperation.FORMAT_DATE_GET_NOTES)
 /*   @ApiImplicitParams({
        @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "string", paramType = "header"),  
        @ApiImplicitParam(name = "sessionid", value = "sessionid", required = true, dataType = "string", paramType = "header")
    })*/
    @RequestMapping(value = APIV1.LOCALIZED_DATE, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
    public APIResponseDTO formatDate(
            @Parameter(name = APIParamName.LOCALE, required = true, description = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = true) String locale,
            @Parameter(name = APIParamName.LONGDATE, required = true, description = APIParamValue.LONGDATE) @RequestParam(value = APIParamName.LONGDATE, required = true) String longDate,
            @Parameter(name = APIParamName.PATTERN, required = true, description = APIParamValue.PATTERN) @RequestParam(value = APIParamName.PATTERN, required = true) String pattern,
            HttpServletRequest request) throws Exception{
        DateDTO dateDTO = new DateDTO();
        String formattedDate = dateFormatService.formatDate(locale, Long.parseLong(longDate),
                pattern);
        dateDTO.setLongDate(longDate);
        dateDTO.setformattedDate(formattedDate);
        dateDTO.setLocale(locale);
        dateDTO.setPattern(pattern);
        return super.handleResponse(APIResponseStatus.OK, dateDTO);
    }
}
