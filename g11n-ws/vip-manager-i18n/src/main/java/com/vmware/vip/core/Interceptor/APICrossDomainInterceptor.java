/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

public class APICrossDomainInterceptor implements HandlerInterceptor {

	private Set<String> allowOrigin;
	private String allowHeaders;
	private String allowMethods;
	private String allowCredentials;
	private String maxAge;

	public APICrossDomainInterceptor(Set<String> allowOrigin, String allowHeaders, String allowMethods, String allowCredentials, String maxAge) {
		this.allowOrigin = allowOrigin;
		this.allowHeaders = allowHeaders;
		this.allowMethods = allowMethods;
		this.allowCredentials = allowCredentials;
		this.maxAge = maxAge;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String originHeader = request.getHeader("Origin");
		if (allowOrigin.contains("*") || allowOrigin.contains(originHeader)) {
			response.setHeader("Access-Control-Allow-Origin", originHeader == null ? "" : originHeader);
			response.setHeader("Access-Control-Allow-Methods", allowMethods);
			response.setHeader("Access-Control-Allow-Headers", allowHeaders);
			response.setHeader("Access-Control-Allow-Credentials", allowCredentials);
			response.setHeader("Access-Control-Max-Age", maxAge);
		}
		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			return false;
		}
		return true;
	}

}
