/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.conf;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.ValidationException;

public class CollectSourceReqBodyInterceptor implements HandlerInterceptor{
	private int sourceReqBodySize = 10485760;
	
	public CollectSourceReqBodyInterceptor(int sourceReqSize) {
		if(sourceReqSize >0) {
	    	this.sourceReqBodySize =  sourceReqSize;
	    }
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request,
							 HttpServletResponse response, Object handler) throws Exception {
		if (request.getMethod().equalsIgnoreCase(ConstantsKeys.POST) && Integer.valueOf(request.getHeader("content-length")) > this.sourceReqBodySize) {
			throw new ValidationException(String.format(ValidationMsg.COLLECTSOURCE_REQUEST_BODY_NOT_VALIDE, this.sourceReqBodySize, request.getHeader("content-length")));
		}
		return true;
	}

}
