/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.synch.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.CachedKeyGetter;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.messages.data.dao.exception.DataException;
import com.vmware.vip.messages.synch.dao.SynchComponentDao;
import com.vmware.vip.messages.synch.model.SyncI18nMsg;
@Service
public class SynchServiceImpl implements SynchService{
	private static Logger logger = LoggerFactory.getLogger(SynchServiceImpl.class);
	
	@Autowired
	private SynchComponentDao syschComponentDao;
	
	@Override
	public List<String> updateTranslationBatch(List<ComponentMessagesDTO> comps) {
		// TODO Auto-generated method stub
		
		List<String> result = new ArrayList<String>();
		
		for(ComponentMessagesDTO dto: comps) {
			File fileResult;
			try {
				fileResult = updateTranslation(dto);
			} catch (DataException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
				fileResult = null;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
				fileResult = null;
			} catch (VIPCacheException e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(), e);
				fileResult = null;
			}
			if(  fileResult != null && fileResult.exists()) {
				result.add(fileResult.getAbsolutePath());
			}
		}
		if(result.size()>0) {
			return result;
		}else {
			return null;
		}
		
		
	
	}

	
	

	
		public File updateTranslation(ComponentMessagesDTO componentMessagesDTO)
				throws DataException, ParseException, VIPCacheException {
			String key = CachedKeyGetter.getOneCompnentCachedKey(componentMessagesDTO);
			File updateFile;
			ComponentMessagesDTO result =  TranslationCache3.getCachedObject(CacheName.ONECOMPONENT, key, ComponentMessagesDTO.class);
			// merge with local bundle file
			SyncI18nMsg syncMsg = mergeComponentMessagesDTOWithFile(componentMessagesDTO);
			
			if (StringUtils.isEmpty(result)) {
				updateFile = syschComponentDao.update(componentMessagesDTO.getProductName(),
						componentMessagesDTO.getVersion(),
						componentMessagesDTO.getComponent(),
						componentMessagesDTO.getLocale(),
						syncMsg.getMessages());
			} else {
				updateFile = syschComponentDao.update(componentMessagesDTO.getProductName(),
						componentMessagesDTO.getVersion(),
						componentMessagesDTO.getComponent(),
						componentMessagesDTO.getLocale(),
						syncMsg.getMessages());
				
				componentMessagesDTO.setMessages(syncMsg.getMessages());
				TranslationCache3.updateCachedObject(CacheName.ONECOMPONENT, key,ComponentMessagesDTO.class, componentMessagesDTO);
			}
			return updateFile;
		}
	 
	 
	 
		/**
		 * Merge the translation in the componentMessagesDTO and in the local
		 * bundle.
		 *
		 * @param componentMessagesDTO
		 *            the object of ComponentMessagesDTO, containing the latest
		 *            translation.
		 * @return ComponentMessagesDTO a DTO object of ComponentMessagesDTO,
		 *         containing the all translation.
		 * @throws ParseException
		 * @throws DataException
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private SyncI18nMsg mergeComponentMessagesDTOWithFile(ComponentMessagesDTO componentMessagesDTO){
	
			
			SyncI18nMsg result;
			try {
				result = syschComponentDao.get(componentMessagesDTO.getProductName(), componentMessagesDTO.getVersion(), componentMessagesDTO.getComponent(), componentMessagesDTO.getLocale());
			} catch (DataException e) {
				// TODO Auto-generated catch block
			   result = null;
			}
			
			if (result != null) {
				Iterator<Map.Entry<String, Object>> it = ((Map) componentMessagesDTO.getMessages()).entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					result.getMessages().put(entry.getKey(), (String) entry.getValue());
				}
				
					return result;
			} else {
				result = new SyncI18nMsg();
				Iterator<Map.Entry<String, Object>> it = ((Map) componentMessagesDTO.getMessages()).entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Object> entry = it.next();
					result.getMessages().put(entry.getKey(), (String) entry.getValue());
				}
				
				return result;
			}
		}




	
	
}
