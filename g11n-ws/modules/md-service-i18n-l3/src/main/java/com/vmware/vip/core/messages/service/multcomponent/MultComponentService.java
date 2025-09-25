/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.multcomponent;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.utils.PseudoConfig;
import com.vmware.vip.core.messages.utils.PseudoMessagesUtils;
import com.vmware.vip.messages.data.dao.api.IComponentChannelDao;
import com.vmware.vip.messages.data.dao.api.IMultComponentDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.model.ResultMessageChannel;

/**
 * This class handles the translation by single component.
 *
 */
@Service
public class MultComponentService implements IMultComponentService {

	private static Logger LOGGER = LoggerFactory.getLogger(MultComponentService.class);

	@Autowired
	private IMultComponentDao multipleComponentsDao;

	@Autowired
	private PseudoConfig pseudoConfig;

	@Autowired(required = false)
	private IComponentChannelDao componentChannelDao;

	@Override
	public TranslationDTO getMultiComponentsTranslation(TranslationDTO translationDTO) throws L3APIException {
		TranslationDTO result = null;
		try {
			result = this.getTranslation(translationDTO);
		} catch (JSONException e) {
			LOGGER.error(e.getMessage(), e);
			throw new L3APIException(ConstantsKeys.FATA_ERROR + "Parse error when get translation for "
					+ translationDTO.getProductName() + ConstantsChar.BACKSLASH + translationDTO.getVersion(), e);
		} catch (DataException e) {
			LOGGER.error(e.getMessage(), e);
			throw new L3APIException("Faild to get translation from data for " + translationDTO.getProductName()
					+ ConstantsChar.BACKSLASH + translationDTO.getVersion(), e);
		}
		// handle pseudo
		if (translationDTO.getPseudo()) {
			pseudoConfig.setEnabled(translationDTO.getPseudo());
			if (translationDTO.getBundles().isEmpty()){
				throw new L3APIException("Faild to get translation from data for " + translationDTO.getProductName()
						+ ConstantsChar.BACKSLASH + translationDTO.getVersion());
			}
			return PseudoMessagesUtils.getPseudoMessages2(result, pseudoConfig);
		}
		return result;
	}

	@SuppressWarnings({ "unchecked" })
	private TranslationDTO getTranslation(TranslationDTO translationDTO) throws JSONException, DataException {
		List<String> locales = translationDTO.getLocales();
		List<String> components = translationDTO.getComponents();
		List<String> bundles = multipleComponentsDao.get2JsonStrs(translationDTO.getProductName(),
				translationDTO.getVersion(), components, locales);
		JSONArray ja = new JSONArray();
		for (int i = 0; i < bundles.size(); i++) {
			String s = (String) bundles.get(i);
			if (s.equalsIgnoreCase("")) {
				continue;
			}
			JSONObject jo = new JSONObject(s);
			ja.put(jo);
		}
		translationDTO.setBundles(ja);
		return translationDTO;
	}

	@Override
	public List<ResultMessageChannel> getTranslationChannels(String productName, String version,
			List<String> components, List<String> locales) throws L3APIException {
		
		try {
			return componentChannelDao.getTransReadableByteChannels(productName, version, components, locales);
		} catch (DataException e) {
			LOGGER.error(e.getMessage(), e);
			throw new L3APIException(
					"Faild to get translation from data for " + productName + ConstantsChar.BACKSLASH + version, e);
		
		}
	}

	
}
