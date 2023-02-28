/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;


import javax.servlet.http.HttpServletRequest;

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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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
    @ApiOperation(value = APIOperation.KEY_TRANSLATION_GET_VALUE, notes = APIOperation.KEY_TRANSLATION_GET_NOTES)
	@RequestMapping(value = APIV1.TRANS_STRING, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
    public APIResponseDTO getStringTranslation(
            @ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME)
            @RequestParam(value = APIParamName.PRODUCT_NAME, required = true) String productName,
            @ApiParam(name = APIParamName.KEY, required = true, value = APIParamValue.KEY)
            @RequestParam(value = APIParamName.KEY, required = true) String key, 
            @ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION)
            @RequestParam(value = APIParamName.VERSION, required = true) String version,            
            @ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT)
            @RequestParam(value = APIParamName.COMPONENT, required = true) String component,
            @ApiParam(name = APIParamName.LOCALE, value = APIParamValue.LOCALE)
            @RequestParam(value = APIParamName.LOCALE, required = false) String locale,         
            @ApiParam(name = APIParamName.SOURCE, required = false, value = APIParamValue.SOURCE)
            @RequestParam(value = APIParamName.SOURCE, required = false) String source,
            @ApiParam(name = APIParamName.COMMENT_SOURCE, value = APIParamValue.COMMENT_SOURCE)
            @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
            @ApiParam(name = APIParamName.SOURCE_FORMAT, value = APIParamValue.SOURCE_FORMAT)
            @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
            @ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE)
            @RequestParam(value = APIParamName.COLLECT_SOURCE, required=false, defaultValue="false") String collectSource,
            @ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO)
            @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
            HttpServletRequest request)  throws Exception {
        ComponentMessagesDTO componentMessagesDTO = new ComponentMessagesDTO();
		componentMessagesDTO.setProductName(productName);
		componentMessagesDTO.setComponent(StringUtils.isEmpty(component) ? ConstantsFile.DEFAULT_COMPONENT : component);
		componentMessagesDTO.setVersion(version);
        componentMessagesDTO.setPseudo(new Boolean(pseudo));
		componentMessagesDTO.setLocale(locale == null ? ConstantsUnicode.EN : locale);
		StringBasedDTO stringBasedDTO = stringBasedService.getStringTranslation(
				componentMessagesDTO, StringUtils.isEmpty(sourceFormat) ? key : (key + ConstantsChar.DOT + ConstantsChar.POUND + sourceFormat.toUpperCase()), source);
		return super.handleResponse(APIResponseStatus.OK, stringBasedDTO);
	}
}
