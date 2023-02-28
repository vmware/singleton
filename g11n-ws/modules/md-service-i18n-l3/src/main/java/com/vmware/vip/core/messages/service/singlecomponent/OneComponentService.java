/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.singlecomponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.CachedKeyGetter;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.TranslationQueryStatusType;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.utils.LocaleUtility;
import com.vmware.vip.core.messages.utils.PseudoConfig;
import com.vmware.vip.core.messages.utils.PseudoMessagesUtils;
import com.vmware.vip.messages.data.dao.api.IOneComponentDao;
import com.vmware.vip.messages.data.dao.exception.DataException;

/**
 * This class handles the translation by single component.
 *
 */
@Service
public class OneComponentService implements IOneComponentService {

	private static Logger LOGGER = LoggerFactory.getLogger(OneComponentService.class);

	@Autowired
	IProductService productService;

	@Resource
	private IOneComponentDao oneComponentDao;

	@Autowired
	private PseudoConfig pseudoConfig;

	/**
	 * Get the translation of a component from cache and disk/db
	 * <p>
	 * If the translation is cached, get it directly; otherwise will get it from
	 * local bundle.
	 *
	 * @param componentMessagesDTO
	 *            the object of ComponentMessagesDTO, containing component's
	 *            information for translate.
	 * @return ComponentMessagesDTO the object of ComponentMessagesDTO,
	 *         containing translation.
	 */
	@Override
	public ComponentMessagesDTO getComponentTranslation(
			ComponentMessagesDTO componentMessagesDTO) throws L3APIException {
		ComponentMessagesDTO result = null;
		final String inputLocale = componentMessagesDTO.getLocale();
		if (componentMessagesDTO.getPseudo()) {
			componentMessagesDTO.setLocale(ConstantsKeys.LATEST);
		} else {
			String fallbackLocale = getFallbackLocale(
					componentMessagesDTO.getProductName(),
					componentMessagesDTO.getVersion(),
					componentMessagesDTO.getLocale());
			componentMessagesDTO.setLocale(fallbackLocale);
		}
		String key = CachedKeyGetter
				.getOneCompnentCachedKey(componentMessagesDTO);
		try {
			result =  TranslationCache3.getCachedObject(
					CacheName.ONECOMPONENT, key, ComponentMessagesDTO.class);
			if (StringUtils.isEmpty(result) || StringUtils.isEmpty(result.getMessages()) || StringUtils.isEmpty(result.getComponent())) {
				LOGGER.info("Get data from local, since it's not found in the cache.");
				result = this.getTranslationFromDisk(componentMessagesDTO);
				result.setDataOrigin(ConstantsKeys.BUNDLE);
				if (!componentMessagesDTO.getPseudo()) {
					if(LOGGER.isDebugEnabled()) {
						String msg = "The result from disk is: {}" + result.toString();
						LOGGER.debug(msg);
					}
					TranslationCache3.addCachedObject(CacheName.ONECOMPONENT,
							key, ComponentMessagesDTO.class, result);
				}
			} else {
				result.setDataOrigin(ConstantsKeys.CACHE);
				LOGGER.info("Found data from cache[" + key + "].");
				if(LOGGER.isDebugEnabled()) {
					String msg = "The result from cache is: {}" + result.toString();
					LOGGER.debug(msg);
				}
			}
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
			throw new L3APIException(ConstantsKeys.FATA_ERROR + "Parse json failed.");
		} catch (DataException e) {
			LOGGER.error(e.getMessage(), e);
			throw new L3APIException("Failed to get translation from data for "
					+ componentMessagesDTO.getProductName()
					+ ConstantsChar.BACKSLASH
					+ componentMessagesDTO.getVersion());
		} catch (VIPCacheException e) {
			LOGGER.error(e.getMessage(), e);
			throw new L3APIException("Failed to get translation from data for "
					+ componentMessagesDTO.getProductName()
					+ ConstantsChar.BACKSLASH
					+ componentMessagesDTO.getVersion());
		}
		pseudoConfig.setEnabled(componentMessagesDTO.getPseudo());
		ComponentMessagesDTO resultComponentMessagesDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(
				PseudoMessagesUtils.getPseudoMessages(result, pseudoConfig),
				resultComponentMessagesDTO);
		resultComponentMessagesDTO.setPseudo(componentMessagesDTO.getPseudo());
		//resultComponentMessagesDTO.setLocale(inputLocale);
		return resultComponentMessagesDTO;
	}

	/*
	 * get the fallbacked locale for specified product and version.
	 */
	private String getFallbackLocale(String productName, String version,
			String inputLocale) throws L3APIException {
		List<String> supportedLocaleList = productService
				.getSupportedLocaleList(productName, version);
		List<Locale> supportedLocales = new ArrayList<Locale>();
		for (String supportedLocale : supportedLocaleList) {
			supportedLocale = supportedLocale.replace("_", "-");
			supportedLocales.add(Locale.forLanguageTag(supportedLocale));
		}
		String requestLocale = inputLocale.replace("_", "-");
		Locale fallbackLocale = LocaleUtility.pickupLocaleFromListNoDefault(
				supportedLocales, Locale.forLanguageTag(requestLocale));
		return fallbackLocale.toLanguageTag();
	}

	/**
	 * Get translation from local running environment, it maybe a jar, maybe a
	 * war
	 * 
	 * @param componentMessagesDTO
	 * @return ComponentMessagesDTO object
	 * @throws DataException
	 * @see com.vmware.vip.core.translation.dao.BaseComponentDao#getTranslation(java.lang.Object)
	 */
	public ComponentMessagesDTO getTranslationFromDisk(
			ComponentMessagesDTO componentMessagesDTO) throws ParseException,
			DataException {
		String result = oneComponentDao.get2JsonStr(
				componentMessagesDTO.getProductName(),
				componentMessagesDTO.getVersion(),
				componentMessagesDTO.getComponent(),
				componentMessagesDTO.getLocale());
		if (StringUtils.isEmpty(result)) {
			componentMessagesDTO.setMessages(result);
			componentMessagesDTO
					.setStatus(TranslationQueryStatusType.ComponentNotFound
							.toString());
			return componentMessagesDTO;
		}
		// get the message ordered
		SingleComponentDTO caseComponentMessagesDTO = new SingleComponentDTO();
		caseComponentMessagesDTO = SingleComponentDTO
				.getSingleComponentDTOWithLinkedMessages(result);
		caseComponentMessagesDTO.setProductName(componentMessagesDTO
				.getProductName());
		caseComponentMessagesDTO.setVersion(componentMessagesDTO.getVersion());
		caseComponentMessagesDTO.setStatus(componentMessagesDTO.getStatus());
		ComponentMessagesDTO msgDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(caseComponentMessagesDTO, msgDTO);
		return msgDTO;
	}

}
