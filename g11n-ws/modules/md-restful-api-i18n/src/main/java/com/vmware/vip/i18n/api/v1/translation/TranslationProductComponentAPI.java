/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;
import jakarta.servlet.http.HttpServletRequest;

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
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.multcomponent.TranslationDTO;
import com.vmware.vip.i18n.api.base.TranslationProductComponentAction;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Provide RESTful API for product to get translation by component base.
 *
 */
@RestController
public class TranslationProductComponentAPI extends TranslationProductComponentAction {
    
    /**
     * Provide translation based on single component.
     * 
     */
    @ApiOperation(value = APIOperation.COMPONENT_TRANSLATION_VALUE, notes = APIOperation.COMPONENT_TRANSLATION_NOTES)
    @RequestMapping(value = APIV1.COMPONENT, method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getSingleComponentTranslation(
            @ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
            @ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
            @ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
            @ApiParam(name = APIParamName.LOCALE, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
            @ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO)
            @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
            HttpServletRequest request)  throws Exception {
        return super.getSingleComponentTrans(productName, component, version, locale, pseudo, "false", request);
    }

    /**
     * Get translation based on multiple component.
     *
     */
    @ApiOperation(value = APIOperation.MULT_COMPONENT_TRANSLATION_NOTES, notes = APIOperation.MULT_COMPONENT_TRANSLATION_NOTES)
    @RequestMapping(value = APIV1.COMPONENTS2, method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getMultipleComponentsTrans(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.COMPONENTS, required = true, value = APIParamValue.COMPONENTS) @PathVariable(APIParamName.COMPONENTS) String components,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@ApiParam(name = APIParamName.LOCALES, required = true, value = APIParamValue.LOCALES, defaultValue = "") @RequestParam(value = APIParamName.LOCALES, required = true) String locales,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest req) throws Exception {
		APIResponseDTO resp = super.getMultipleComponentsTrans(productName, components, version, locales, pseudo, req);
		TranslationDTO translationDTO = (TranslationDTO) resp.getData();
		if (translationDTO.getBundles() == null || translationDTO.getBundles().size() == 0) {
			throw new L3APIException(
					String.format(ConstantsMsg.TRANS_GET_FAILD, productName + ConstantsChar.BACKSLASH + version));
		}
		return resp;
	}
}
