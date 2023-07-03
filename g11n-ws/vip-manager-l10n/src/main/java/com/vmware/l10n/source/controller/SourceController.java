/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.controller;

import java.io.IOException;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;

import com.vmware.vip.common.utils.SourceFormatUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.l10n.source.dto.SourceAPIResponseDTO;
import com.vmware.l10n.source.service.SourceService;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.KeySourceCommentDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;


/**
 * This class is the controller for collecting dynamic english strings in
 * product code.
 */
@Hidden
@RestController
public class SourceController {
	private static Logger LOGGER = LoggerFactory.getLogger(SourceController.class);
	@Autowired
	SourceService sourceService;

	/**
	 * [get] receive source and cache it
	 * 
	 * @param productName
	 *            Product name
	 * @param component
	 *            Component name of product
	 * @param key
	 *            The unique identify for source in component's resource file
	 * @param version
	 *            Product version
	 * @param source
	 *            The english string which need translate
	 * @param commentForSource
	 *            The comment for source
	 * @param req
	 * @return SourceAPIResponseDTO The object which represents response status
	 */
	@CrossOrigin
	@RequestMapping(value = L10NAPIV1.CREATE_SOURCE_GET, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO addStringForTranslation(
			@PathVariable(value = APIParamName.PRODUCT_NAME) String productName,
			@PathVariable(value = APIParamName.COMPONENT) String component,
			@PathVariable(value = APIParamName.KEY) String key,
			@RequestParam(value = APIParamName.VERSION, required = true) String version,
			@RequestParam(value = APIParamName.SOURCE, required = false) String source,
			@RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			HttpServletRequest req) throws Exception{
		source = source == null ? "" : source;
		LOGGER.info("The request url is: {}",  req.getRequestURL());
		LOGGER.info("The parameters are: version=" + version + ", key=" + key + ", source=" + source
				+ ", commentForSource=" + commentForSource);
		StringSourceDTO stringSourceDTO = createSourceDTO(productName, version, component, key, source, commentForSource,  sourceFormat);

		boolean isSourceCached = sourceService.cacheSource(stringSourceDTO);
		SourceAPIResponseDTO sourceAPIResponseDTO = new SourceAPIResponseDTO();
		setResponseStatus(sourceAPIResponseDTO, isSourceCached);
		return sourceAPIResponseDTO;
	}

	/**
	 * [post] receive source and cache it.
	 * 
	 */
	@CrossOrigin
	@RequestMapping(value = L10NAPIV1.CREATE_SOURCE_POST, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO postSourceByKey(
			@PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@PathVariable(APIParamName.COMPONENT) String component,
			@PathVariable(APIParamName.KEY) String key,
			@RequestParam(value = APIParamName.VERSION, required = true) String version,
			@RequestParam(value = APIParamName.SOURCE, required = false) String source,
			@RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			HttpServletRequest request) throws Exception{
		LOGGER.info("The request url is {}",  request.getRequestURL());
		LOGGER.info(source);

		SourceAPIResponseDTO sourceAPIResponseDTO = new SourceAPIResponseDTO();
		// [bug 1827676] Will change it back in ResourceFileWritter.writeJSONObjectToJSONFile
		String sourceStr = this.getSourceFromRequest(request);
		if(StringUtils.isEmpty(sourceStr)) {
			sourceStr = source;
		}
		boolean isSourceCached = true;
		JSONArray listKSC = null;
		if (ConstantsKeys.JSON_KEYSET.equalsIgnoreCase(key)
				&& sourceStr.startsWith("[") && sourceStr.endsWith("]")) {
			listKSC = (JSONArray) JSONValue.parseWithException(sourceStr);
			ObjectMapper objectMapper = new ObjectMapper();
			for (Object kscObj : listKSC) {
				final KeySourceCommentDTO kscDTO = objectMapper.readValue(
						kscObj.toString(), KeySourceCommentDTO.class);
				String k = kscDTO.getKey();
				String s = kscDTO.getSource();
				String c = kscDTO.getCommentForSource();
				String sf = kscDTO.getSourceFormat();
				if (!StringUtils.isEmpty(sf) && SourceFormatUtils.isBase64Encode(sf.toUpperCase())){
					s = SourceFormatUtils.decodeSourceBase64Str(s);
					sf = SourceFormatUtils.formatSourceFormatStr(sf.toUpperCase());
				}

				final StringSourceDTO stringSourceDTO = createSourceDTO(
						productName, version, component, k, s, c, sf);
				if (!sourceService.cacheSource(stringSourceDTO)) {
					isSourceCached = false;
				}
			}
		} else {
			LOGGER.info(key);
			if (!StringUtils.isEmpty(sourceFormat) && SourceFormatUtils.isBase64Encode(sourceFormat.toUpperCase())){
				sourceStr = SourceFormatUtils.decodeSourceBase64Str(sourceStr);
				sourceFormat = SourceFormatUtils.formatSourceFormatStr(sourceFormat.toUpperCase());
			}
			final StringSourceDTO stringSourceDTO = createSourceDTO(
					productName, version, component, key, sourceStr,
					commentForSource, sourceFormat);
			if (!sourceService.cacheSource(stringSourceDTO)) {
				isSourceCached = false;
			}
		}
		setResponseStatus(sourceAPIResponseDTO, isSourceCached);
		return sourceAPIResponseDTO;
	}

	/*
	 * create a StringSourceDTO object.
	 */
	private StringSourceDTO createSourceDTO(String productName, String version, String component, String key, String source, String commentForSource,  String sourceFormat){
		StringSourceDTO stringSourceDTO = new StringSourceDTO();
		stringSourceDTO.setProductName(productName);
		stringSourceDTO.setComponent(component);
		stringSourceDTO.setVersion(version);
		stringSourceDTO.setKey(key);
		stringSourceDTO.setSource(source);
		stringSourceDTO.setComment(commentForSource);
		stringSourceDTO.setSourceFormat(sourceFormat);
		return stringSourceDTO;
	}

	/*
	 * set the response status before respond to client.
	 */
	private void setResponseStatus(SourceAPIResponseDTO sourceAPIResponseDTO, boolean isSourceCached) {
		if (isSourceCached) {
			sourceAPIResponseDTO.setResponse(APIResponseStatus.OK);
		} else {
			sourceAPIResponseDTO
					.setResponse(APIResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/*
	 * get the source from the request.
	 */
    private String getSourceFromRequest(HttpServletRequest request){
		String source = "";
		try {
			source = IOUtils.toString(request.getInputStream(),
					ConstantsUnicode.UTF8);
			if (source == null || source.equals("{}")) {
				source = ConstantsChar.EMPTY;
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
    	return source;
    }
}
