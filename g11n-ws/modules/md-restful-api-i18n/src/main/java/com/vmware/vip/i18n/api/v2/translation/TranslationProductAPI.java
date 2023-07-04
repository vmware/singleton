/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.translation;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.i18n.api.base.StreamProductAction;

/**
 * Provide API for product to get translation by component base.
 *
 */
@RestController("v2-TranslationProductAPI")
public class TranslationProductAPI  extends StreamProductAction {
	
    /**
     * Provide translation based on multiple component.
     *
     */
	@ApiOperation(value = APIOperation.PRODUCT_TRANSLATION_VALUE, notes = APIOperation.PRODUCT_TRANSLATION_NOTES)
    @RequestMapping(value = APIV2.PRODUCT_TRANSLATION_GET, method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public void getMultipleComponentsTranslation(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
	    	@ApiParam(name = APIParamName.COMPONENTS, required = false, value = APIParamValue.COMPONENTS) @RequestParam(value = APIParamName.COMPONENTS, required = false, defaultValue = "") String components,
			@ApiParam(name = APIParamName.LOCALES, required = false, value = APIParamValue.LOCALES) @RequestParam(value = APIParamName.LOCALES, required = false, defaultValue = "") String locales,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
            HttpServletResponse resp)  throws Exception {
        super.writeMultTranslationResponse(productName, version, components, locales, pseudo, resp);
    }

	/**
	 * Provide supported locales by product name and version.
	 *
	 */
	@ApiOperation(value = APIOperation.PRODUCT_LOCALE_LIST_VALUE, notes = APIOperation.PRODUCT_LOCALE_LIST_NOTES)
	@RequestMapping(value = APIV2.PRODUCT_LOCALE_LIST_GET, method = RequestMethod.GET, produces = {API.API_CHARSET})
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getSupportedLocales(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(APIParamName.VERSION) String version,
			HttpServletRequest request) throws Exception{
		return super.getSLocales(productName, version, request);
	}

    /**
     * Provide component's names by product name and version.
     *
     */
    @ApiOperation(value = APIOperation.PRODUCT_COMPONENT_LIST_VALUE, notes = APIOperation.PRODUCT_COMPONENT_LIST_NOTES)
    @RequestMapping(value = APIV2.PRODUCT_COMPONENT_LIST_GET, method = RequestMethod.GET, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getComponentNameList(
            @ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
            @ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
            HttpServletRequest request)  throws Exception {
        return super.getCNameList(productName, version, request);
    }

    /**
     * Provide component's names by product name and version.
     * @throws L3APIException 
     *
     */
    @ApiOperation(value = APIOperation.PRODUCT_VERSIONINFO_VALUE, notes = APIOperation.PRODUCT_VERSIONINFO_NOTES)
    @RequestMapping(value = APIV2.PRODUCT_VERSIONINFO_GET, method = RequestMethod.GET, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getDropInfo(
            @ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
            @ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
            HttpServletRequest request) throws L3APIException {
        return super.getVersionInfo(productName, version);
    }
}
