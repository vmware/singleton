/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.string;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsMsg;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.i18n.dto.StringBasedDTO;
import com.vmware.vip.common.utils.LocaleUtils;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.core.messages.service.singlecomponent.IOneComponentService;
import com.vmware.vip.core.messages.utils.PseudoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the translation by String.
 *
 */
@Service
public class StringService implements IStringService {
	private static Logger logger = LoggerFactory.getLogger(StringService.class);
	@Autowired
	private PseudoConfig pseudoConfig;
	@Autowired
	private IOneComponentService singleComponentService;

	/**
	 * Get translation of one string.
	 * <p>
	 * If the translation is cached, get it directly, otherwise get it from
	 * local bundle.
	 *
	 * @param componentMessagesDTO
	 *            the object of ComponentMessagesDTO.
	 * @param key
	 *            The unique identify for source in component's resource file.
	 * @param source
	 *            The English string which need translate.
	 * @return TranslationDTO a DTO object of StringBasedDTO, containing
	 *         translation.
	 */
	@Override
	public StringBasedDTO getStringTranslation(
            final ComponentMessagesDTO comDTO, String key, String source)
			throws L3APIException {
		ComponentMessagesDTO enComDTO = createComponentDTO(comDTO,
				ConstantsUnicode.EN, ConstantsKeys.FALSE);

		String translation = "", enString = "";

		// get translation
		try {
			translation = getString(comDTO, key);
		} catch (L3APIException e) {
			logger.error(e.getMessage(), e);
		}

		// get en-US string
		try {
			enString = getString(enComDTO, key);
		} catch (L3APIException e) {
			logger.warn(e.getMessage(), e);
		}

		StringBasedDTO strDTO = createStringDTO(comDTO, key, source,
				translation);

		// update the translation if the translation is not found.
		if (StringUtils.isEmpty(translation)
				&& LocaleUtils.isDefaultLocale(strDTO.getLocale()) == false) {
			if (StringUtils.isEmpty(enString)) {
				strDTO.setTranslation(source);
				strDTO.setStatus(String.format(ConstantsMsg.TRANS_NOT_EN_NOT, ConstantsMsg.TRANS_IS_NOT_FOUND));
			} else {
				strDTO.setTranslation(enString);
				strDTO.setStatus(String.format(ConstantsMsg.TRANS_NOTFOUND_EN_FOUND, ConstantsMsg.TRANS_IS_NOT_FOUND));
			}
		} else {
			if((source!= null)&& source.equals(enString)) {
				strDTO.setStatus(String.format(ConstantsMsg.TRANS_FOUND_RETURN, ConstantsMsg.TRANS_IS_FOUND));
			} else {
				strDTO.setStatus(ConstantsMsg.SOURCE_IS_NOT_PROVIDE);
				
			}
		}

		// update the translation if the source is not empty.
		if (!StringUtils.isEmpty(source) && !isSourceEqualToEnString(source, enString)
					&& (!comDTO.getPseudo())) {
				strDTO.setTranslation(source);
				strDTO.setStatus(String.format(ConstantsMsg.TRANS_NOTFOUND_NOTLATEST, ConstantsMsg.TRANS_IS_NOT_FOUND));
		}

		// update the translation for pseudo
		if (comDTO.getPseudo()) {
			if (StringUtils.isEmpty(translation)) {
				String tag = pseudoConfig.getNotExistSourceTag();
				strDTO.setTranslation(tag + source + tag);
				strDTO.setStatus(String.format(ConstantsMsg.PSEUDO_NOTFOUND, ConstantsMsg.TRANS_IS_NOT_FOUND));
			} else {
				if (translation.startsWith("@") || translation.startsWith("#")) {
					;
				} else {
					String tag = pseudoConfig.getExistSourceTag();
					strDTO.setTranslation(tag + source + tag);
				}
				strDTO.setStatus(String.format(ConstantsMsg.PSEUDO_FOUND, ConstantsMsg.TRANS_IS_FOUND));
			}
		}
		return strDTO;
	}

	// judge if the source is equal to enUSString.
	private boolean isSourceEqualToEnString(String source, String enString)
			throws L3APIException {
		boolean isEqual = false;
		if(source != null) {
			try {
				String prettyEnUSStr = new ObjectMapper()
						.writerWithDefaultPrettyPrinter().writeValueAsString(
								enString);
				String prettySource = "\"" + source + "\"";
				if ((prettySource).equals(prettyEnUSStr) || source.equals(enString)) {
					isEqual = true;
				}
			} catch (JsonProcessingException e) {
				throw new L3APIException(
						ConstantsKeys.FATA_ERROR + "Error occurs when perform writeValueAsString ", e);
			}
		}
		return isEqual;
	}

	/*
	 * create a ComponentMessagesDTO from a JSON string and update the fields:
	 * locale, pseudo. Currently it's used for getting en-US bundle to compare
	 * with received source.
	 */
	private ComponentMessagesDTO createComponentDTO(
            final ComponentMessagesDTO componentMessagesDTO, String locale,
            String pseudo) {
		ComponentMessagesDTO newComponentMessagesDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(componentMessagesDTO, newComponentMessagesDTO);
		newComponentMessagesDTO.setLocale(locale);
		newComponentMessagesDTO.setPseudo(Boolean.parseBoolean(pseudo));
		return newComponentMessagesDTO;
	}

	/*
	 * get the translation from bundle.
	 */
	@SuppressWarnings("rawtypes")
	private String getString(ComponentMessagesDTO componentMsgDTOWithParams,
                             String key) throws L3APIException {
		String strTranslation = "";
		ComponentMessagesDTO componentMsgDTOWithData = singleComponentService
				.getComponentTranslation(componentMsgDTOWithParams);
		Object messages = componentMsgDTOWithData.getMessages();
		if (!StringUtils.isEmpty(messages)) {
			strTranslation = (String) ((Map) messages).get(key);
		}
		return strTranslation;
	}

	/*
	 * convert component-base DTO to string-base DTO as the response object.
	 */
	private StringBasedDTO createStringDTO(
            ComponentMessagesDTO componentMessagesDTO, String key,
            String source, String translation) {
		StringBasedDTO stringBasedDTO = new StringBasedDTO();
		BeanUtils.copyProperties(componentMessagesDTO, stringBasedDTO);
		stringBasedDTO.setKey(key);
		stringBasedDTO.setSource(source);
		stringBasedDTO.setTranslation(translation);
		return stringBasedDTO;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SingleComponentDTO getMultKeyTranslation(ComponentMessagesDTO compMsg, String[] keyArr)
			throws L3APIException {

		ComponentMessagesDTO componentMsgDTOWithData = singleComponentService.getComponentTranslation(compMsg);
		Object messages = componentMsgDTOWithData.getMessages();
		if (!StringUtils.isEmpty(messages)) {

			Map<String, Object> msgMap = new HashMap<String, Object>();
			for (String key : keyArr) {
				Object value = (String) ((Map) messages).get(key);
				if (value != null) {
					msgMap.put(key, value);
				}

			}
			componentMsgDTOWithData.setMessages(msgMap);

			return componentMsgDTOWithData;
		} else {
			return null;
		}
	}
}
