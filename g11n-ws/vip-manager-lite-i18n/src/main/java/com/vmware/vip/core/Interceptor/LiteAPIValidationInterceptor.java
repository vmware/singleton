/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.Interceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.utils.RegExpValidatorUtils;

/**
 * Interceptor for collection new resource
 */
public class LiteAPIValidationInterceptor extends HandlerInterceptorAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(LiteAPIValidationInterceptor.class);

	private String clientRequestIdsStr;
	private List<String> clientRequestIds;
	public LiteAPIValidationInterceptor(String clientReqIdsStr) {
		super();
		this.clientRequestIdsStr = clientReqIdsStr;
		try {
			this.clientRequestIds = Arrays.asList(this.clientRequestIdsStr.split(ConstantsChar.COMMA));
		}catch(Exception e) {
			this.clientRequestIds = null;
		}
		
	}
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
		LOGGER.debug(request.getSession().getId());
		String singletonRequestID = getRequestId(request, this.clientRequestIds);
		String logOfUrl = singletonRequestID + "The request url is: " + request.getRequestURL();
		String logOfQueryStr = singletonRequestID + "The request query string is: " + request.getQueryString();
		String startHandle = singletonRequestID + "[thread-" + Thread.currentThread().getId() + "] Start to handle request...";
		
		validate(request);

		LOGGER.info(startHandle);
		LOGGER.info(logOfUrl);
		LOGGER.info(logOfQueryStr);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		//Do nothing because of not need to postHandle business
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		 // Do nothing because of not need to afterCompletion business
	}
	
	/**
	 * Use to get client request ID content from HTTP headers
	 */
	private String getRequestId(HttpServletRequest request, List<String> headerNames) {
		StringBuilder singletonReqIds = new StringBuilder("");
		if(headerNames != null) {
			for(String headerName: headerNames) {
				String reqIdStr = request.getHeader(headerName);
				if(!StringUtils.isEmpty(reqIdStr)) {
					singletonReqIds.append("[clientRequestHeader- ").append(headerName).append(": ")
					.append(reqIdStr).append( "] ");
				}
			}
		}
		return singletonReqIds.toString();
	}
	
	public void validate(HttpServletRequest request) throws ValidationException {
		validateProductname(request);
		validateVersion(request);
		validateComponent(request);
		validateComponents(request);
		validateKey(request);
		validateKeys(request);
		validateLocale(request);
		validateLanguage(request);
		validateLocales(request);
		validateSourceformat(request);
		validateCollectsource(request);
		validatePseudo(request);
		validateNumber(request);
		validateScale(request);
		validateRegion(request);
		validateCombine(request);
		validateScope(request);
	}

	@SuppressWarnings("unchecked")
	private void validateProductname(HttpServletRequest request)
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
	private void validateVersion(HttpServletRequest request)
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
	private void validateComponent(HttpServletRequest request)
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
	private void validateRegion(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String region = pathVariables.get(APIParamName.REGION) == null ? request
				.getParameter(APIParamName.REGION) : pathVariables
				.get(APIParamName.REGION);
		if (StringUtils.isEmpty(region)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(region)) {
			throw new ValidationException(ValidationMsg.REGION_NOT_VALIDE);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validateScope(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String scope = pathVariables.get(APIParamName.SCOPE) == null ? request
				.getParameter(APIParamName.SCOPE) : pathVariables
				.get(APIParamName.SCOPE);
		if (StringUtils.isEmpty(scope)) {
			return;
		}
		if (!RegExpValidatorUtils.isLetterArray(scope)) {
			throw new ValidationException(ValidationMsg.SCOPE_NOT_VALIDE);
		}
	}
	@SuppressWarnings("unchecked")
	private void validateCombine(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String combine = pathVariables.get(APIParamName.COMBINE) == null ? request
				.getParameter(APIParamName.COMBINE) : pathVariables
				.get(APIParamName.COMBINE);
		if (StringUtils.isEmpty(combine)) {
			return;
		}
		if (!RegExpValidatorUtils.isOneOrTwo(combine)) {
			throw new ValidationException(ValidationMsg.COMBINE_NOT_VALIDE);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validateComponents(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String components = pathVariables.get(APIParamName.COMPONENTS) == null ? request
				.getParameter(APIParamName.COMPONENTS) : pathVariables
				.get(APIParamName.COMPONENTS);
		if (StringUtils.isEmpty(components)) {
			return;
		}
		String[] compArr = components.split(ConstantsChar.COMMA);
		for(int i=0; i<compArr.length; i++) {
			if(StringUtils.isEmpty(compArr[i]) || !RegExpValidatorUtils.IsLetterAndNumberAndValidchar(compArr[i])) {
				throw new ValidationException(ValidationMsg.COMPONENTS_NOT_VALIDE);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validateLocales(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String locales = pathVariables.get(APIParamName.LOCALES) == null ? request
				.getParameter(APIParamName.LOCALES) : pathVariables
				.get(APIParamName.LOCALES);
		if (StringUtils.isEmpty(locales)) {
			return;
		}
		if (!RegExpValidatorUtils.isLetterNumbCommaAndValidchar(locales)) {
			throw new ValidationException(ValidationMsg.LOCALES_NOT_VALIDE);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void validateKey(HttpServletRequest request)
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
	private void validateKeys(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String keys = pathVariables.get(APIParamName.KEYS) == null ? request
				.getParameter(APIParamName.KEYS) : pathVariables
				.get(APIParamName.KEYS);
		if (StringUtils.isEmpty(keys)) {
			return;
		}
		if (!RegExpValidatorUtils.isAscii(keys)) {
			throw new ValidationException(ValidationMsg.KEYS_NOT_VALIDE);
		}
	}

	@SuppressWarnings("unchecked")
	private void validateLocale(HttpServletRequest request)
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

	@SuppressWarnings("unchecked")
	private void validateLanguage(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String language = pathVariables.get(APIParamName.LANGUAGE) == null ? request
				.getParameter(APIParamName.LANGUAGE) : pathVariables
				.get(APIParamName.LANGUAGE);
		if (StringUtils.isEmpty(language)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(language)) {
			throw new ValidationException(ValidationMsg.LANGUAGE_NOT_VALIDE);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void validateNumber(HttpServletRequest request)
			throws ValidationException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String number = pathVariables.get(APIParamName.NUMBER) == null ? request
				.getParameter(APIParamName.NUMBER) : pathVariables
				.get(APIParamName.NUMBER);
		if (StringUtils.isEmpty(number)) {
			return;
		}
		if (!RegExpValidatorUtils.isNumeric(number)) {
			throw new ValidationException(ValidationMsg.NUMBER_NOT_VALIDE);
		}
	}

	private void validateScale(HttpServletRequest request)
			throws ValidationException {
		String scale = request.getParameter(APIParamName.SCALE) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.SCALE);
		try {
			if (!StringUtils.isEmpty(scale)
					&& new Integer(scale).intValue() < 0) {
				throw new ValidationException(ValidationMsg.SCALE_NOT_VALIDE);
			}
		} catch (NumberFormatException e) {
			throw new ValidationException("NumberFormatException: " + e.getMessage());
		}
	}
	
	private void validateSourceformat(HttpServletRequest request)
			throws ValidationException {
		String sourceformat = request.getParameter(APIParamName.SOURCE_FORMAT) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.SOURCE_FORMAT);
		if (StringUtils.isEmpty(sourceformat)) {
			return;
		}
		if (!ConstantsKeys.SOURCE_FORMAT_LIST.contains(sourceformat.toUpperCase())) {
			throw new ValidationException(ValidationMsg.SOURCEFORMAT_NOT_VALIDE);
		}
	}

	private void validateCollectsource(HttpServletRequest request)
			throws ValidationException {
		String collectsource = request
				.getParameter(APIParamName.COLLECT_SOURCE) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.COLLECT_SOURCE);
		if (StringUtils.isEmpty(collectsource)) {
			return;
		}
		if (!RegExpValidatorUtils.IsTrueOrFalse(collectsource)) {
			throw new ValidationException(
					ValidationMsg.COLLECTSOURCE_NOT_VALIDE);
		}
	}

	private void validatePseudo(HttpServletRequest request)
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



}
