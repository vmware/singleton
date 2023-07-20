/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ValidationMsg;
import com.vmware.vip.common.exceptions.ValidationException;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.TranslationDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.utils.RegExpValidatorUtils;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;

public class TranslationSyncAction extends BaseAction {
	private static Logger LOGGER = LoggerFactory.getLogger(TranslationSyncAction.class);
	@Autowired
	IProductService productService;
   
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
			Map<String, String> msgs = translationDTO.getMessages();
			validateKeys(msgs);
			componentMessagesDTO.setMessages(msgs);
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
	
	
	public void validateKeys(Map<String, String> msgs) throws ValidationException {
		for(Entry<String,String> entry : msgs.entrySet()) {
			String key = entry.getKey();
			if(!RegExpValidatorUtils.isAscii(key)) {
				throw new ValidationException(String.format(ValidationMsg.KEY_NOT_VALIDE_FORMAT, key));
			}
		}
		
	}

}
