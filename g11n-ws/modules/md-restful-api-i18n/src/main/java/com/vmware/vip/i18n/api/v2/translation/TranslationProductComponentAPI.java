/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.translation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.i18n.api.base.TranslationProductComponentAction;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


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
    @ApiOperation(value = APIOperation.COMPONENT_TRANSLATION_VALUE, notes = APIOperation.COMPONENT_TRANSLATION_NOTES)    
    @RequestMapping(value = APIV2.COMPONENT_TRANSLATION_GET, method = RequestMethod.GET, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getSingleComponentTranslation(
            @ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
            @ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
            @ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
            @ApiParam(name = APIParamName.LOCALE, required = true, value = APIParamValue.LOCALE) @PathVariable(value = APIParamName.LOCALE) String locale,
            @ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
            @ApiParam(name = APIParamName.MT, value = APIParamValue.MT)
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
