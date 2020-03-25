/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class APICacheControlInterceptor extends HandlerInterceptorAdapter {

	private int second;

	public APICacheControlInterceptor(int second) {
		this.second = second;
	}

	/**
	 * add Cache-Control: max-age in Translation APIs response
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.toString())) {
			String CacheVal = "max-age=" + this.second + ", public";
			response.setHeader("Cache-Control", CacheVal);
		}
	}

}
