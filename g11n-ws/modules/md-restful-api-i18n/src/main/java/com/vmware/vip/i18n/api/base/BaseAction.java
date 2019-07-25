/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.Response;

public class BaseAction {
	private static Logger logger = LoggerFactory.getLogger(BaseAction.class);

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
