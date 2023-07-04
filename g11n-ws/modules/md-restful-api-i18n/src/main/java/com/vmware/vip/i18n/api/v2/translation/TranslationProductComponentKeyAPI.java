/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.translation;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPAPIException;
import com.vmware.vip.common.i18n.dto.KeySourceCommentDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.i18n.api.base.TranslationProductComponentKeyAction;

import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Provide RESTful API for product to get translation by String base.
 *
 */
@RestController("v2-TranslationKeyAPI")
public class TranslationProductComponentKeyAPI extends TranslationProductComponentKeyAction {
	
	

	
	@Autowired(required = false)
	MeterRegistry meterRegistry;

	/**
	 * Provide translation based on String
	 *
	 */
	@ApiOperation(value = APIOperation.KEY_TRANSLATION_GET_VALUE, notes = APIOperation.KEY_TRANSLATION_GET_NOTES)
	@RequestMapping(value = APIV2.KEY_TRANSLATION_GET, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getTranslationByGet(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
			@ApiParam(name = APIParamName.LOCALE, required = true, value = APIParamValue.LOCALE) @PathVariable(value = APIParamName.LOCALE) String locale,
			@ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
			@ApiParam(name = APIParamName.KEY, required = true, value = APIParamValue.KEY) @PathVariable(APIParamName.KEY) String key,
			@ApiParam(name = APIParamName.SOURCE, value = APIParamValue.SOURCE) @RequestParam(value = APIParamName.SOURCE, required = false,  defaultValue = "") String source,
			@ApiParam(name = APIParamName.COMMENT_SOURCE, value = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@ApiParam(name = APIParamName.SOURCE_FORMAT, value = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = false, defaultValue = "false") String collectSource,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request) throws L3APIException {
		
		return super.getTransByGet(productName, version, locale, component, key, source, sourceFormat, pseudo);
	}

	/**
	 * Create source with post data, especially it's used for creating long
	 * source
	 * 
	 */
	@ApiOperation(value = APIOperation.KEY_TRANSLATION_POST_VALUE, notes = APIOperation.KEY_TRANSLATION_POST_NOTES)
	@RequestMapping(value = APIV2.KEY_TRANSLATION_POST, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getTranslationByPost(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
			@ApiParam(name = APIParamName.LOCALE, required = true, value = APIParamValue.LOCALE) @PathVariable(value = APIParamName.LOCALE) String locale,
			@ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
			@ApiParam(name = APIParamName.KEY, required = true, value = APIParamValue.KEY) @PathVariable(APIParamName.KEY) String key,
			@ApiParam(value = APIParamValue.SOURCE, required = false) String source,
			@ApiParam(name = APIParamName.COMMENT_SOURCE, value = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@ApiParam(name = APIParamName.SOURCE_FORMAT, value = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = false, defaultValue = "false") String collectSource,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
            @ApiParam(name = APIParamName.MT, value = APIParamValue.MT)
            @RequestParam(value = APIParamName.MT, required=false, defaultValue="false") String machineTranslation,
            @RequestParam(value = APIParamName.CHECK_TRANS_STATUS, required=false, defaultValue="false") String checkTranslationStatus,
         	//	@RequestHeader(required = true) String authorization,
            HttpServletRequest request, HttpServletResponse response)
			throws L3APIException,IOException {
		source = source != null ? source:"";
	    if(meterRegistry!= null) {
	    	meterRegistry.counter("vip.translation.key", APIParamName.KEY, key).increment();
	    }
		return super.getTransByPost(productName, version, locale, component, key, source, commentForSource, sourceFormat, collectSource, pseudo, machineTranslation, checkTranslationStatus, request, response);
	}

	/**
	 * API to post a bunch of strings
	 * @throws VIPAPIException 
	 *
	 */
	@ApiOperation(value = APIOperation.KEY_SET_POST_VALUE, notes = APIOperation.KEY_SET_POST_NOTES)
	@RequestMapping(value = APIV2.KEY_SET_POST, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO postSources(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
			@ApiParam(name = APIParamName.LOCALE, required = true, value = APIParamValue.LOCALE) @PathVariable(value = APIParamName.LOCALE) String locale,
			@ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
			@RequestBody List<KeySourceCommentDTO> sourceSet,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = false, defaultValue = "false") String collectSource,
			HttpServletRequest request) throws JsonProcessingException, VIPAPIException {
		request.setAttribute(ConstantsKeys.KEY, ConstantsKeys.JSON_KEYSET);
		ObjectMapper mapper = new ObjectMapper();
		String requestJson = mapper.writeValueAsString(sourceSet);
		if (!StringUtils.isEmpty(requestJson)) {
			validateSourceSetAndKey(sourceSet);
			request.setAttribute(ConstantsKeys.SOURCE, requestJson);
		}
		return super.handleResponse(APIResponseStatus.OK, "Recieved the sources and comments(please use translation-product-component-api to confirm it).");
	}
	
	/**
	 * API to get a bunch of strings
	 *
	 */
	@ApiOperation(value = APIOperation.KEY_SET_GET_VALUE, notes = APIOperation.KEY_SET_GET_NOTES)
	@RequestMapping(value = APIV2.KEY_SET_GET, method = RequestMethod.GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO getKeysTranslation(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
			@ApiParam(name = APIParamName.LOCALE, required = true, value = APIParamValue.LOCALE) @PathVariable(value = APIParamName.LOCALE) String locale,
			@ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
			@ApiParam(name = APIParamName.KEYS, required = true, value = APIParamValue.KEYS) @RequestParam(value = APIParamName.KEYS) String keys,
			@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request) throws L3APIException {
		
		return super.getMultTransByGet(productName, version, locale, component, keys, pseudo);

	}
	
}
