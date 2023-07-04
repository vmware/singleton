/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.i18n.api.base.TranslationSourceAction;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Provide RESTful API for product to get translation by String base.
 *
 */
@RestController
public class TranslationSourceAPI extends TranslationSourceAction {

	/**
	 * Provide translation based on String
	 *
	 */
	@ApiOperation(value = APIOperation.SOURCE_TRANSLATION_GET_VALUE, notes = APIOperation.SOURCE_TRANSLATION_POST_NOTES)
	@RequestMapping(value = APIV1.SOURCES_GET, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@Override
	public String getTranslationBySource(
			@PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@RequestParam(value = APIParamName.VERSION) String version,
			@PathVariable(APIParamName.COMPONENT) String component,
			@RequestParam(value = APIParamName.SOURCE, required = true) String source,
			@RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = false, defaultValue = "false") String collectSource,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request, HttpServletResponse response) {
		return super.getTranslationBySource(productName, component, version,
				locale, source, sourceFormat, collectSource, pseudo, request,
				response);
	}

	/**
	 * Create source with post data, especially it's used for creating long
	 * source
	 * 
	 */
	@ApiOperation(value = APIOperation.SOURCE_TRANSLATION_POST_VALUE, notes = APIOperation.SOURCE_TRANSLATION_POST_NOTES)
	@RequestMapping(value = APIV1.SOURCES_POST, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO postTranslationBySource(
			@PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@RequestParam(value = APIParamName.VERSION) String version,
			@PathVariable(APIParamName.COMPONENT) String component,
			@RequestBody String source,
			@RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = false, defaultValue = "false") String collectSource,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request) throws L3APIException {
		return super.createSource(productName, component, version, locale, source,
				sourceFormat, collectSource, pseudo, request);
	}
}