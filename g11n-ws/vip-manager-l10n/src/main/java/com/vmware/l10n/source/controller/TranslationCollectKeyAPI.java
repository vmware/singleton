/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.l10n.source.dto.SourceAPIResponseDTO;
import com.vmware.l10n.source.service.SourceService;
import com.vmware.l10n.utils.SourceUtils;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIParamValue;
import com.vmware.vip.api.rest.l10n.L10nI18nAPI;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.KeySourceCommentDTO;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@RestController("i10n-TranslationCollectKeyAPI")
public class TranslationCollectKeyAPI {

	@Autowired
	private SourceService sourceService;

	/**
	 * Post a string key's source to l10n server
	 */
	@ApiIgnore
	@ApiOperation(value = APIOperation.KEY_SOURCE_POST_VALUE, notes = APIOperation.KEY_SOURCE_POST_NOTES)
	@RequestMapping(value = L10nI18nAPI.TRANSLATION_KEY_APIV1, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO collectV1StringTranslation(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @RequestParam(value = APIParamName.PRODUCT_NAME, required = true) String productName,
			@ApiParam(name = APIParamName.KEY, required = true, value = APIParamValue.KEY) @RequestParam(value = APIParamName.KEY, required = true) String key,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT) @RequestParam(value = APIParamName.COMPONENT, required = true) String component,
			@ApiParam(name = APIParamName.LOCALE, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@ApiParam(name = APIParamName.SOURCE, required = false, value = APIParamValue.SOURCE) @RequestParam(value = APIParamName.SOURCE, required = false) String source,
			@ApiParam(name = APIParamName.COMMENT_SOURCE, value = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@ApiParam(name = APIParamName.SOURCE_FORMAT, value = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource,
			//@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request) throws L10nAPIException {
		String newLocale =  StringUtils.isEmpty(locale) ? ConstantsUnicode.EN : locale;
		String newKey = StringUtils.isEmpty(sourceFormat) ? key
				: (key + ConstantsChar.DOT + ConstantsChar.POUND + sourceFormat.toUpperCase());
		String newSource = source == null ? "" : source;
		StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, component, newLocale, newKey,
				newSource, commentForSource, sourceFormat);
		boolean isSourceCached = sourceService.cacheSource(sourceObj);
		return SourceUtils.handleSourceResponse(isSourceCached);
	}
	
	
	/**
	 *  Post a key's source to l10n server
	 * @param productName
	 * @param version
	 * @param component
	 * @param key
	 * @param source
	 * @param commentForSource
	 * @param locale
	 * @param sourceFormat
	 * @param collectSource
	 * @param pseudo
	 * @param request
	 * @return
	 * @throws L10nAPIException
	 */
	@ApiIgnore
	@ApiOperation(value = APIOperation.SOURCE_TRANSLATION_POST_VALUE, notes = APIOperation.SOURCE_TRANSLATION_POST_NOTES)
	@RequestMapping(value = L10nI18nAPI.TRANSLATION_PRODUCT_COMOPONENT_KEY_APIV1, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO collectV1KeyTranslation(
			@PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@PathVariable(APIParamName.COMPONENT) String component,
			@PathVariable(APIParamName.KEY) String key,
			@ApiParam(value = APIParamValue.SOURCE, required = false) @RequestBody String source,
			@ApiParam(name = APIParamName.COMMENT_SOURCE, value = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@ApiParam(name = APIParamName.LOCALE, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@ApiParam(name = APIParamName.SOURCE_FORMAT, value = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource,
			//@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request)
			throws L10nAPIException {
		String newLocale = locale == null ? ConstantsUnicode.EN : locale;
		String newKey = StringUtils.isEmpty(sourceFormat) ? key
				: (key + ConstantsChar.DOT + ConstantsChar.POUND + sourceFormat.toUpperCase());
		String querySource = request.getParameter(APIParamName.SOURCE);
		String newSource = "";
		if(!StringUtils.isEmpty(source) || !StringUtils.isEmpty(querySource)) {
		   newSource = source == null ? querySource : source;
		}
		StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, component, newLocale, newKey,
				newSource, commentForSource, sourceFormat);
		boolean isSourceCached = sourceService.cacheSource(sourceObj);
		return SourceUtils.handleSourceResponse(isSourceCached);
	}
	
	
	/**
	 *Post a string key's source to l10n server.
	 *
	 */
	@ApiIgnore
	@ApiOperation(value = APIOperation.KEY_SOURCE_POST_VALUE, notes = APIOperation.KEY_SOURCE_POST_NOTES)
	@RequestMapping(value = L10nI18nAPI.TRANSLATION_PRODUCT_NOCOMOPONENT_KEY_APIV1, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO collectV1KeyTranslationNoComponent(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@ApiParam(name = APIParamName.KEY, required = true, value = APIParamValue.KEY) @PathVariable(APIParamName.KEY) String key,
			@ApiParam(name = APIParamName.SOURCE, required = false, value = APIParamValue.SOURCE) @RequestParam(value = APIParamName.SOURCE, required = false) String source,
			@ApiParam(name = APIParamName.COMMENT_SOURCE, value = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@ApiParam(name = APIParamName.LOCALE, value = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@ApiParam(name = APIParamName.SOURCE_FORMAT, value = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource,
		//	@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = ConstantsKeys.FALSE) String pseudo,
			HttpServletRequest request) throws L10nAPIException {
		String newLocale = locale == null ? ConstantsUnicode.EN : locale;
		String newKey = StringUtils.isEmpty(sourceFormat) ? key
				: (key + ConstantsChar.DOT + ConstantsChar.POUND + sourceFormat.toUpperCase());
		String newSource = source == null ? "" : source;
		StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, ConstantsFile.DEFAULT_COMPONENT, newLocale, newKey,
				newSource, commentForSource, sourceFormat);
		boolean isSourceCached = sourceService.cacheSource(sourceObj);
		return SourceUtils.handleSourceResponse(isSourceCached);
		
	}
	
	/**
	 * Create source with post data, especially it's used for creating long
	 * source
	 * 
	 */
	@ApiOperation(value = APIOperation.KEY_TRANSLATION_POST_VALUE, notes = APIOperation.KEY_TRANSLATION_POST_NOTES)
	@RequestMapping(value = L10nI18nAPI.KEY_TRANSLATION_APIV2, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO collectV2KeyTranslation(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
			@ApiParam(name = APIParamName.LOCALE, required = true, value = APIParamValue.LOCALE) @PathVariable(value = APIParamName.LOCALE) String locale,
			@ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
			@ApiParam(name = APIParamName.KEY, required = true, value = APIParamValue.KEY) @PathVariable(APIParamName.KEY) String key,
			@ApiParam(value = APIParamValue.SOURCE, required = false) @RequestBody String source,
			@ApiParam(name = APIParamName.COMMENT_SOURCE, value = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@ApiParam(name = APIParamName.SOURCE_FORMAT, value = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource,
		//@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
        // @ApiParam(name = APIParamName.MT, value = APIParamValue.MT) @RequestParam(value = APIParamName.MT, required=false, defaultValue="false") String machineTranslation,
        // @RequestParam(value = APIParamName.CHECK_TRANS_STATUS, required=false, defaultValue="false") String checkTranslationStatus,
            HttpServletRequest request)
			throws L10nAPIException {
		String newKey = StringUtils.isEmpty(sourceFormat) ? key
				: (key + ConstantsChar.DOT + ConstantsChar.POUND + sourceFormat.toUpperCase());
		String newSource = source == null ? "" : source;
		StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, component, locale, newKey,
				newSource, commentForSource, sourceFormat);
		boolean isSourceCached = sourceService.cacheSource(sourceObj);
		return SourceUtils.handleSourceResponse(isSourceCached);
	}

	/**
	 * API to post a bunch of strings
	 *
	 */
	@ApiOperation(value = APIOperation.KEY_SET_POST_VALUE, notes = APIOperation.KEY_SET_POST_NOTES)
	@RequestMapping(value = L10nI18nAPI.KEYS_TRANSLATION_APIV2, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO collectV2KeysTranslation(
			@ApiParam(name = APIParamName.PRODUCT_NAME, required = true, value = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@ApiParam(name = APIParamName.VERSION, required = true, value = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
			@ApiParam(name = APIParamName.LOCALE, required = true, value = APIParamValue.LOCALE) @PathVariable(value = APIParamName.LOCALE) String locale,
			@ApiParam(name = APIParamName.COMPONENT, required = true, value = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
			@RequestBody List<KeySourceCommentDTO> sourceSet,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource,
			HttpServletRequest request) throws L10nAPIException {
		for (KeySourceCommentDTO sto : sourceSet) {
			String newLocale = locale == null ? ConstantsUnicode.EN : locale;
			String newKey = sto.getKey();
			String newSource =sto.getSource();
			StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, component, newLocale, newKey,
					newSource, sto.getCommentForSource(), null);
			boolean isSourceCached = sourceService.cacheSource(sourceObj);
			if(!isSourceCached) {
				return SourceUtils.handleSourceResponse(isSourceCached); 
			}
		}
	
		return SourceUtils.handleSourceResponse(true); 
	}
	
	

}
