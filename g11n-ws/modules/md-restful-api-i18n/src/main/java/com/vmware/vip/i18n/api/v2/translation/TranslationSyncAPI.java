/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.translation;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.i18n.api.base.TranslationSyncAction;

import io.swagger.annotations.ApiOperation;

/**
 *
 * Provide RESTful API to synchronize the latest translation.
 *
 */
@RestController("v2-TranslationSyncAPI")
public class TranslationSyncAPI extends TranslationSyncAction {

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.OK)
	public void processMethod(Exception e, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		super.processMethod(e, request, response);
	}

	/**
	 * Synchronize the latest translation from GRM or other third party.
	 * <p>
	 * Apply to On-Premise and SaaS.
	 *
	 * @param updateTranslationDTO
	 *            This Java Bean represents request content from GRM or other
	 *            third party. Base on product.
	 * @param productName
	 *            The name of product.
	 * @param version
	 *            The release version of product.
	 * @param request
	 *            Extends the ServletRequest interface to provide request
	 *            information for HTTP servlets.
	 * @return APIResponseDTO The object which represents response status.
	 */
	@ApiOperation(value = APIOperation.TRANSLATION_UPDATE_VALUE, notes = APIOperation.TRANSLATION_UPDATE_NOTES)
	@RequestMapping(value = APIV2.PRODUCT_TRANSLATION_PUT, method = RequestMethod.PUT, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO updateTranslation(
			@RequestBody UpdateTranslationDTO translationData,
			@PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@PathVariable(APIParamName.VERSION) String version,
			HttpServletRequest request) throws Exception {
		return super.updateTranslation(translationData, productName, version, request);
	}
}
