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
	@Operation(summary = APIOperation.PRODUCT_TRANSLATION_VALUE, description = APIOperation.PRODUCT_TRANSLATION_NOTES)
    @RequestMapping(value = APIV2.PRODUCT_TRANSLATION_GET, method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public void getMultipleComponentsTranslation(
			@Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
	    	@Parameter(name = APIParamName.COMPONENTS, required = false, description = APIParamValue.COMPONENTS) @RequestParam(value = APIParamName.COMPONENTS, required = false, defaultValue = "") String components,
			@Parameter(name = APIParamName.LOCALES, required = false, description = APIParamValue.LOCALES) @RequestParam(value = APIParamName.LOCALES, required = false, defaultValue = "") String locales,
			@Parameter(name = APIParamName.PSEUDO, description = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
            HttpServletResponse resp)  throws Exception {
        super.writeMultTranslationResponse(productName, version, components, locales, pseudo, resp);
    }

	/**
	 * Provide supported locales by product name and version.
	 *
	 */
	@Operation(summary = APIOperation.PRODUCT_LOCALE_LIST_VALUE, description = APIOperation.PRODUCT_LOCALE_LIST_NOTES)
	@RequestMapping(value = APIV2.PRODUCT_LOCALE_LIST_GET, method = RequestMethod.GET, produces = {API.API_CHARSET})
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getSupportedLocales(
			@Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @PathVariable(APIParamName.VERSION) String version,
			HttpServletRequest request) throws Exception{
		return super.getSLocales(productName, version, request);
	}

    /**
     * Provide component's names by product name and version.
     *
     */
    @Operation(summary = APIOperation.PRODUCT_COMPONENT_LIST_VALUE, description = APIOperation.PRODUCT_COMPONENT_LIST_NOTES)
    @RequestMapping(value = APIV2.PRODUCT_COMPONENT_LIST_GET, method = RequestMethod.GET, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getComponentNameList(
            @Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
            @Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
            HttpServletRequest request)  throws Exception {
        return super.getCNameList(productName, version, request);
    }

    /**
     * Provide component's names by product name and version.
     * @throws L3APIException 
     *
     */
    @Operation(summary = APIOperation.PRODUCT_VERSIONINFO_VALUE, description = APIOperation.PRODUCT_VERSIONINFO_NOTES)
    @RequestMapping(value = APIV2.PRODUCT_VERSIONINFO_GET, method = RequestMethod.GET, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getDropInfo(
            @Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
            @Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
            HttpServletRequest request) throws L3APIException {
        return super.getVersionInfo(productName, version);
    }

    /**
     * Provide version's names by product name.
     *
     */
    @Operation(summary = APIOperation.PRODUCT_VERSION_LIST_VALUE, description = APIOperation.PRODUCT_VERSION_LIST_NOTES)
    @RequestMapping(value = APIV2.PRODUCT_VERSION_LIST_GET, method = RequestMethod.GET, produces = {API.API_CHARSET})
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getVersionNameList(
            @Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
            HttpServletRequest request)  throws Exception {
        return super.getVersionList(productName);
    }
}
