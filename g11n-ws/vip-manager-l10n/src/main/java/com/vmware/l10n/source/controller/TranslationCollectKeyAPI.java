/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;

import com.vmware.vip.common.utils.SourceFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.i18n.dto.KeySourceCommentDTO;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;
import com.vmware.vip.common.utils.RegExpValidatorUtils;


@RestController("i10n-TranslationCollectKeyAPI")
public class TranslationCollectKeyAPI {
   private static Logger logger = LoggerFactory.getLogger(TranslationCollectKeyAPI.class);
	@Autowired
	private SourceService sourceService;

	/**
	 * Post a string key's source to l10n server
	 */
	@Hidden
	@Operation(summary = APIOperation.KEY_SOURCE_POST_VALUE, description = APIOperation.KEY_SOURCE_POST_NOTES)
	@RequestMapping(value = L10nI18nAPI.TRANSLATION_KEY_APIV1, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO collectV1StringTranslation(
			@Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @RequestParam(value = APIParamName.PRODUCT_NAME, required = true) String productName,
			@Parameter(name = APIParamName.KEY, required = true, description = APIParamValue.KEY) @RequestParam(value = APIParamName.KEY, required = true) String key,
			@Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@Parameter(name = APIParamName.COMPONENT, required = true, description = APIParamValue.COMPONENT) @RequestParam(value = APIParamName.COMPONENT, required = true) String component,
			@Parameter(name = APIParamName.LOCALE, description = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@Parameter(name = APIParamName.SOURCE, required = false, description = APIParamValue.SOURCE) @RequestParam(value = APIParamName.SOURCE, required = false) String source,
			@Parameter(name = APIParamName.COMMENT_SOURCE, description = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@Parameter(name = APIParamName.SOURCE_FORMAT, description = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@Parameter(name = APIParamName.COLLECT_SOURCE, description = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource,
			//@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request) throws L10nAPIException {
		String newLocale =  StringUtils.isEmpty(locale) ? ConstantsUnicode.EN : locale;
		String newSource = source == null ? "" : source;
		logger.info("The parameters are: productName={}, version={}, component={}, locale={}, key={}, source={}", productName, version, component, locale, key, newSource);
		StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, component, newLocale, key,
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
	 * @param request
	 * @return
	 * @throws L10nAPIException
	 */
	@Hidden
	@Operation(summary = APIOperation.SOURCE_TRANSLATION_POST_VALUE, description = APIOperation.SOURCE_TRANSLATION_POST_NOTES)
	@RequestMapping(value = L10nI18nAPI.TRANSLATION_PRODUCT_COMOPONENT_KEY_APIV1, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO collectV1KeyTranslation(
			@PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@PathVariable(APIParamName.COMPONENT) String component,
			@PathVariable(APIParamName.KEY) String key,
			@Parameter(description = APIParamValue.SOURCE, required = false) @RequestBody String source,
			@Parameter(name = APIParamName.COMMENT_SOURCE, description = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@Parameter(name = APIParamName.LOCALE, description = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@Parameter(name = APIParamName.SOURCE_FORMAT, description = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@Parameter(name = APIParamName.COLLECT_SOURCE, description = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource,
			//@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request)
			throws L10nAPIException {
		String newLocale = locale == null ? ConstantsUnicode.EN : locale;
		String querySource = request.getParameter(APIParamName.SOURCE);
		String newSource = "";
		if(!StringUtils.isEmpty(source) || !StringUtils.isEmpty(querySource)) {
		   newSource = source == null ? querySource : source;
		}
		logger.info("The parameters are: productName={}, version={}, component={}, locale={}, key={}, source={}", productName, version, component, locale, key, newSource);
		StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, component, newLocale, key,
				newSource, commentForSource, sourceFormat);
		boolean isSourceCached = sourceService.cacheSource(sourceObj);
		return SourceUtils.handleSourceResponse(isSourceCached);
	}
	
	
	/**
	 *Post a string key's source to l10n server.
	 *
	 */
	@Hidden
	@Operation(summary = APIOperation.KEY_SOURCE_POST_VALUE, description = APIOperation.KEY_SOURCE_POST_NOTES)
	@RequestMapping(value = L10nI18nAPI.TRANSLATION_PRODUCT_NOCOMOPONENT_KEY_APIV1, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO collectV1KeyTranslationNoComponent(
			@Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @RequestParam(value = APIParamName.VERSION, required = true) String version,
			@Parameter(name = APIParamName.KEY, required = true, description = APIParamValue.KEY) @PathVariable(APIParamName.KEY) String key,
			@Parameter(name = APIParamName.SOURCE, required = false, description = APIParamValue.SOURCE) @RequestParam(value = APIParamName.SOURCE, required = false) String source,
			@Parameter(name = APIParamName.COMMENT_SOURCE, description = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@Parameter(name = APIParamName.LOCALE, description = APIParamValue.LOCALE) @RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@Parameter(name = APIParamName.SOURCE_FORMAT, description = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@Parameter(name = APIParamName.COLLECT_SOURCE, description = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource,
		//	@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = ConstantsKeys.FALSE) String pseudo,
			HttpServletRequest request) throws L10nAPIException {
		String newLocale = locale == null ? ConstantsUnicode.EN : locale;
	
		String newSource = source == null ? "" : source;
		StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, ConstantsFile.DEFAULT_COMPONENT, newLocale, key,
				newSource, commentForSource, sourceFormat);
		logger.info("The parameters are: productName={}, version={}, locale={}, key={}, source={}", productName, version, locale, key, newSource);
		boolean isSourceCached = sourceService.cacheSource(sourceObj);
		return SourceUtils.handleSourceResponse(isSourceCached);
		
	}
	
	/**
	 * Create source with post data, especially it's used for creating long
	 * source
	 * 
	 */
	@Operation(summary = APIOperation.KEY_TRANSLATION_POST_VALUE, description = APIOperation.KEY_TRANSLATION_POST_NOTES)
	@RequestMapping(value = L10nI18nAPI.KEY_TRANSLATION_APIV2, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO collectV2KeyTranslation(
			@Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
			@Parameter(name = APIParamName.LOCALE, required = true, description = APIParamValue.LOCALE) @PathVariable(value = APIParamName.LOCALE) String locale,
			@Parameter(name = APIParamName.COMPONENT, required = true, description = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
			@Parameter(name = APIParamName.KEY, required = true, description = APIParamValue.KEY) @PathVariable(APIParamName.KEY) String key,
			@Parameter(description = APIParamValue.SOURCE, required = false) @RequestBody String source,
			@Parameter(name = APIParamName.COMMENT_SOURCE, description = APIParamValue.COMMENT_SOURCE) @RequestParam(value = APIParamName.COMMENT_SOURCE, required = false) String commentForSource,
			@Parameter(name = APIParamName.SOURCE_FORMAT, description = APIParamValue.SOURCE_FORMAT) @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@Parameter(name = APIParamName.COLLECT_SOURCE, description = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource
      )
			throws L10nAPIException {
	
		String newSource = source == null ? "" : source;

		if (!StringUtils.isEmpty(sourceFormat)){
			sourceFormat = sourceFormat.toUpperCase();
			if (SourceFormatUtils.isBase64Encode(sourceFormat)){
				newSource = SourceFormatUtils.decodeSourceBase64Str(newSource);
				sourceFormat = SourceFormatUtils.formatSourceFormatStr(sourceFormat);
			}
		}

		StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, component, locale, key,
				newSource, commentForSource, sourceFormat);
		logger.info("The parameters are: productName={}, version={}, component={}, locale={}, key={}, source={}", productName, version, component, locale, key, newSource);
		boolean isSourceCached = sourceService.cacheSource(sourceObj);
		return SourceUtils.handleSourceResponse(isSourceCached);
	}

	/**
	 * API to post a bunch of strings
	 * @throws ValidationException 
	 *
	 */
	@Operation(summary = APIOperation.KEY_SET_POST_VALUE, description = APIOperation.KEY_SET_POST_NOTES)
	@RequestMapping(value = L10nI18nAPI.KEYS_TRANSLATION_APIV2, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO collectV2KeysTranslation(
			@Parameter(name = APIParamName.PRODUCT_NAME, required = true, description = APIParamValue.PRODUCT_NAME) @PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@Parameter(name = APIParamName.VERSION, required = true, description = APIParamValue.VERSION) @PathVariable(value = APIParamName.VERSION) String version,
			@Parameter(name = APIParamName.LOCALE, required = true, description = APIParamValue.LOCALE) @PathVariable(value = APIParamName.LOCALE) String locale,
			@Parameter(name = APIParamName.COMPONENT, required = true, description = APIParamValue.COMPONENT) @PathVariable(APIParamName.COMPONENT) String component,
			@RequestBody List<KeySourceCommentDTO> sourceSet,
			@Parameter(name = APIParamName.COLLECT_SOURCE, description = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource,
			HttpServletRequest request) throws ValidationException  {
		logger.info("The parameters are: productName={}, version={}, component={}, locale={}", productName, version, component, locale);
		for (KeySourceCommentDTO sto : sourceSet) {
			String newLocale = locale == null ? ConstantsUnicode.EN : locale;
			String newKey = sto.getKey();
			String newSource =sto.getSource();
			String sf = sto.getSourceFormat().toUpperCase();
			if (!StringUtils.isEmpty(sf) && SourceFormatUtils.isBase64Encode(sf)){
				newSource = SourceFormatUtils.decodeSourceBase64Str(newSource);
				sf = SourceFormatUtils.formatSourceFormatStr(sf);
			}

			if (!StringUtils.isEmpty(sf) && !ConstantsKeys.SOURCE_FORMAT_LIST.contains(sf)) {
				throw new ValidationException(String.format(ValidationMsg.SOURCEFORMAT_NOT_VALIDE_FORMAT, newKey));
			}
			
			if(!RegExpValidatorUtils.isAscii(newKey)) {
				throw new ValidationException(String.format(ValidationMsg.KEY_NOT_VALIDE_FORMAT, newKey));
			}
			
			StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, component, newLocale, newKey,
					newSource, sto.getCommentForSource(), sf);
			boolean isSourceCached = sourceService.cacheSource(sourceObj);
			if(!isSourceCached) {
				return SourceUtils.handleSourceResponse(isSourceCached); 
			}
		}
	
		return SourceUtils.handleSourceResponse(true); 
	}
	
	

}
