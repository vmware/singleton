/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.translation.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.l10n.translation.dao.SingleComponentDao;
import com.vmware.l10n.translation.dto.ComponentMessagesDTO;
import com.vmware.l10n.translation.service.TranslationSyncServerService;
import com.vmware.l10n.utils.MapUtil;
import com.vmware.vip.common.constants.TranslationQueryStatusType;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO;
import com.vmware.vip.common.i18n.dto.UpdateTranslationDTO.UpdateTranslationDataDTO.TranslationDTO;
import com.vmware.vip.common.l10n.exception.L10nAPIException;

/**
 * This class synchronizes the latest translation for vIP server.
 *
 */
@Service
public class TranslationSyncServerServiceImpl implements TranslationSyncServerService {
    private static Logger LOGGER = LoggerFactory.getLogger(TranslationSyncServerServiceImpl.class);

    @Autowired
    private SingleComponentDao singleComponentDao;

    /**
     * Synchronize the translation in the componentMessagesDTO to the local bundle and cache.
     *
     * @param componentMessagesDTO
     *        The object of ComponentMessagesDTO, containing the latest translation.
     * @return boolean
     *         Sync successfully, return true, otherwise return false.
     * @throws JsonProcessingException 
     * 
     */
	private boolean updateTranslation(ComponentMessagesDTO componentMessagesDTO) throws L10nAPIException, JsonProcessingException {
		LOGGER.info("Start Update transaltion for: ["+componentMessagesDTO.getProductName()+"], ["+componentMessagesDTO.getVersion()+"], ["+componentMessagesDTO.getComponent()+"], ["+componentMessagesDTO.getLocale()+"]");

		if (!singleComponentDao.lockFile(componentMessagesDTO)) {
			return false;
		}

		try {
			// merge with local bundle file
			componentMessagesDTO = mergeComponentMessagesDTOWithFile(componentMessagesDTO);
			// update the local bundle file
			LOGGER.info("Update the local bundle file");
			boolean flag = singleComponentDao.writeTranslationToFile(componentMessagesDTO);
			LOGGER.info("End of Update transaltion");
			return flag;
		} finally {
			singleComponentDao.unlockFile(componentMessagesDTO);
		}
	}

	/**
	 * Batch synchronization.
	 *
	 * @param componentMessagesDTOList
	 *        The list of ComponentMessagesDTO.
	 * @return List<TranslationDTO>
	 *         The list of Update failed.
	 * @throws JsonProcessingException
	 *
	 */
	@Override
	public List<TranslationDTO> updateBatchTranslation(List<ComponentMessagesDTO> componentMessagesDTOList) throws L10nAPIException, JsonProcessingException{
		List<TranslationDTO> translationDTOList = new ArrayList<>();
		for(ComponentMessagesDTO componentMessagesDTO : componentMessagesDTOList){
			if(!updateTranslation(componentMessagesDTO)){
				TranslationDTO translationDTO = new UpdateTranslationDTO.UpdateTranslationDataDTO.TranslationDTO();
				translationDTO.setComponent(componentMessagesDTO.getComponent());
				translationDTO.setLocale(componentMessagesDTO.getLocale());
				translationDTO.setMessages((Map)componentMessagesDTO.getMessages());
				translationDTOList.add(translationDTO);
			}
		}
		return translationDTOList;
	}

	/**
	 * Merge the translation in the componentMessagesDTO and in the local bundle.
	 *
	 * @param componentMessagesDTO
	 *        the object of ComponentMessagesDTO, containing the latest translation.
	 * @return ComponentMessagesDTO
	 *         a DTO object of ComponentMessagesDTO, containing the all translation.
	 */
	private ComponentMessagesDTO mergeComponentMessagesDTOWithFile(ComponentMessagesDTO componentMessagesDTO) throws L10nAPIException {
		ComponentMessagesDTO paramComponentMessagesDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(componentMessagesDTO, paramComponentMessagesDTO);
		ComponentMessagesDTO result = singleComponentDao.getTranslationFromFile(paramComponentMessagesDTO);
		if(!StringUtils.isEmpty(result) && !StringUtils.isEmpty(result.getStatus()) && result.getStatus().equals("Translation"+TranslationQueryStatusType.FileFound)){
			Object messageObj = result.getMessages();
			if (!StringUtils.isEmpty(messageObj)) {
				Map<String,Object> messages = (Map<String, Object>) messageObj;
				Iterator<Map.Entry<String, Object>> it = ((Map)componentMessagesDTO.getMessages()).entrySet()
						.iterator();
				boolean isChanged = false;
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
					componentMessagesDTO.setId(result.getId());
				}
				componentMessagesDTO.setMessages(messages);
				return componentMessagesDTO;
			}else{
				return componentMessagesDTO;
			}
		}else{
			return componentMessagesDTO;
		}
	}

	@Override
	public void saveCreationInfo(UpdateTranslationDTO updateTranslationDTO) {
		singleComponentDao.saveCreationInfo(updateTranslationDTO);
	}
}
