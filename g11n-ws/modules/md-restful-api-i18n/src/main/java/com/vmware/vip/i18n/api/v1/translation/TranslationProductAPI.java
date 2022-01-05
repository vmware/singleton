/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;

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
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.i18n.api.base.TranslationProductAction;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Provide API for product to get translation by component base.
 *
 */
@RestController
public class TranslationProductAPI   extends TranslationProductAction{

	/**
	 * get translation by product.
	 *
	 */
	@ApiOperation(value = APIOperation.PRODUCT_TRANSLATION_VALUE, notes = APIOperation.PRODUCT_TRANSLATION_NOTES)
	@RequestMapping(value = APIV1.TRANSLATION, method = RequestMethod.GET, produces = { API.API_CHARSET })
	public String getProductTranslation(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @RequestParam(value = APIParamName.PRODUCT_NAME, required = true) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@ApiParam(name = APIParamName.LOCALE, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request, HttpServletResponse response)  throws Exception {
		return super.getProductTrans(productName, version, locale, pseudo, request, response);
	}

	/**
	 * get supported locale-list by product name and version.
	 *
	 */
	@ApiOperation(value = APIOperation.PRODUCT_LOCALE_LIST_VALUE, notes = APIOperation.PRODUCT_LOCALE_LIST_NOTES)
	@RequestMapping(value = APIV1.SUPPORTED_LOCALES, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getSupportedLocales(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(APIParamName.VERSION) String version,
			HttpServletRequest request)  throws Exception {
		return super.getSLocales(productName, version, request);
	}

    /**
     * get component-list by product name and version.
     *
     */
    @ApiOperation(value = APIOperation.PRODUCT_COMPONENT_LIST_VALUE, notes = APIOperation.PRODUCT_COMPONENT_LIST_NOTES)
    @RequestMapping(value = APIV1.COMPONENTS, method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getComponentNameList(
            @ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @RequestParam String productName,
            @ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
            HttpServletRequest request)  throws Exception {
        return super.getCNameList(productName, version, request);
    }
}
