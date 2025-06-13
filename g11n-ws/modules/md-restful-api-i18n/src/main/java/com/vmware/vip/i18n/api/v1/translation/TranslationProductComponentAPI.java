/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = APIOperation.COMPONENT_TRANSLATION_VALUE, description = APIOperation.COMPONENT_TRANSLATION_NOTES)
    @RequestMapping(value = APIV1.COMPONENT, method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getSingleComponentTranslation(
            @Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
            @Parameter(name = APIParamName.COMPONENT, required = true, description = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
            @Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
            @Parameter(name = APIParamName.LOCALE, description = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
            @Parameter(name = APIParamName.PSEUDO, description = APIParamValue.PSEUDO)
            @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
            HttpServletRequest request)  throws Exception {
        return super.getSingleComponentTrans(productName, component, version, locale, pseudo, "false", request);
    }

    /**
     * Get translation based on multiple component.
     *
     */
    @Operation(summary = APIOperation.MULT_COMPONENT_TRANSLATION_NOTES, description = APIOperation.MULT_COMPONENT_TRANSLATION_NOTES)
    @RequestMapping(value = APIV1.COMPONENTS2, method = RequestMethod.GET, produces = { API.API_CHARSET })
    @ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getMultipleComponentsTrans(
			@Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@Parameter(name = APIParamName.COMPONENTS, required = true, description = APIParamValue.COMPONENTS) @PathVariable(APIParamName.COMPONENTS) String components,
			@Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@Parameter(name = APIParamName.LOCALES, required = true, description = APIParamValue.LOCALES) @RequestParam(value = APIParamName.LOCALES, required = true) String locales,
			@Parameter(name = APIParamName.PSEUDO, description = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest req) throws Exception {
		APIResponseDTO resp = super.getMultipleComponentsTrans(productName, components, version, locales, pseudo, req);
		TranslationDTO translationDTO = (TranslationDTO) resp.getData();
		if (translationDTO.getBundles() == null || translationDTO.getBundles().toList().size() == 0) {
			throw new L3APIException(
					String.format(ConstantsMsg.TRANS_GET_FAILD, productName + ConstantsChar.BACKSLASH + version));
		}
		return resp;
	}
}
