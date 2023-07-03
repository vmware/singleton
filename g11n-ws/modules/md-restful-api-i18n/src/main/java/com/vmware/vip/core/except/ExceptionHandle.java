/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.except;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.exceptions.VIPHttpException;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.core.about.exception.AboutAPIException;
import com.vmware.vip.core.messages.exception.L2APIException;
import com.vmware.vip.core.messages.exception.L3APIException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;


@ControllerAdvice
public class ExceptionHandle {
	private static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);
	
	@ExceptionHandler(value = ValidationException.class)
	private void processValidationException (HttpServletResponse resp, ValidationException ve) {
		logger.error("====== Validation Exception =======");
		logger.error(ve.getMessage());
		Response respObj =  new Response(APIResponseStatus.BAD_REQUEST.getCode(), ve.getMessage());
		resp.setContentType("application/json;charset=utf-8");
		try {
			resp.getWriter().write(
					new ObjectMapper().writerWithDefaultPrettyPrinter()
							.writeValueAsString(respObj));
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public APIResponseDTO handler(Exception e) {
		APIResponseDTO response = new APIResponseDTO();
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
