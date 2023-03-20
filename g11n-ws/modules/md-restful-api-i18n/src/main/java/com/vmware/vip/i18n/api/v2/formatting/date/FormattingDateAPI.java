/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.formatting.date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.i18n.l2.service.date.DateDTO;
import com.vmware.i18n.l2.service.date.IDateFormatService;
import com.vmware.i18n.utils.timezone.TimeZoneName;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.messages.exception.L2APIException;
import com.vmware.vip.i18n.api.base.BaseAction;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Provide RESTful API to manipulate the date by specific locale and pattern.
 *
 */
@RestController("v2-FormattingDateAPI")
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
    @ApiOperation(value = APIOperation.FORMAT_DATE_GET_VALUE, notes = APIOperation.FORMAT_DATE_GET_NOTES)
 /*   @ApiImplicitParams({
        @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "string", paramType = "header"),  
        @ApiImplicitParam(name = "sessionid", value = "sessionid", required = true, dataType = "string", paramType = "header")
    })*/
    @RequestMapping(value = APIV2.LOCALIZED_DATE, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
    public APIResponseDTO formatDate(
            @ApiParam(name = APIParamName.LOCALE, required = true, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = true) String locale,
            @ApiParam(name = APIParamName.LONGDATE, required = true, value = APIParamValue.LONGDATE) @RequestParam(value = APIParamName.LONGDATE, required = true) String longDate,
            @ApiParam(name = APIParamName.PATTERN, required = true, value = APIParamValue.PATTERN) @RequestParam(value = APIParamName.PATTERN, required = true) String pattern,
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
    
    @RequestMapping(value = APIV2.LOCALIZED_TIMEZONE_NAME, method = RequestMethod.GET, produces = { API.API_CHARSET })
   	@ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getDisplayTimezoneNameList(
            @ApiParam(name = APIParamName.DISPLAY_LANGUAGE, required = true, value = APIParamValue.DISPLAY_LANGUAGE) @RequestParam(value = APIParamName.DISPLAY_LANGUAGE, required = true) String displayLanguage,
            @ApiParam(name = APIParamName.DEFAULT_TERRITORY, required = false, value = APIParamValue.DEFAULT_TERRITORY) @RequestParam(value = APIParamName.DEFAULT_TERRITORY, required = false, defaultValue = "true") String defaultTerritory
    ) throws L2APIException{
    	TimeZoneName jsonObj = dateFormatService.getTimeZoneName(displayLanguage, Boolean.parseBoolean(defaultTerritory));
        return super.handleResponse(APIResponseStatus.OK, jsonObj);
    }
}
