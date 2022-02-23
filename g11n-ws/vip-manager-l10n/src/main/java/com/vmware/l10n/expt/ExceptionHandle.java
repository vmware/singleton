/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.expt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vmware.vip.common.exceptions.VIPAPIException;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
@ControllerAdvice
public class ExceptionHandle {
	private static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

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
		} else if(e instanceof VIPAPIException){
			response.setResponse(new Response(APIResponseStatus.BAD_REQUEST.getCode(), e.getMessage()));
			logger.error(e.getMessage(),e);
		} else {
			logger.error("unknown error");
			logger.error(e.getMessage(),e);
		}
		return response;
	}
}
