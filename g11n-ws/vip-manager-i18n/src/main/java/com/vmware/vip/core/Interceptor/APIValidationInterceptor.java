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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.core.auth.AuthenException;
import com.vmware.vip.core.auth.IAuthen;
import com.vmware.vip.core.auth.IPAuthentication;
import com.vmware.vip.core.auth.VIPAuthentication;
import com.vmware.vip.core.validation.IVlidation;
import com.vmware.vip.core.validation.ParameterValidation;
import com.vmware.vip.core.validation.URLValidation;
import com.vmware.vip.core.validation.VIPValidation;
/**
 * Interceptor for collection new resource
 */
public class APIValidationInterceptor extends HandlerInterceptorAdapter {
	private static Logger LOGGER = LoggerFactory.getLogger(APIValidationInterceptor.class);

	private Map<String, Object> allowedListMap;
	private String clientRequestIdsStr;
	private List<String> clientRequestIds;
	public APIValidationInterceptor(Map<String, Object> allowedListMap, String clientReqIdsStr) {
		super();
		this.allowedListMap = allowedListMap;
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
		LOGGER.debug(logOfUrl);
		LOGGER.debug(logOfQueryStr);
		IVlidation u = VIPValidation.getInstance(URLValidation.class, request);
		IVlidation p = VIPValidation.getInstance(ParameterValidation.class, request);
		
		u.validate();
		request.setAttribute(ParameterValidation.TAG_ALLOW_PRODUCT_LIST_MAP, this.allowedListMap);
		p.validate();
			
		String startHandle = singletonRequestID + "[thread-" + Thread.currentThread().getId() + "] Start to handle request...";
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

}
