/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.except;

import java.text.MessageFormat;

import com.vmware.vip.core.about.exception.AboutAPIException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.common.exceptions.VIPHttpException;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.core.messages.exception.L2APIException;
import com.vmware.vip.core.messages.exception.L3APIException;

@ControllerAdvice
public class ExceptionHandle {
	private static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public APIResponseDTO handler(Exception e) {
		APIResponseDTO response = new APIResponseDTO();
		response.setData("");
		response.setSignature("");
		if (e instanceof AboutAPIException) {
			logger.error("====== About API's Exception =======");
			logger.error(e.getMessage());
			response.setResponse(new Response(APIResponseStatus.INTERNAL_NO_RESOURCE_ERROR.getCode(), e.getMessage()));
		} else if (e instanceof L3APIException) {
			logger.error("====== L3 API's Exception =======");
			logger.error(e.getMessage());
			response.setResponse(new Response(APIResponseStatus.INTERNAL_NO_RESOURCE_ERROR.getCode(), e.getMessage()));
		} else if (e instanceof L2APIException) {
			logger.error("====== L2 API's Exception =======");
			logger.error(e.getMessage());
			response.setResponse(new Response(APIResponseStatus.BAD_REQUEST.getCode(), e.getMessage()));
		}  else if (e instanceof VIPHttpException) {
			logger.error("====== HTTP Exception =======");
			logger.error(e.getMessage());
			response.setResponse(new Response(APIResponseStatus.INTERNAL_SERVER_ERROR.getCode(), e.getMessage()));
		} else {
			response.setResponse(new Response(APIResponseStatus.UNKNOWN_ERROR.getCode(), e.getMessage()));
			String errorStr = MessageFormat.format("unknown error: {0}" ,e.getMessage());
			logger.error(errorStr);
		}
		String rstr = "[response] " + response.getResponse().toJSONString();
		logger.info(rstr);
		String endHandle = "[thread-" + Thread.currentThread().getId() + "] End to handle request.";
		logger.info(endHandle);
		return response;
	}
}
