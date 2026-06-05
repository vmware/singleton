/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service.impl;

import com.vmware.l10n.source.dto.GRMAPIResponseStatus;
import com.vmware.l10n.source.dto.GRMResponseDTO;
import com.vmware.l10n.source.service.SourceService;
import com.vmware.l10n.utils.MapUtil;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.source.service.RemoteSyncService;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.Map;

/**
 * This implementation of interface SourceService.
 */
@Service
public class RemoteSyncServicempl implements RemoteSyncService {

	private static Logger logger = LoggerFactory.getLogger(RemoteSyncServicempl.class);

	@Autowired
	private SourceDao sourceDao;

	public void ping(String remoteURL) throws L10nAPIException {
		ComponentSourceDTO componentSourceDTO = new ComponentSourceDTO();
		componentSourceDTO.setProductName("test");
		componentSourceDTO.setVersion("test");
		componentSourceDTO.setLocale("test");
		componentSourceDTO.setComponent("test");
		componentSourceDTO.setComments("", "");
		componentSourceDTO.setMessages("", "");
		send(componentSourceDTO, remoteURL);
	}

	/*
	 * synchronize the updated source GRM if the switch is on.
	 */
	@SuppressWarnings("unchecked")
	public void send(ComponentSourceDTO componentSourceDTO, String remoteURL)
			throws L10nAPIException {
		boolean pushFlag = false;
		if (!StringUtils.isEmpty(componentSourceDTO)) {
			StringBuilder url = new StringBuilder();
			url.append(remoteURL).append(
					L10NAPIV1.GRM_SEND_SOURCE
							.replace("{" + APIParamName.PRODUCT_NAME + "}",
									componentSourceDTO.getProductName())
							.replace("{" + APIParamName.VERSION + "}",
									componentSourceDTO.getVersion())
							.replace(
									"{" + APIParamName.COMPONENT + "}",
									componentSourceDTO.getComponent()).replace(
											"{" + APIParamName.LOCALE + "}",
											ConstantsUnicode.EN));
			JSONObject requestParam = new JSONObject();
			requestParam.put(ConstantsKeys.MESSAGES,
					componentSourceDTO.getMessages());
			requestParam.put(ConstantsKeys.COMMENTS,
					componentSourceDTO.getComments());
			requestParam.put(ConstantsKeys.CONTENT_TYPES,
					componentSourceDTO.getSourceFormats());
			pushFlag = sendToRemote(url.toString(), requestParam.toMap());
		}
		if (!pushFlag) {
			throw new L10nAPIException("Error occur when send to remote ["
					+ remoteURL + "].");
		}
	}

	/**
	 * Send source strings to GRM by component.
	 *
	 * @param url          the URL of register strings API provided by GRM
	 * @param requestParam the request body, it includes 'messages' and 'comments',
	 *                     the former represents source strings and the latter
	 *                     represents comments for source strings
	 * @return send result, true represents success, false represents failure.
	 */
	public boolean sendToRemote(String url, Map<String, Object> requestParam) {
		logger.info("Send data to remote server [{}] ...", url);
		logger.info("The request body is: {}", requestParam);
		boolean result = false;
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		JSONObject jsonObj = new JSONObject(requestParam);
		HttpEntity<String> formEntity = new HttpEntity<String>(jsonObj.toString(), headers);
		try {
			ResponseEntity<GRMResponseDTO> responseEntity = restTemplate.postForEntity(url, formEntity,
					GRMResponseDTO.class);
			GRMResponseDTO gRMResponseDTO = responseEntity.getBody();
			if (gRMResponseDTO == null) {
				return false;
			}
			if (gRMResponseDTO.getStatus() == GRMAPIResponseStatus.CREATED.getCode()) {
				result = true;
				logger.info("The request has succeeded, the result: {} {}", gRMResponseDTO.getStatus(),
						gRMResponseDTO.getResult());
			} else {
				logger.error("The request has failed, the response code: {} reason: {}", +gRMResponseDTO.getStatus(),
						gRMResponseDTO.getErrorMessage());
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}


}
