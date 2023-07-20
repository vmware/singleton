/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.expt;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
@ControllerAdvice
public class ExceptionHandle {
	private static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

	@ExceptionHandler(value = ValidationException.class)
	private void processValidationException (HttpServletResponse resp, ValidationException ve) {
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
		response.setResponse(APIResponseStatus.UNKNOWN_ERROR);
		response.setSignature("");
		if (e instanceof L10nAPIException) {
			logger.error("====== L10n API's Exception =======");
			logger.error(e.getMessage(), e);
			
			response.setResponse(APIResponseStatus.INTERNAL_SERVER_ERROR);
		} else {
			logger.error("unknown error");
			logger.error(e.getMessage(),e);
		}
		return response;
	}
	
	
}
