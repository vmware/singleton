/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.mt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.vmware.vip.messages.mt.MTFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.CachedKeyGetter;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.core.messages.exception.L3APIException;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.core.messages.service.singlecomponent.IOneComponentService;
import com.vmware.vip.common.i18n.dto.StringBasedDTO;
import com.vmware.vip.messages.data.dao.api.IMTProcessor;
import com.vmware.vip.messages.data.dao.api.IOneComponentDao;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.data.dao.exception.MTException;
import com.vmware.vip.messages.mt.MTConfig;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the translation by single component.
 *
 */
@Service
public class MTService implements IMTService {

	private static Logger LOGGER = LoggerFactory.getLogger(MTService.class);

	@Autowired
	private IOneComponentDao oneComponentDao;

	@Autowired
	IOneComponentService oneComponentService;

	/**
	 * Get component MT translation
	 *
	 */
	@SuppressWarnings("unchecked")
	public ComponentMessagesDTO getComponentMTTranslation(
			ComponentMessagesDTO componentMessagesDTO) throws L3APIException {
		String key = this.getCachedKey(componentMessagesDTO);
		LOGGER.info("get MT translation for " + key);
		String toLang = componentMessagesDTO.getLocale();
		ComponentMessagesDTO enComponentMessagesDTO = new ComponentMessagesDTO();
		ComponentMessagesDTO resultComponentMessagesDTO = new ComponentMessagesDTO();
		BeanUtils.copyProperties(componentMessagesDTO, enComponentMessagesDTO);
		BeanUtils.copyProperties(componentMessagesDTO,
				resultComponentMessagesDTO);
		ComponentMessagesDTO latestDTO = null;
		// get the latest.json from local disk and get its MT from cache
		// if component MT DTO exist in cache then get it and return;
		// else request for the component MT from MT server then put it to
		// cache.
		try {
			enComponentMessagesDTO.setLocale(ConstantsKeys.LATEST);
			latestDTO = oneComponentService
					.getTranslationFromDisk(enComponentMessagesDTO);
			ComponentMessagesDTO cachedDTO =  TranslationCache3.getCachedObject(CacheName.MT, key, ComponentMessagesDTO.class);
			if (cachedDTO != null
					&& !StringUtils.isEmpty(cachedDTO.getMessages())) {
				if (latestDTO != null
						&& !StringUtils.isEmpty(latestDTO.getMessages())) {
					int cacheSize = ((Map<String, String>) cachedDTO
							.getMessages()).size();
					int diskSize = ((Map<String, String>) latestDTO
							.getMessages()).size();
					if (cacheSize == diskSize) {
						cachedDTO.setDataOrigin(ConstantsKeys.CACHE + "(" + ConstantsKeys.MT + ")");
						return cachedDTO;
					}
				}
			}
			if (latestDTO != null && latestDTO.getMessages() != null) {
				Map<String, Object> messages = (Map<String, Object>) latestDTO
						.getMessages();
				OrderedKV kv = new OrderedKV(messages);
				List<String> sourceList = kv.getValues();
				List<String> mtResult = new ArrayList<String>();
				IMTProcessor mtProcessor = MTFactory.getMTProcessor();
				// Since Azure has limitation(max 25) to the array size of source, so we need to handle it.
				int translatedCount = new Integer(MTConfig.TRANSLATECOUNT).intValue(), fromIndex = 0, toIndex = translatedCount;
				if (sourceList.size() < translatedCount) {
					mtResult = mtProcessor.translateArray(ConstantsUnicode.EN,
							toLang, sourceList);
				} else {
					for (; fromIndex < sourceList.size();) {
						mtResult.addAll(mtProcessor.translateArray(
								ConstantsUnicode.EN, toLang,
								sourceList.subList(fromIndex, toIndex)));
						fromIndex = toIndex;
						toIndex = (toIndex + translatedCount) > sourceList
								.size() ? sourceList.size()
								: (toIndex + translatedCount);
					}
				}

				kv.setValues(mtResult);
				resultComponentMessagesDTO.setMessages(this.getMapFromKV(kv));
				// add MT to cache
				try {
					TranslationCache3.addCachedObject(CacheName.MT, key,
							ComponentMessagesDTO.class, resultComponentMessagesDTO);
				} catch (VIPCacheException e) {
					e.printStackTrace();
				}
			}
		} catch (ParseException | DataException | MTException
				| VIPCacheException e) {
			
			LOGGER.error(e.getMessage(), e);
			throw new L3APIException(e.getMessage());
		}
		resultComponentMessagesDTO.setDataOrigin(ConstantsKeys.MT);
		return resultComponentMessagesDTO;
	}

	private Map<String, String> getMapFromKV(OrderedKV kv) {
		Map<String, String> m = new HashMap<String, String>();
		List<String> keys = kv.getKeys();
		List<String> values = kv.getValues();
		for (int i = 0; i < keys.size(); i++) {
			m.put(keys.get(i), values.get(i));
		}
		return m;
	}

	
	/**
	 * 
	 * @param key
	 * @param source
	 * @param en_key
	 * @param enDTO
	 * @throws VIPCacheException
	 * // store key and source to cache:
	 * // 1. if the key and source exist in cache, then update cache
	 *	// 2. if not exist in cache, create a new key and source
	 */
	@SuppressWarnings("unchecked")
	private void storeKeySource2Cache(String key, String source, String en_key, ComponentMessagesDTO enDTO) throws VIPCacheException{
		
		Map<String, String> p = null;
		if (TranslationCache3.getCachedObject(CacheName.MTSOURCE, en_key, ComponentMessagesDTO.class) != null) {
			ComponentMessagesDTO cacheENDTO =  TranslationCache3
					.getCachedObject(CacheName.MTSOURCE, en_key, ComponentMessagesDTO.class);
			if (!StringUtils.isEmpty(cacheENDTO.getMessages())) {
				p = (Map<String, String>) cacheENDTO.getMessages();
				p.put(key, source);
				cacheENDTO.setMessages(p);
				TranslationCache3.addCachedObject(CacheName.MTSOURCE,
						en_key, ComponentMessagesDTO.class, cacheENDTO);
			}
		} else {
			Map<String, String> m = new HashMap<String, String>();
			m.put(key, source);
			enDTO.setMessages(m);
			TranslationCache3.addCachedObject(CacheName.MTSOURCE, en_key,
					ComponentMessagesDTO.class, enDTO);
		}

	} 
	
	       
	
	
	
	
	
	@SuppressWarnings("unchecked")
	    private String getObjByMT(String com_key, String key, final ComponentMessagesDTO comDTO, String source, String mtTranslationPara ) throws MTException, VIPCacheException {
		// Component object exists in cache:
					// 1. get the component object from cache
					// 2. create or update MT string to component object
					// 3. update component object to cache
		         String mtTranslation = mtTranslationPara;
		         Map<String, String> cachedMTMap = null;
		         IMTProcessor mtProcessor = MTFactory.getMTProcessor();
					if (TranslationCache3.getCachedObject(CacheName.MTSOURCE, com_key,ComponentMessagesDTO.class) != null) {
						ComponentMessagesDTO cacheComDTO =  TranslationCache3
								.getCachedObject(CacheName.MTSOURCE, com_key, ComponentMessagesDTO.class);
						cachedMTMap = (Map<String, String>) cacheComDTO.getMessages();
						
						
						if (cachedMTMap != null && cachedMTMap.containsKey(key)) {
							mtTranslation = cachedMTMap.get(key);
						} else {
							mtTranslation = mtProcessor.translateString(
									ConstantsUnicode.EN, comDTO.getLocale(), source);
							if(cachedMTMap == null) {
								cachedMTMap = new HashMap<String, String>();
							}
							cachedMTMap.put(key, mtTranslation);
							cacheComDTO.setMessages(cachedMTMap);
							TranslationCache3.addCachedObject(CacheName.MTSOURCE,
									com_key, ComponentMessagesDTO.class, cacheComDTO);
						}
					}
					// Component object doesn't exist in cache:
					// 1. get MT string from MT server;
					// 2. put it to component object;
					// 3. add the component object to cache
					else {
						mtTranslation = mtProcessor.translateString(
								ConstantsUnicode.EN_US, comDTO.getLocale(), source);
						Map<String, String> newMap = new HashMap<String, String>();
						newMap.put(key, mtTranslation);
						comDTO.setMessages(newMap);
						TranslationCache3.addCachedObject(CacheName.MTSOURCE, com_key,ComponentMessagesDTO.class,
								comDTO);
					}
					
					
			 return mtTranslation;
		   
	}
	
	
	
	/**
	 * get string MT translation
	 */
	

	@Override
	public StringBasedDTO getStringMTTranslation(
			final ComponentMessagesDTO comDTO, String key, String source)
			throws L3APIException {
		String mtTranslation = source;
		try {
			ComponentMessagesDTO enDTO = new ComponentMessagesDTO();
			BeanUtils.copyProperties(comDTO, enDTO);
			enDTO.setLocale(ConstantsKeys.LATEST);
			String en_key = this.getCachedKey(enDTO);
			
			storeKeySource2Cache(key, source, en_key, enDTO);
			
			String com_key = this.getCachedKey(comDTO);
		
			mtTranslation = getObjByMT( com_key,  key, comDTO,  source,  mtTranslation ); 
			
		} catch (MTException e) {
			LOGGER.error(e.getMessage(), e);
			throw new L3APIException(e.getMessage());
		} catch (VIPCacheException e) {
			LOGGER.error(e.getMessage(), e);
		}
		StringBasedDTO strDTO = new StringBasedDTO();
		BeanUtils.copyProperties(comDTO, strDTO);
		strDTO.setKey(key);
		strDTO.setSource(source);
		strDTO.setTranslation(mtTranslation);
		// update the translation if the translation is not found.
		if (StringUtils.isEmpty(mtTranslation)) {
			strDTO.setTranslation(source);
			strDTO.setStatus("The machine translation not found, English not found, return the recieved source as translation.");
		} else {
			strDTO.setStatus("The machine translation is found and returned.");
		}
		return strDTO;
	}

	private String getCachedKey(ComponentMessagesDTO comDTO) {
		return CachedKeyGetter.getOneCompnentCachedKey(comDTO) + "_"
				+ CacheName.MT;

	}
}
