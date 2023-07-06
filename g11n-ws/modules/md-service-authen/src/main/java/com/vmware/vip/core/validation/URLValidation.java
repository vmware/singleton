/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.validation;


import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.ValidationException;

public class URLValidation implements IVlidation {
   private static Logger logger = LoggerFactory.getLogger(URLValidation.class);
	HttpServletRequest request = null;

	public URLValidation(HttpServletRequest request) {
		this.request = request;
	}

	public void validate() throws ValidationException {
		if (request != null) {
			// String uri = request.getRequestURI();
			String url = request.getRequestURL().toString();
			if (!url.contains(API.I18N_API_ROOT)
					&& !url.contains(API.L10N_API_ROOT)
					&& !url.contains("/auth/token")
					&& !validateSwaggerUIRequest(url)&& !url.contains("/error")) {
				
				logger.info("current invalid url : "+url);
				
			
				throw new ValidationException(ValidationMsg.INVALID_URL);
			}
		}
	}

	private boolean validateSwaggerUIRequest(String url) {
		if (StringUtils.isEmpty(url)) {
			return false;
		}
		boolean containedSwagger = false;
		String[] rs = { "swagger-resources", "api-docs", "swagger-ui" };
		for (String r : rs) {
			if (url.contains(r)) {
				containedSwagger = true;
				break;
			}
		}
		return containedSwagger;
	}
}
