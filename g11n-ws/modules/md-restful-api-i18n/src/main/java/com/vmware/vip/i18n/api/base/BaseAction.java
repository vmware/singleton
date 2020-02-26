/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.i18n.api.base.utils.VersionMatcher;

public class BaseAction {
	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

	@Autowired
	protected IProductService baseProductService;
	
	protected String availableVersion(String productName, String version) {
        try {
          return VersionMatcher.getMatchedVersion(version, baseProductService.getSupportVersionList(productName));
        }catch(Exception e) {
          return version;
        }
    }
   
	protected APIResponseDTO versionFallbackHandleResponse(String oldVersion, String newVersion, Object data) {
		 if(oldVersion.equals(newVersion)) {
			 return handleResponse(APIResponseStatus.OK, data);
		 }else {
			 return handleResponse(APIResponseStatus.FALLBACK_TRANSLATION, data);
		 }
	}
	
	protected APIResponseDTO handleResponse(Response response, Object data) {
		APIResponseDTO d = new APIResponseDTO();
		d.setData(data == null ? "" : data);
		response.setServerTime(LocalDateTime.now().toString());
		d.setResponse(response);
		if (logger.isDebugEnabled()) {
			String logOfResData = "The response data: "
					+ d.getData().toString();
			logger.debug(logOfResData);
		}
		String rstr = "[response] " + response.toJSONString();
		logger.info(rstr);
		String endHandle = "[thread-" + Thread.currentThread().getId() + "] End to handle request.";
		logger.info(endHandle);
		return d;
	}

	protected APIResponseDTO handleResponse(int code, String message,
			Object data) {
		APIResponseDTO d = new APIResponseDTO();
		d.setData(data);
		Response r = new Response();
		r.setCode(code);
		r.setMessage(message);
		r.setServerTime(LocalDateTime.now().toString());
		d.setResponse(r);
		if (logger.isDebugEnabled()) {
			String logOfResData = "The response data: "
					+ d.getData().toString();
			logger.debug(logOfResData);
		}
		String rstr = "[response] " + r.toJSONString();
		logger.info(rstr);
		String endHandle = "[thread-" + Thread.currentThread().getId() + "] End to handle request.";
		logger.info(endHandle);
		return d;
	}
}
