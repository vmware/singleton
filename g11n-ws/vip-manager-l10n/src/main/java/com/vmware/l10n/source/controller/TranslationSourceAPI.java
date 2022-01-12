/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.controller;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.ResponseBody;
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
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;
import com.vmware.vip.common.utils.KeyUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Provide RESTful API for product to get translation by String base.
 *
 */
@RestController("i10n-TranslationSourceAPI")
public class TranslationSourceAPI  {
	private static Logger logger = LoggerFactory.getLogger(TranslationSourceAPI.class);
	@Autowired
	private SourceService sourceService;
	
	/**
	 * Create source with post data, especially it's used for creating long
	 * source
	 * 
	 */
	@ApiIgnore
	@ApiOperation(value = APIOperation.SOURCE_TRANSLATION_POST_VALUE, notes = APIOperation.SOURCE_TRANSLATION_POST_NOTES)
	@RequestMapping(value = L10nI18nAPI.TRANSLATION_SOURCE_APIV1, method = RequestMethod.POST, produces = { API.API_CHARSET })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO postTranslationBySource(
			@PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@RequestParam(value = APIParamName.VERSION) String version,
			@PathVariable(APIParamName.COMPONENT) String component,
			@RequestBody String source,
			@RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
			@RequestParam(value = APIParamName.LOCALE, required = false) String locale,
			@ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE) @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource,
			//@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required = false, defaultValue = "false") String pseudo,
			HttpServletRequest request) throws L10nAPIException {	
		String key = KeyUtils.generateKey(component, null, source.toString());
		String newLocale =  StringUtils.isEmpty(locale) ? ConstantsUnicode.EN : locale;
		StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, component, newLocale, key,
				source, null, sourceFormat);
		logger.info("The parameters are: productName={}, version={}, component={}, locale={}, key={}, source={}", productName, version, component, newLocale, key, source);
		boolean isSourceCached = sourceService.cacheSource(sourceObj);
		return SourceUtils.handleSourceResponse(isSourceCached);
	}

	/**
	 * Create source with post data, especially it's used for creating long
	 * source
	 * 
	 * @param productName
	 *        The name of product.
	 * @param component
	 *        The name of component.
	 * @param version
	 *        The release version of product.
	 * @param source
	 *        The English string that you want to translate.
	 * @param locale
	 *        The name of locale. e.g: ja_JP, zh_CN.
	 * @param request
	 *        Extends the ServletRequest interface to provide request information for HTTP servlets.
	 * @return APIResponseDTO The object which represents response status.
	 */
    
    @ApiOperation(value = APIOperation.SOURCE_TRANSLATION_POST_VALUE, notes = APIOperation.SOURCE_TRANSLATION_POST_NOTES)
    @RequestMapping(value = L10nI18nAPI.TRANSLATION_SOURCE_APIV2, method = RequestMethod.POST, produces = { API.API_CHARSET })
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
	public SourceAPIResponseDTO createSource (
			@PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@PathVariable(APIParamName.COMPONENT) String component,
			@PathVariable(value = APIParamName.VERSION) String version,
			@PathVariable(value = APIParamName.LOCALE) String locale,
            @RequestBody String source,
            @RequestParam(value = APIParamName.SOURCE_FORMAT, required = false) String sourceFormat,
            @ApiParam(name = APIParamName.COLLECT_SOURCE, value = APIParamValue.COLLECT_SOURCE)
            @RequestParam(value = APIParamName.COLLECT_SOURCE, required = true, defaultValue = "true") String collectSource,
           //@ApiParam(name = APIParamName.PSEUDO, value = APIParamValue.PSEUDO) @RequestParam(value = APIParamName.PSEUDO, required=false, defaultValue="false") String pseudo,
            HttpServletRequest request)  throws L10nAPIException {
    	String key = KeyUtils.generateKey(component, null, source.toString());
		String newLocale =  StringUtils.isEmpty(locale) ? ConstantsUnicode.EN : locale;
		StringSourceDTO sourceObj = SourceUtils.createSourceDTO(productName, version, component, newLocale, key,
				source, null, sourceFormat);
		logger.info("The parameters are: productName={}, version={}, component={}, locale={}, key={}, source={}", productName, version, component, newLocale, key, source);
		boolean isSourceCached = sourceService.cacheSource(sourceObj);
		return SourceUtils.handleSourceResponse(isSourceCached);
    	
	}
}
