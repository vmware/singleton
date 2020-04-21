/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.VIPAPIException;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.common.utils.RegExpValidatorUtils;


public class CollectSourceValidationInterceptor extends HandlerInterceptorAdapter {
	
	private static Logger LOGGER = LoggerFactory.getLogger(CollectSourceValidationInterceptor.class);
	
	public CollectSourceValidationInterceptor(List<String> sourceLocales) {
		this.sourceLocales = sourceLocales;
	}
	
	private List<String> sourceLocales;
	
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
		try {
			validate(request, this.sourceLocales); 
		} catch (VIPAPIException e) {
			LOGGER.warn(e.getMessage());
			Response r = new Response();
			r.setCode(APIResponseStatus.BAD_REQUEST.getCode());
			r.setMessage(e.getMessage());
			try {
				response.getWriter().write(
						new ObjectMapper().writerWithDefaultPrettyPrinter()
								.writeValueAsString(r));
				return false;
			} catch (IOException e1) {
				LOGGER.warn(e1.getMessage());
				return false;
			}
		}
		String startHandle = "[thread-" + Thread.currentThread().getId() + "] Start to handle request...";
		LOGGER.info(startHandle);
		LOGGER.info(logOfUrl);
		LOGGER.info(logOfQueryStr);
		return true;
	}
	
	/**
	 * 
	 * @param request
	 * @param language types that can collect source 
	 * @throws VIPAPIException
	 */
	private static void validate(HttpServletRequest request, List<String> sourceLocales) throws VIPAPIException {
		if (request == null) { 
			return;
		}
		validateProductname(request);
		validateVersion(request);
		validateComponent(request);
		validateKey(request);
		validateLocale(request, sourceLocales);
		validateSourceformat(request);
		validateCollectsource(request);
		validatePseudo(request);
	
	}

	@SuppressWarnings("unchecked")
	private static void validateProductname(HttpServletRequest request)
			throws VIPAPIException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String productName = pathVariables.get(APIParamName.PRODUCT_NAME) == null ? request
				.getParameter(APIParamName.PRODUCT_NAME) : pathVariables
				.get(APIParamName.PRODUCT_NAME);
		if (StringUtils.isEmpty(productName)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterOrNumber(productName)) {
			throw new VIPAPIException(ValidationMsg.PRODUCTNAME_NOT_VALIDE);
		}
	}

	@SuppressWarnings("unchecked")
	private static void validateVersion(HttpServletRequest request)
			throws VIPAPIException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String version = pathVariables.get(APIParamName.VERSION) == null ? request
				.getParameter(APIParamName.VERSION) : pathVariables
				.get(APIParamName.VERSION);
		if (StringUtils.isEmpty(version)) {
			return;
		}
		if (!RegExpValidatorUtils.IsNumberAndDot(version)) {
			throw new VIPAPIException(ValidationMsg.VERSION_NOT_VALIDE);
		}
	}

	@SuppressWarnings("unchecked")
	private static void validateComponent(HttpServletRequest request)
			throws VIPAPIException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String component = pathVariables.get(APIParamName.COMPONENT) == null ? request
				.getParameter(APIParamName.COMPONENT) : pathVariables
				.get(APIParamName.COMPONENT);
		if (StringUtils.isEmpty(component)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(component)) {
			throw new VIPAPIException(ValidationMsg.COMPONENT_NOT_VALIDE);
		}
	}


	@SuppressWarnings("unchecked")
	private static void validateKey(HttpServletRequest request)
			throws VIPAPIException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String key = pathVariables.get(APIParamName.KEY) == null ? request
				.getParameter(APIParamName.KEY) : pathVariables
				.get(APIParamName.KEY);
		if (StringUtils.isEmpty(key)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(key)) {
			throw new VIPAPIException(ValidationMsg.KEY_NOT_VALIDE);
		}
	}

	@SuppressWarnings("unchecked")
	private static void validateLocale(HttpServletRequest request, List<String> sourceLocales2)
			throws VIPAPIException {
		Map<String, String> pathVariables = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String locale = pathVariables.get(APIParamName.LOCALE) == null ? request
				.getParameter(APIParamName.LOCALE) : pathVariables
				.get(APIParamName.LOCALE);
		if (StringUtils.isEmpty(locale)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterAndNumberAndValidchar(locale)) {
			throw new VIPAPIException(ValidationMsg.LOCALE_NOT_VALIDE);
		}else if (!sourceLocales2.contains(locale)) {
			throw new VIPAPIException(ValidationMsg.LOCALE_NOT_VALIDE);
		}
	}


	private static void validateSourceformat(HttpServletRequest request)
			throws VIPAPIException {
		String sourceformat = request.getParameter(APIParamName.SOURCE_FORMAT) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.SOURCE_FORMAT);
		if (StringUtils.isEmpty(sourceformat)) {
			return;
		}
		if (!RegExpValidatorUtils.IsLetterOrNumber(sourceformat)) {
			throw new VIPAPIException(ValidationMsg.SOURCEFORMAT_NOT_VALIDE);
		}
	}

	private static void validateCollectsource(HttpServletRequest request)
			throws VIPAPIException {
		String collectsource = request
				.getParameter(APIParamName.COLLECT_SOURCE) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.COLLECT_SOURCE);
		if (StringUtils.isEmpty(collectsource)) {
			return;
		}
		if (!RegExpValidatorUtils.IsTrueOrFalse(collectsource)) {
			throw new VIPAPIException(ValidationMsg.COLLECTSOURCE_NOT_VALIDE);
		}else if(collectsource.toLowerCase().equals("false")){
			throw new VIPAPIException(ValidationMsg.COLLECTSOURCE_NOT_VALIDE);
		}
	}

	private static void validatePseudo(HttpServletRequest request)
			throws VIPAPIException {
		String pseudo = request.getParameter(APIParamName.PSEUDO) == null ? ConstantsKeys.EMPTY_STRING
				: request.getParameter(APIParamName.PSEUDO);
		if (StringUtils.isEmpty(pseudo)) {
			return;
		}
		if (!RegExpValidatorUtils.IsTrueOrFalse(pseudo)) {
			throw new VIPAPIException(ValidationMsg.PSEUDO_NOT_VALIDE);
		}
	}

}
