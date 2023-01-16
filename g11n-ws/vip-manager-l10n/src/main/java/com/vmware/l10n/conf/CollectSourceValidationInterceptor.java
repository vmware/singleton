/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmware.vip.common.utils.SourceFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.utils.RegExpValidatorUtils;


public class CollectSourceValidationInterceptor extends HandlerInterceptorAdapter {
	
	private static Logger LOGGER = LoggerFactory.getLogger(CollectSourceValidationInterceptor.class);
	
	public CollectSourceValidationInterceptor(Map<String, List<String>> allowListMap) {
		this.allowList = allowListMap;
	}

	private Map<String, List<String>> allowList;
	
	/**
	 * Collect new source and send to l10n server
	 *
	 * @param request
	 *            HttpServletRequest object
	 * @param response
	 *            HttpServletResponse object
	 * @param handler
	 *            Object
	 * @return a boolean result
	 * @exception Exception
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String logOfUrl = "The request url is: " + request.getRequestURL();
		String logOfQueryStr = "The request query string is: " + request.getQueryString();
		LOGGER.debug(logOfUrl);
		LOGGER.debug(logOfQueryStr);
		
		validate(request, this.allowList); 
		String startHandle = "[thread-" + Thread.currentThread().getId() + "] Start to handle request...";
		LOGGER.info(startHandle);
		LOGGER.info(logOfUrl);
		LOGGER.info(logOfQueryStr);
		return true;
	}
	
	/**
	 * 
	 * @param request
	 * @param allowList types that can collect source product list
	 * @throws ValidationException
	 */
	private static void validate(HttpServletRequest request, Map<String, List<String>> allowList) throws ValidationException {
		if (request == null) { 
			return;
		}
		validateAllowList(request, allowList);
		validateProductname(request);
		validateVersion(request);
		validateComponent(request);
		validateKey(request);
		validateLocale(request);
		validateSourceformat(request);
		validateCollectsource(request);
		validatePseudo(request);
	}

	@SuppressWarnings("unchecked")
	private static void validateProductname(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String productName = pathVariables.get(APIParamName.PRODUCT_NAME) == null ? request
				.getParameter(APIParamName.PRODUCT_NAME) : pathVariables
				.get(APIParamName.PRODUCT_NAME);
		if (StringUtils.isEmpty(productName)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterOrNumber(productName)) {
			throw new ValidationException(ValidationMsg.PRODUCTNAME_NOT_VALIDE);
		}
	}

	@SuppressWarnings("unchecked")
	private static void validateVersion(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String version = pathVariables.get(APIParamName.VERSION) == null ? request
				.getParameter(APIParamName.VERSION) : pathVariables
				.get(APIParamName.VERSION);
		if (StringUtils.isEmpty(version)) {
			return;
		}
		if (!RegExpValidatorUtils.IsNumberAndDot(version)) {
			throw new ValidationException(ValidationMsg.VERSION_NOT_VALIDE);
		}
	}

	@SuppressWarnings("unchecked")
	private static void validateComponent(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String component = pathVariables.get(APIParamName.COMPONENT) == null ? request
				.getParameter(APIParamName.COMPONENT) : pathVariables
				.get(APIParamName.COMPONENT);
		if (StringUtils.isEmpty(component)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(component)) {
			throw new ValidationException(ValidationMsg.COMPONENT_NOT_VALIDE);
		}
	}


	@SuppressWarnings("unchecked")
	private static void validateKey(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String key = pathVariables.get(APIParamName.KEY) == null ? request
				.getParameter(APIParamName.KEY) : pathVariables
				.get(APIParamName.KEY);
		if (StringUtils.isEmpty(key)) {
			return;
		}
		if (!RegExpValidatorUtils.isAscii(key)) {
			throw new ValidationException(String.format(ValidationMsg.KEY_NOT_VALIDE_FORMAT, key));
		}
	}

	@SuppressWarnings("unchecked")
	private static void validateLocale(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String locale = pathVariables.get(APIParamName.LOCALE) == null ? request
				.getParameter(APIParamName.LOCALE) : pathVariables
				.get(APIParamName.LOCALE);
		if (StringUtils.isEmpty(locale)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(locale)) {
			throw new ValidationException(ValidationMsg.LOCALE_NOT_VALIDE);
		}
	}

	public static void validateSourceformat(HttpServletRequest request)
			throws ValidationException {
		String sourceFormat = request.getParameter(APIParamName.SOURCE_FORMAT) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.SOURCE_FORMAT);
		if (!StringUtils.isEmpty(sourceFormat)) {
			sourceFormat = sourceFormat.toUpperCase();
			if (SourceFormatUtils.isBase64Encode(sourceFormat)){
				sourceFormat = SourceFormatUtils.formatSourceFormatStr(sourceFormat);
				if (!StringUtils.isEmpty(sourceFormat) && !ConstantsKeys.SOURCE_FORMAT_LIST.contains(sourceFormat)) {
					throw new ValidationException(ValidationMsg.SOURCEFORMAT_NOT_VALIDE);
				}
			}else{
				if (!ConstantsKeys.SOURCE_FORMAT_LIST.contains(sourceFormat)) {
					throw new ValidationException(ValidationMsg.SOURCEFORMAT_NOT_VALIDE);
				}
			}
		}
	}

	private static void validateCollectsource(HttpServletRequest request)
			throws ValidationException {
		String collectsource = request
				.getParameter(APIParamName.COLLECT_SOURCE) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.COLLECT_SOURCE);
		if (StringUtils.isEmpty(collectsource)) {
			return;
		}
		if (!RegExpValidatorUtils.IsTrueOrFalse(collectsource)) {
			throw new ValidationException(ValidationMsg.COLLECTSOURCE_NOT_VALIDE_L10N);
		}else if(collectsource.toLowerCase().equals("false")){
			throw new ValidationException(ValidationMsg.COLLECTSOURCE_NOT_VALIDE_L10N);
		}
	}

	private static void validatePseudo(HttpServletRequest request)
			throws ValidationException {
		String pseudo = request.getParameter(APIParamName.PSEUDO) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.PSEUDO);
		if (StringUtils.isEmpty(pseudo)) {
			return;
		}
		if (!RegExpValidatorUtils.IsTrueOrFalse(pseudo)) {
			throw new ValidationException(ValidationMsg.PSEUDO_NOT_VALIDE);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void validateAllowList(HttpServletRequest request, Map<String, List<String>> allowList) 
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String productName = pathVariables.get(APIParamName.PRODUCT_NAME) == null
				? request.getParameter(APIParamName.PRODUCT_NAME)
				: pathVariables.get(APIParamName.PRODUCT_NAME);
		String version = pathVariables.get(APIParamName.VERSION) == null ? request.getParameter(APIParamName.VERSION)
				: pathVariables.get(APIParamName.VERSION);
		if (StringUtils.isEmpty(productName) || StringUtils.isEmpty(version)) {
			return;
		}
		
		if(allowList != null && allowList.containsKey(productName)
				&& (allowList.get(productName).contains(ConstantsChar.ASTERISK) || allowList.get(productName).contains(version))) {
			return;
		}else {
			throw new ValidationException(String.format(ValidationMsg.PRODUCTNAME_NOT_SUPPORTED, productName));
		}
	}

}
