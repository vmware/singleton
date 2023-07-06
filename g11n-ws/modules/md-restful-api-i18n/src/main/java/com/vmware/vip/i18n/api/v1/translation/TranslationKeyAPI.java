/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
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
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.StringBasedDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.core.messages.service.string.IStringService;
import com.vmware.vip.i18n.api.base.BaseAction;


/**
 * Provide API for product to get translation by String base.
 *
 */
@RestController
public class TranslationKeyAPI extends BaseAction {
	@Autowired
    IStringService stringBasedService;

	/**
	 * Provide translation based on String.
	 * 
	 */
    @Operation(summary = APIOperation.KEY_TRANSLATION_GET_VALUE, description = APIOperation.KEY_TRANSLATION_GET_NOTES)
	@RequestMapping(value = APIV1.TRANS_STRING, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getStringTranslation(
            @Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME)
            @RequestParam(value = APIParamName.PRODUCT_NAME, required = true) String productName,
            @Parameter(name = APIParamName.KEY, required = true, description = APIParamValue.KEY)
            @RequestParam(value = APIParamName.KEY, required = true) String key, 
            @Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION)
            @RequestParam(value = APIParamName.VERSION, required = true) String version,            
            @Parameter(name = APIParamName.COMPONENT, required = true, description = APIParamValue.COMPONENT)
            @RequestParam(value = APIParamName.COMPONENT, required = true) String component,
            @Parameter(name = APIParamName.LOCALE, description = APIParamValue.LOCALE)
            @RequestParam(value = APIParamName.LOCALE, required = false) String locale,         
            @Parameter(name = APIParamName.SOURCE, required = false, description = APIParamValue.SOURCE)
            @RequestParam(value = APIParamName.SOURCE, required = false) String source,
            @Parameter(name = APIParamName.COMMENT_SOURCE, description = APIParamValue.COMMENT_SOURCE)
            @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
            @Parameter(name = APIParamName.SOURCE_FORMAT, description = APIParamValue.SOURCE_FORMAT)
            @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
            @Parameter(name = APIParamName.COLLECT_SOURCE, description = APIParamValue.COLLECT_SOURCE)
            @RequestParam(value = APIParamName.COLLECT_SOURCE, required=false, defaultValue="false") String collectSource,
            @Parameter(name = APIParamName.PSEUDO, description = APIParamValue.PSEUDO)
            @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
            HttpServletRequest request)  throws Exception {
        ComponentMessagesDTO componentMessagesDTO = new ComponentMessagesDTO();
		componentMessagesDTO.setProductName(productName);
		componentMessagesDTO.setComponent(StringUtils.isEmpty(component) ? ConstantsFile.DEFAULT_COMPONENT : component);
		componentMessagesDTO.setVersion(version);
        componentMessagesDTO.setPseudo(Boolean.parseBoolean(pseudo));
		componentMessagesDTO.setLocale(locale == null ? ConstantsUnicode.EN : locale);
		StringBasedDTO stringBasedDTO = stringBasedService.getStringTranslation(
				componentMessagesDTO, StringUtils.isEmpty(sourceFormat) ? key : (key + ConstantsChar.DOT + ConstantsChar.POUND + sourceFormat.toUpperCase()), source);
		return super.handleResponse(APIResponseStatus.OK, stringBasedDTO);
	}
}
