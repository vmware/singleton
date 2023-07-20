/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.translation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.i18n.api.base.TranslationProductComponentAction;



/**
 * Provide RESTful API for product to get translation by component base.
 *
 */
@RestController("v2-TranslationComponentAPI")
public class TranslationProductComponentAPI extends TranslationProductComponentAction {

	
    /**
     * Provide translation based on single component.
     * 
     */
    @Operation(summary = APIOperation.COMPONENT_TRANSLATION_VALUE, description = APIOperation.COMPONENT_TRANSLATION_NOTES)
    @RequestMapping(value = APIV2.COMPONENT_TRANSLATION_GET, method = RequestMethod.GET, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getSingleComponentTranslation(
            @Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
            @Parameter(name = APIParamName.COMPONENT, required = true, description = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
            @Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
            @Parameter(name = APIParamName.LOCALE, required = true, description = APIParamValue.LOCALE) @PathVariable(value = APIParamName.LOCALE) String locale,
            @Parameter(name = APIParamName.PSEUDO, description = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
            @Parameter(name = APIParamName.MT, description = APIParamValue.MT)
            @RequestParam(value = APIParamName.MT, required=false, defaultValue="false") String machineTranslation,
     	//	@RequestHeader(required = true) String authorization,
            
            @RequestParam(value = APIParamName.CHECK_TRANS_STATUS, required=false, defaultValue="false") String checkTranslationStatus,
            HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
    	
    
		/*if(machineTranslation.equalsIgnoreCase("true")) {
			try {
				
				jwtService.verifyToken(authorization).get("username").asString();
			}catch(Exception e) {
				response.sendError(HttpStatus.UNAUTHORIZED.value(), "you token has expired or other authorization error!!!");
				return null;
			}
			
		}*/
    	
    	
    	
    	APIResponseDTO resp = getSingleComponentTrans(productName, component, version, locale, pseudo, machineTranslation, request);
    	if(checkTranslationStatus.equalsIgnoreCase("true")) {
    		return checkTranslationResult(productName, component, version, locale, resp);
    	}else {
    		return resp;
    		
    	}
    	
    }

    
  
}
