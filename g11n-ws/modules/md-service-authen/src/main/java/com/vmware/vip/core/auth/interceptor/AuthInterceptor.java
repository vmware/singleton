/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.auth.interceptor;

import java.io.PrintWriter;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.core.csp.service.TokenService;

public class AuthInterceptor extends HandlerInterceptorAdapter {

	private static Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	private String allowSourceCollection;
	private final TokenService tokenService;
	private static final String CSP_AUTH_TOKEN = "csp-auth-token";

	public AuthInterceptor(String allowSourceCollection, TokenService tokenService) {
		this.allowSourceCollection = allowSourceCollection;
		this.tokenService = tokenService;
	}

	@Override
	public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
			throws Exception {
		if (StringUtils.equalsIgnoreCase(request.getParameter(ConstantsKeys.COLLECT_SOURCE), ConstantsKeys.TRUE)) {
			PrintWriter writer = response.getWriter();
			if (allowSourceCollection.equalsIgnoreCase(ConstantsKeys.TRUE)) {
				final String token = request.getHeader(CSP_AUTH_TOKEN);
				if(token == null) {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.getWriter().write(this.buildRespBody(HttpStatus.UNAUTHORIZED.value(), ConstantsKeys.TOKEN_VALIDATION_ERROR));
					return false;
				}
				if (!tokenService.isTokenValid(token)) {
					// The user is not authenticated.
					response.setStatus(HttpStatus.FORBIDDEN.value());
					response.getWriter().write(this.buildRespBody(HttpStatus.FORBIDDEN.value(), ConstantsKeys.TOKEN_INVALIDATION_ERROR));
					return false;
				}
			} else {
				response.setStatus(HttpStatus.FORBIDDEN.value());
				writer.write(this.buildRespBody(HttpStatus.FORBIDDEN.value(), ConstantsKeys.SOURCE_COLLECTION_ERROR));
				return false;
			}
		}
		return true;
	}

	private String buildRespBody(int code, String msg) {
		Response resp = new Response();
		resp.setCode(code);
		resp.setMessage(msg);
		resp.setServerTime(LocalDateTime.now().toString());
		String responseBody = resp.toJSONString();
		if (logger.isDebugEnabled()) {
			logger.debug(responseBody);
		}
		String rstr = "[response] " + responseBody;
		logger.info(rstr);
		String endHandle = "[thread-" + Thread.currentThread().getId() + "] End to handle request.";
		logger.info(endHandle);
		return responseBody;
	}

}
