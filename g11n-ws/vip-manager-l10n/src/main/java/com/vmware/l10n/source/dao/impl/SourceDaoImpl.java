/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.dao.impl;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.vmware.l10n.record.dao.SqlLiteDao;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.source.dto.GRMAPIResponseStatus;
import com.vmware.l10n.source.dto.GRMResponseDTO;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import com.vmware.l10n.utils.MapUtil;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsFile;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPResourceOperationException;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.resourcefile.LocalJSONReader;
import com.vmware.vip.common.i18n.resourcefile.ResourceFilePathGetter;
import com.vmware.vip.common.utils.SortJSONUtils;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

@Repository
public class SourceDaoImpl implements SourceDao {
	private static Logger LOGGER = LoggerFactory.getLogger(SourceDaoImpl.class);
	@Autowired
	private SqlLiteDao sqlLite;
	
	@Override
	public String getFromBundle(SingleComponentDTO singleComponentDTO,
			String basepath) {
		String result = "";
		String component = singleComponentDTO.getComponent();
		if (StringUtils.isEmpty(component)) {
			component = ConstantsFile.DEFAULT_COMPONENT;
			singleComponentDTO.setComponent(component);
		}
		String filepath = ConstantsFile.L10N_BUNDLES_PATH
				+ ResourceFilePathGetter
						.getProductVersionConcatName(singleComponentDTO)
				+ ConstantsChar.BACKSLASH
				+ component
				+ ConstantsChar.BACKSLASH
				+ ResourceFilePathGetter
						.getLocalizedJSONFileName(ConstantsKeys.LATEST);
		LOGGER.info("Read content from file: {}{}",  basepath, filepath);

		if (new File(basepath + filepath).exists()) {
			result = new LocalJSONReader().readLocalJSONFile(basepath
					+ filepath);
		}
		return result;
	}

	
	@Override
	public boolean updateToBundle(ComponentMessagesDTO componentMessagesDTO,
			String basepath) {
		LOGGER.info("[updateLocalTranslationToFile]");
		String component = componentMessagesDTO.getComponent();
		if (StringUtils.isEmpty(component)) {
			component = ConstantsFile.DEFAULT_COMPONENT;
			componentMessagesDTO.setComponent(component);
		}
		String filepath = ConstantsFile.L10N_BUNDLES_PATH
				+ ResourceFilePathGetter
						.getProductVersionConcatName(componentMessagesDTO)
				+ ConstantsChar.BACKSLASH
				+ component
				+ ConstantsChar.BACKSLASH
				+ ResourceFilePathGetter
						.getLocalizedJSONFileName(componentMessagesDTO
								.getLocale());
		LOGGER.info("Read content from file: {}{}",  basepath, filepath);

		File targetFile = new File(basepath + filepath);
		if (targetFile.exists()) {
			LOGGER.info("The bunlde file path {}{} is found, update the bundle file.", basepath, filepath);
			try {
				SortJSONUtils.writeJSONObjectToJSONFile(basepath
						+ filepath, componentMessagesDTO);
				sqlLite.updateModifySourceRecord(componentMessagesDTO);
				return true;
			} catch (VIPResourceOperationException e) {
				LOGGER.error(e.getMessage(), e);
				return false;
			}
		} else {
			LOGGER.info("The bunlde file path {}{} is not found, cascade create the dir,add new bundle file ",  basepath, filepath);
			try {
				FileUtils.write(targetFile, "", "UTF-8", true);
				SortJSONUtils.writeJSONObjectToJSONFile(basepath
						+ filepath, componentMessagesDTO);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				return false;
			}
			
			sqlLite.createSourceRecord(componentMessagesDTO);
			
			return true;
		}
	}

	/*
	 * send the source the remoteServer
	 * @see com.vmware.l10n.source.dao.SourceDao#sendToGRM(java.lang.String, java.util.Map)
	 */
	@Override
	public boolean sendToRemote(String url, Map<String, Object> requestParam) {
		LOGGER.info("Send data to remote server [{}] ...", url);
		LOGGER.info("The request body is: {}", requestParam);
		boolean result = false;
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType
				.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		JSONObject jsonObj = new JSONObject(requestParam);
		HttpEntity<String> formEntity = new HttpEntity<String>(
				jsonObj.toString(), headers);
		try {
			ResponseEntity<GRMResponseDTO> responseEntity = restTemplate
					.postForEntity(url, formEntity, GRMResponseDTO.class);
			GRMResponseDTO gRMResponseDTO = responseEntity.getBody();
			if (gRMResponseDTO.getStatus() == GRMAPIResponseStatus.CREATED
					.getCode()) {
				result = true;
				LOGGER.info("The request has successed, the result: {} {}", gRMResponseDTO.getStatus(),  gRMResponseDTO.getResult());
			} else {
				LOGGER.info("The request has failed, the response code: {} reason: {}", + gRMResponseDTO.getStatus(), gRMResponseDTO.getErrorMessage());
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	/*
	 * merge the cache content with JSON bundle by component, the structure of
	 * componentJSON is same with the bundle file.
	 */
	@SuppressWarnings("unchecked")
	public ComponentMessagesDTO mergeCacheWithBundle(
			ComponentSourceDTO cachedComponentSourceDTO, String componentJSON) {
		ComponentMessagesDTO componentMessagesDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(cachedComponentSourceDTO, componentMessagesDTO);
		if (!StringUtils.isEmpty(componentJSON)) {
			JSONParser parser = new JSONParser();
			ContainerFactory containerFactory = MapUtil.getContainerFactory();
			Map<String, Object> messages = new LinkedHashMap<String, Object>();
			Map<String, Object> bundle = null;
			try {
				bundle = (Map<String, Object>) parser.parse(componentJSON,
						containerFactory);
			} catch (ParseException e) {
				LOGGER.error(e.getMessage(), e);
				
			}
			if ( (bundle != null) && !StringUtils.isEmpty(bundle)) {
				messages = (Map<String, Object>) bundle
						.get(ConstantsKeys.MESSAGES);
				Iterator<Map.Entry<String, Object>> it = cachedComponentSourceDTO
						.getMessages().entrySet().iterator();
				boolean isChanged = false ;
				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					String key = entry.getKey();
					String value = entry.getValue() == null ? "" : (String)entry.getValue();
					String v = messages.get(key) == null ? "" : (String)messages.get(key);
					if(!value.equals(v) && !isChanged) {
						isChanged = true;
					}
					MapUtil.updateKeyValue(messages, key, value);
				}
				if(isChanged) {
					componentMessagesDTO.setId(System.currentTimeMillis());
				} else {
					componentMessagesDTO.setId(Long.parseLong(bundle.get(ConstantsKeys.ID) == null ? "0" : bundle.get(ConstantsKeys.ID).toString()));
				}
				componentMessagesDTO.setMessages(messages);
			}
		}
		return componentMessagesDTO;
	}

}