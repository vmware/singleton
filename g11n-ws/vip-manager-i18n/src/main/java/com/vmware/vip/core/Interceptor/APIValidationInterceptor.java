/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.Interceptor;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.core.auth.AuthenException;
import com.vmware.vip.core.auth.IAuthen;
import com.vmware.vip.core.auth.IPAuthentication;
import com.vmware.vip.core.auth.VIPAuthentication;
import com.vmware.vip.core.validation.IVlidation;
import com.vmware.vip.core.validation.ParameterValidation;
import com.vmware.vip.core.validation.URLValidation;
import com.vmware.vip.core.validation.VIPValidation;
import com.vmware.vip.core.validation.ValidationException;

/**
 * Interceptor for collection new resource
 */
public class APIValidationInterceptor extends HandlerInterceptorAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(APIValidationInterceptor.class);

	private Map<String, Object> whiteListMap;
	public APIValidationInterceptor(Map<String, Object> whiteListMap) {
		super();
		this.whiteListMap = whiteListMap;
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
		String logOfUrl = "The request url is: " + request.getRequestURL();
		String logOfQueryStr = "The request query string is: " + request.getQueryString();
		LOGGER.debug(logOfUrl);
		LOGGER.debug(logOfQueryStr);
		IVlidation u = VIPValidation.getInstance(URLValidation.class, request);
		IVlidation p = VIPValidation.getInstance(ParameterValidation.class,
				request);
		try {
			u.validate();
			request.setAttribute(ParameterValidation.TAG_WHITE_LIST_MAP, this.whiteListMap);
			p.validate();
		} catch (ValidationException e) {
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

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// Authen
		IAuthen ipauth = VIPAuthentication.getInstance(IPAuthentication.class,
				request);
		try {
			ipauth.authen();
		} catch (AuthenException ae) {
			LOGGER.warn(ae.getMessage(), ae);
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		 // Do nothing because of not need to afterCompletion business
	}

}
