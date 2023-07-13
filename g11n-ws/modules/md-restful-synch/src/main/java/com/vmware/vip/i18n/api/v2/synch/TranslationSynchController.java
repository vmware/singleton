/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.api.v2.synch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.api.rest.API;
import com.vmware.vip.api.rest.APIOperation;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.TranslationDTO;
import com.vmware.vip.common.i18n.dto.response.APIResponseDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.messages.synch.model.SynchFile2GitReq;
import com.vmware.vip.messages.synch.service.SynchService;
import com.vmware.vip.messages.synch.utils.HttpsUtils;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author shihu
 *
 */

@RestController
public class TranslationSynchController {
	private static Logger logger = LoggerFactory.getLogger(TranslationSynchController.class);
	@Autowired
	private SynchService synchService;

	@Value("${source.cache.server.url}")
	private String sourceCacheServerUrl;

	@Value("${translation.synch.git.flag}")
	private String translationSynchGitFlag;

	@Operation(summary = APIOperation.TRANSLATION_SYNC_VALUE, description = APIOperation.TRANSLATION_SYNC_NOTES)
	@RequestMapping(value = APIV2.PRODUCT_TRANSLATION_SYNC_PUT, method = RequestMethod.PUT, produces = { API.API_CHARSET })
	@ResponseStatus(HttpStatus.OK)
	public APIResponseDTO synchTranslation(@RequestBody UpdateTranslationDTO updateTranslationDTO,
			@PathVariable(APIParamName.PRODUCT_NAME) String productName,
			@PathVariable(APIParamName.VERSION) String version, HttpServletRequest request){
		ObjectMapper mapper = new ObjectMapper();
		String requestJson = "";
		try {
			requestJson = mapper.writeValueAsString(updateTranslationDTO);
		} catch (JsonProcessingException e) {
		   logger.error(e.getMessage(), e);
		}
		logger.info("The request content of updateTranslation is:\n " + requestJson);
		if (StringUtils.isEmpty(updateTranslationDTO) || StringUtils.isEmpty(updateTranslationDTO.getData())|| StringUtils.isEmpty(updateTranslationDTO.getData().getTranslation())) {
			return handleResponse(APIResponseStatus.BAD_REQUEST, "Data of updateTranslation is invalid!");
		}
		
		
		
		UpdateTranslationDataDTO updateTranslationDataDTO = updateTranslationDTO.getData();
		List<TranslationDTO> translationList = updateTranslationDataDTO.getTranslation();
		if (translationList == null || translationList.isEmpty()) {
			return handleResponse(APIResponseStatus.INTERNAL_SERVER_ERROR, "Inputting translation list is empty!");
		}
		if (StringUtils.isEmpty(updateTranslationDataDTO.getProductName())
				|| !updateTranslationDataDTO.getProductName().equals(productName)) {
			return handleResponse(APIResponseStatus.BAD_REQUEST, "Productname is incorrect!");
		}
		if (StringUtils.isEmpty(updateTranslationDataDTO.getVersion())
				|| !updateTranslationDataDTO.getVersion().equals(version)) {
			return handleResponse(APIResponseStatus.BAD_REQUEST, "Version is incorrect!");
		}
		
		
		logger.info("begin to translation convert");
		
		List<ComponentMessagesDTO> componentMessagesDTOList = new ArrayList<ComponentMessagesDTO>();
		for (TranslationDTO translationDTO : translationList) {
			ComponentMessagesDTO componentMessagesDTO = new ComponentMessagesDTO();
			componentMessagesDTO.setProductName(updateTranslationDataDTO.getProductName());
			componentMessagesDTO.setVersion(updateTranslationDataDTO.getVersion());
			componentMessagesDTO.setComponent(translationDTO.getComponent());
			componentMessagesDTO.setLocale(translationDTO.getLocale());
			componentMessagesDTO.setMessages(translationDTO.getMessages());
			componentMessagesDTOList.add(componentMessagesDTO);
		}
		
		logger.info("the synch translation component size"+componentMessagesDTOList.size());
		
		List<String> translationPaths = synchService.updateTranslationBatch(componentMessagesDTOList);
		if (translationPaths == null) {
			return handleResponse(APIResponseStatus.INTERNAL_SERVER_ERROR, "Update translation to disk failed!");
		} else {
			return sync2GitByL10n(productName, version, translationPaths);

		}
	}

	private APIResponseDTO handleResponse(Response response, Object data) {
		APIResponseDTO d = new APIResponseDTO();
		d.setData(data);
		response.setServerTime(LocalDateTime.now().toString());
		d.setResponse(response);
		if (logger.isDebugEnabled()) {
			String logOfResData = "The response data: " + d.getData().toString();
			logger.debug(logOfResData);
		}
		String rstr = "[response] " + response.toJSONString();
		logger.info(rstr);
		String endHandle = "[thread-" + Thread.currentThread().getId() + "] End to handle request.";
		logger.info(endHandle);
		return d;
	}

	private APIResponseDTO sync2GitByL10n(String productName, String version, List<String> translationPaths) {
		if (Boolean.parseBoolean(translationSynchGitFlag)) {
			SynchFile2GitReq sf2greq = new SynchFile2GitReq();
			sf2greq.setProductName(productName);
			sf2greq.setVersion(version);
			sf2greq.setItemNames(translationPaths);
			String url = sourceCacheServerUrl+L10NAPIV1.SYNC_TRANSLATION_GIT_L10N;
			logger.info("synch translation url----"+ url);
			try {
				Response resp = HttpsUtils.doPostWithObj2Obj(url, sf2greq, Response.class);
				if (resp.getCode().equals( APIResponseStatus.OK.getCode())) {
					return handleResponse(APIResponseStatus.OK, "Update translation sucessfully!");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
			}

			return handleResponse(APIResponseStatus.INTERNAL_SERVER_ERROR, "Update translation to git failed!");

		} else {
			return handleResponse(APIResponseStatus.OK, "Update translation sucessfully!");
		}

	}

}
