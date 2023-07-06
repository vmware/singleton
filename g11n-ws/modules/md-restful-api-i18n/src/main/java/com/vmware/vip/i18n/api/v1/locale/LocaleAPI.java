/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.locale;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.i18n.l2.service.locale.LocaleDTO;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.i18n.api.base.BaseAction;
import com.vmware.vip.i18n.api.base.utils.LocaleUtility;



/**
 * Provide RESTful API to manipulate the locale.
 *
 */
@RestController
public class LocaleAPI extends BaseAction{

    /**
     * Pick first locale from browser's language setting
     *
     * @param request
     *        Extends the ServletRequest interface to provide request information for HTTP servlets.
     * @return APIResponseDTO 
     *         The object which represents response status.
     */
    @Operation(summary = APIOperation.LOCALE_PICKUP_VALUE, description = APIOperation.LOCALE_PICKUP_NOTES)
    @RequestMapping(value = APIV1.BROWSER_LOCALE, method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getBrowserLocale(HttpServletRequest request) {
        return super.handleResponse(APIResponseStatus.OK, this.getLocaleDTO(request.getLocale().toString(), request.getLocale().getDisplayName()));
    }

    /**
     * Pick first locale from browser's language setting and normalize it.
     *
     * @param request
     *        Extends the ServletRequest interface to provide request information for HTTP servlets.s
     * @return APIResponseDTO 
     *         The object which represents response status.
     */
    @Operation(summary = APIOperation.LOCALE_NORMALIZATION_VALUE, description = APIOperation.LOCALE_NORMALIZATION_NOTES)
    @RequestMapping(value = APIV1.NORM_BROWSER_LOCALE, method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getNormalizedBrowserLocale(HttpServletRequest request) {
        String locale = LocaleUtility.normalizeLocaleFromString(request.getLocale().toString()).toString();
        return super.handleResponse(APIResponseStatus.OK, this.getLocaleDTO(locale, request.getLocale().getDisplayName()));
    }

    private LocaleDTO getLocaleDTO(String locale, String displayName) {
        if(locale.indexOf("__#")>=0){
        	locale=locale.replace("__#", "_");
        }
        LocaleDTO localeDTO = new LocaleDTO();
        localeDTO.setLocale(locale);
        localeDTO.setDisplayName(displayName);
    	return localeDTO;
    }
}
