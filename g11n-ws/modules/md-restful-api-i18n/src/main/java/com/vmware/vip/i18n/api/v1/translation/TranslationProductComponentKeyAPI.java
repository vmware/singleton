/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v1.translation;

import java.io.IOException;

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
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.i18n.api.base.TranslationProductComponentKeyAction;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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
	@ApiOperation(value = APIOperation.KEY_TRANSLATION_GET_VALUE, notes = APIOperation.KEY_TRANSLATION_GET_NOTES)
	@RequestMapping(value = APIV1.KEY2_GET, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getTranslationByGet(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@ApiParam(name = APIParamName.KEY, required = true, value = APIParamValue.KEY) @PathVariable(APIParamName.KEY) String key,
			@ApiParam(name = APIParamName.SOURCE, value = APIParamValue.SOURCE) @RequestParam(value = APIParamName.SOURCE, required = false, defaultValue = "") String source,
			@ApiParam(name = APIParamName.COMMENT_SOURCE, value = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@ApiParam(name = APIParamName.LOCALE, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@ApiParam(name = APIParamName.SOURCE_FORMAT, value = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = false, defaultValue = "false") String collectSource,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request) throws L3APIException {
		return super.getTransByGet(productName, version, locale, component, key, source, sourceFormat, pseudo);
	}

	/**
	 * Provide translation based on String use default component.
	 *
	 */
	@ApiOperation(value = APIOperation.KEY_TRANSLATION_GET_VALUE, notes = APIOperation.KEY_TRANSLATION_GET_NOTES)
	@RequestMapping(value = APIV1.PRODUCT_KEY2, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getStringTranslationExclusiveComponent(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@ApiParam(name = APIParamName.KEY, required = true, value = APIParamValue.KEY) @PathVariable(APIParamName.KEY) String key,
			@ApiParam(name = APIParamName.SOURCE, required = false, value = APIParamValue.SOURCE) @RequestParam(value = APIParamName.SOURCE, required = false, defaultValue = "") String source,
			@ApiParam(name = APIParamName.COMMENT_SOURCE, value = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@ApiParam(name = APIParamName.LOCALE, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@ApiParam(name = APIParamName.SOURCE_FORMAT, value = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = false, defaultValue = ConstantsKeys.FALSE) String collectSource,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = ConstantsKeys.FALSE) String pseudo,
			HttpServletRequest request) throws L3APIException {
		return super.getStringBasedTranslation(productName, version, ConstantsFile.DEFAULT_COMPONENT, locale, key, source, pseudo, ConstantsKeys.FALSE, sourceFormat, ConstantsKeys.FALSE);
	}


	/**
	 * Create source with post data, especially it's used for creating long
	 * source
	 * 
	 */
	@ApiOperation(value = APIOperation.SOURCE_TRANSLATION_POST_VALUE, notes = APIOperation.SOURCE_TRANSLATION_POST_NOTES)
	@RequestMapping(value = APIV1.KEY2_POST, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getTranslationByPost(
			@PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@PathVariable(APIParamName.COMPONENT) String component,
			@PathVariable(APIParamName.KEY) String key,
			@ApiParam(value = APIParamValue.SOURCE, required = false) String source,
			@ApiParam(name = APIParamName.COMMENT_SOURCE, value = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@ApiParam(name = APIParamName.LOCALE, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@ApiParam(name = APIParamName.SOURCE_FORMAT, value = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = false, defaultValue = "false") String collectSource,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request, HttpServletResponse response)
			throws L3APIException, IOException {
		return super.getTransByPost(productName, version, locale, component, key, source, commentForSource, sourceFormat, collectSource, pseudo, "false", "false", request, response);
	}
}
