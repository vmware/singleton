/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.utils;

import java.util.Iterator;
import java.util.Map;

import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import com.vmware.l10n.source.dto.SourceAPIResponseDTO;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;

public class SourceUtils {
	private static Logger logger = LoggerFactory.getLogger(SourceUtils.class);

	private SourceUtils() {}
	
	/*
	 * create a StringSourceDTO object.
	 */
	public static StringSourceDTO createSourceDTO(String productName, String version, String component, String locale, String key, String source, String commentForSource,  String sourceFormat){
		StringSourceDTO stringSourceDTO = new StringSourceDTO();
		stringSourceDTO.setProductName(productName);
		stringSourceDTO.setComponent(component);
		stringSourceDTO.setVersion(version);
		stringSourceDTO.setLocale(locale);
		stringSourceDTO.setKey(key);
		stringSourceDTO.setSource(source);
		stringSourceDTO.setComment(commentForSource);
		stringSourceDTO.setSourceFormat(sourceFormat);
		return stringSourceDTO;
	}

	public static SourceAPIResponseDTO handleSourceResponse(boolean isSourceCached){
		SourceAPIResponseDTO sourceAPIResponseDTO = new SourceAPIResponseDTO();
		if (isSourceCached) {
			sourceAPIResponseDTO.setResponse(APIResponseStatus.TRANSLATION_COLLECT_REQUEST_SUCCESS);
		} else {
			sourceAPIResponseDTO.setResponse(APIResponseStatus.TRANSLATION_COLLECT_FAILURE);
		}
		return sourceAPIResponseDTO;
	}

	/*
	 * merge the cache content with JSON bundle by component, the structure of
	 * componentJSON is same with the bundle file.
	 */
	@SuppressWarnings("unchecked")
	public static SingleComponentDTO mergeCacheWithBundle(
			final SingleComponentDTO cachedComponentSourceDTO, final String componentJSON) {
		ComponentMessagesDTO componentMessagesDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(cachedComponentSourceDTO, componentMessagesDTO);
		if (!StringUtils.isEmpty(componentJSON)) {
			JSONParser parser = new JSONParser();
			ContainerFactory containerFactory = MapUtil.getContainerFactory();
			Map<String, Object> messages;
			Map<String, Object> bundle = null;
			try {
				bundle = (Map<String, Object>) parser.parse(componentJSON,
						containerFactory);
			} catch (ParseException e) {
				logger.error(e.getMessage(), e);
				
			}
			if (bundle != null && !bundle.isEmpty()) {
				messages = (Map<String, Object>) bundle.get(ConstantsKeys.MESSAGES);
				Iterator<Map.Entry<String, Object>> it = ((Map<String, Object>)cachedComponentSourceDTO
						.getMessages()).entrySet().iterator();
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


