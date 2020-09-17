/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.TranslationDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.i18n.api.base.BaseAction;

public class TranslationSyncAction extends BaseAction {
	private static Logger LOGGER = LoggerFactory.getLogger(TranslationSyncAction.class);
	@Autowired
	IProductService productService;

	public void processMethod(Exception e, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		LOGGER.error("abnormal server:" + e.getLocalizedMessage());
		response.setCharacterEncoding(ConstantsUnicode.UTF8);
		response.setContentType(API.API_CHARSET);
		APIResponseDTO apiResponseDTO = new APIResponseDTO();
		apiResponseDTO.setResponse(APIResponseStatus.INTERNAL_SERVER_ERROR);
		ObjectMapper mapper = new ObjectMapper();
		String responseJson = mapper.writeValueAsString(apiResponseDTO);
		response.getWriter().printf(responseJson);
		response.flushBuffer();
	}

	public APIResponseDTO updateTranslation(
			UpdateTranslationDTO updateTranslationDTO, String productName,
			String version, HttpServletRequest request) throws Exception {
		request.setAttribute(ConstantsKeys.UPDATEDTO, updateTranslationDTO);
		ObjectMapper mapper = new ObjectMapper();
		String requestJson = "";
		try {
			requestJson = mapper.writeValueAsString(updateTranslationDTO);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		LOGGER.info("The request content of updateTranslation is:\n "
				+ requestJson);
		if (StringUtils.isEmpty(updateTranslationDTO)
				|| StringUtils.isEmpty(updateTranslationDTO.getData())
				|| StringUtils.isEmpty(updateTranslationDTO.getData()
						.getTranslation())) {
			return super.handleResponse(APIResponseStatus.BAD_REQUEST,
					"Data of updateTranslation is invalid!");
		}
		UpdateTranslationDataDTO updateTranslationDataDTO = updateTranslationDTO
				.getData();
		List<TranslationDTO> translationList = updateTranslationDataDTO
				.getTranslation();
		if (translationList == null || translationList.isEmpty()) {
			return super.handleResponse(
					APIResponseStatus.INTERNAL_SERVER_ERROR,
					"Inputting translation list is empty!");
		}
		if (StringUtils.isEmpty(updateTranslationDataDTO.getProductName())
				|| !updateTranslationDataDTO.getProductName().equals(
						productName)) {
			return super.handleResponse(APIResponseStatus.BAD_REQUEST,
					"Productname is incorrect!");
		}
		if (StringUtils.isEmpty(updateTranslationDataDTO.getVersion())
				|| !updateTranslationDataDTO.getVersion().equals(version)) {
			return super.handleResponse(APIResponseStatus.BAD_REQUEST,
					"Version is incorrect!");
		}
		List<ComponentMessagesDTO> componentMessagesDTOList = new ArrayList<ComponentMessagesDTO>();
		for (TranslationDTO translationDTO : translationList) {
			ComponentMessagesDTO componentMessagesDTO = new ComponentMessagesDTO();
			componentMessagesDTO.setProductName(updateTranslationDataDTO
					.getProductName());
			componentMessagesDTO.setVersion(updateTranslationDataDTO
					.getVersion());
			componentMessagesDTO.setComponent(translationDTO.getComponent());
			componentMessagesDTO.setLocale(translationDTO.getLocale());
			componentMessagesDTO.setMessages(translationDTO.getMessages());
			componentMessagesDTOList.add(componentMessagesDTO);
		}
		List<TranslationDTO> translationDTOList = productService
				.updateBatchTranslation(componentMessagesDTOList);
		if (translationDTOList.size() > 0) {
			return super.handleResponse(
					APIResponseStatus.INTERNAL_SERVER_ERROR,
					"Update translation failed!");
		} else {
			return super.handleResponse(APIResponseStatus.OK,
					"Update translation sucessfully!");
		}
	}

}
