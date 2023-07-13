/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;

import java.io.IOException;

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
import com.vmware.vip.api.rest.APIV1;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.i18n.api.base.TranslationProductComponentKeyAction;


/**
 * Provide RESTful API for product to get translation by String base.
 *
 */
@RestController
public class TranslationProductComponentKeyAPI extends
		TranslationProductComponentKeyAction {

	/**
	 * get translation by string
	 *
	 */
	@Operation(summary = APIOperation.KEY_TRANSLATION_GET_VALUE, description = APIOperation.KEY_TRANSLATION_GET_NOTES)
	@RequestMapping(value = APIV1.KEY2_GET, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getTranslationByGet(
			@Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@Parameter(name = APIParamName.COMPONENT, required = true, description = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
			@Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@Parameter(name = APIParamName.KEY, required = true, description = APIParamValue.KEY) @PathVariable(APIParamName.KEY) String key,
			@Parameter(name = APIParamName.SOURCE, description = APIParamValue.SOURCE) @RequestParam(value = APIParamName.SOURCE, required = false, defaultValue = "") String source,
			@Parameter(name = APIParamName.COMMENT_SOURCE, description = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@Parameter(name = APIParamName.LOCALE, description = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@Parameter(name = APIParamName.SOURCE_FORMAT, description = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@Parameter(name = APIParamName.COLLECT_SOURCE, description = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = false, defaultValue = "false") String collectSource,
			@Parameter(name = APIParamName.PSEUDO, description = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request) throws L3APIException {
		return super.getTransByGet(productName, version, locale, component, key, source, sourceFormat, pseudo);
	}

	/**
	 * Provide translation based on String use default component.
	 *
	 */
	@Operation(summary = APIOperation.KEY_TRANSLATION_GET_VALUE, description = APIOperation.KEY_TRANSLATION_GET_NOTES)
	@RequestMapping(value = APIV1.PRODUCT_KEY2, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getStringTranslationExclusiveComponent(
			@Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@Parameter(name = APIParamName.KEY, required = true, description = APIParamValue.KEY) @PathVariable(APIParamName.KEY) String key,
			@Parameter(name = APIParamName.SOURCE, required = false, description = APIParamValue.SOURCE) @RequestParam(value = APIParamName.SOURCE, required = false, defaultValue = "") String source,
			@Parameter(name = APIParamName.COMMENT_SOURCE, description = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@Parameter(name = APIParamName.LOCALE, description = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@Parameter(name = APIParamName.SOURCE_FORMAT, description = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@Parameter(name = APIParamName.COLLECT_SOURCE, description = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = false, defaultValue = ConstantsKeys.FALSE) String collectSource,
			@Parameter(name = APIParamName.PSEUDO, description = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = ConstantsKeys.FALSE) String pseudo,
			HttpServletRequest request) throws L3APIException {
		return super.getStringBasedTranslation(productName, version, ConstantsFile.DEFAULT_COMPONENT, locale, key, source, pseudo, ConstantsKeys.FALSE, sourceFormat, ConstantsKeys.FALSE);
	}


	/**
	 * Create source with post data, especially it's used for creating long
	 * source
	 * 
	 */
	@Operation(summary = APIOperation.SOURCE_TRANSLATION_POST_VALUE, description = APIOperation.SOURCE_TRANSLATION_POST_NOTES)
	@RequestMapping(value = APIV1.KEY2_POST, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getTranslationByPost(
			@PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@PathVariable(APIParamName.COMPONENT) String component,
			@PathVariable(APIParamName.KEY) String key,
			@Parameter(name = APIParamName.SOURCE, description = APIParamValue.SOURCE, required = false) String source,
			@Parameter(name = APIParamName.COMMENT_SOURCE, description = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@Parameter(name = APIParamName.LOCALE, description = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@Parameter(name = APIParamName.SOURCE_FORMAT, description = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@Parameter(name = APIParamName.COLLECT_SOURCE, description = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = false, defaultValue = "false") String collectSource,
			@Parameter(name = APIParamName.PSEUDO, description = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request, HttpServletResponse response)
			throws L3APIException, IOException {
		return super.getTransByPost(productName, version, locale, component, key, source, commentForSource, sourceFormat, collectSource, pseudo, "false", "false", request, response);
	}
}
