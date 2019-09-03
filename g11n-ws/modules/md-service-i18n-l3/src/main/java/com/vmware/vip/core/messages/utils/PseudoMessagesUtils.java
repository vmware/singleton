/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.utils;

import java.util.Map;

/*import org.json.simple.JSONArray;
import org.json.simple.JSONObject;*/
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.utils.JSONUtils;
import com.vmware.vip.core.messages.service.multcomponent.TranslationDTO;

/**
 * A helper Util to add tag "@@" for product component DTO
 */
public class PseudoMessagesUtils {

	/**
	 * Add tag "@@" for the messages attribute value of SingleComponentDTO
	 *
	 * @param singleComponentDTO
	 *            The component DTO
	 * @param pseudoConfig
	 *            A config Object for addition tag,it's init according to the
	 *            "application.properties"
	 */
	@SuppressWarnings("rawtypes")
	public static SingleComponentDTO getPseudoMessages(
			SingleComponentDTO singleComponentDTO, PseudoConfig pseudoConfig) {
		SingleComponentDTO pseudoSingleComponentDTO = new SingleComponentDTO();
		BeanUtils.copyProperties(singleComponentDTO, pseudoSingleComponentDTO);
		if (!StringUtils.isEmpty(pseudoSingleComponentDTO.getMessages())) {
			Map messages = (Map) pseudoSingleComponentDTO.getMessages();
			if (pseudoConfig.isEnabled()) {
				pseudoSingleComponentDTO.setMessages(JSONUtils
						.getOrderedMapForPseudo(messages,
								pseudoConfig.getExistSourceTag()));
			}
		}
		return pseudoSingleComponentDTO;
	}

	@SuppressWarnings({ "rawtypes" })
	public static TranslationDTO getPseudoMessages2(
			TranslationDTO translationDTO, PseudoConfig pseudoConfig) {
		TranslationDTO t = new TranslationDTO();
		BeanUtils.copyProperties(translationDTO, t);
		JSONArray pseudoList = new JSONArray();
		JSONArray ja = translationDTO.getBundles();
		for(int i=0; i<ja.size(); i++) {
			if (pseudoConfig.isEnabled()) {
			JSONObject jo = ja.getJSONObject(i);
			Map messages = (Map) jo.get(ConstantsKeys.MESSAGES);
			jo.put(ConstantsKeys.MESSAGES,
					JSONUtils.getOrderedMapForPseudo(messages,
							pseudoConfig.getExistSourceTag()));
			pseudoList.add(jo);
			}
		}
		t.setBundles(pseudoList);
		return t;
	}
}
