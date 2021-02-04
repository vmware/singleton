/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.source.service.SourceService;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.l10n.utils.MapUtil;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;
import com.vmware.vip.common.l10n.source.util.PathUtil;

/**
 * This implementation of interface SourceService.
 */
@Service
public class SourceServiceImpl implements SourceService {
	/** the path of local resource file,can be configed in spring config file **/
	@Value("${source.bundle.file.basepath}")
	private String basePath;
	
	private static Logger LOGGER = LoggerFactory.getLogger(SourceService.class);

	private final static BlockingQueue<StringSourceDTO> stringSources = new LinkedBlockingQueue<StringSourceDTO>();
	
	private final static ConcurrentMap<String, ComponentSourceDTO> prepareMap = new  ConcurrentHashMap<String, ComponentSourceDTO>();
	
	public boolean cacheSource(StringSourceDTO stringSourceDTO) throws L10nAPIException {
		if (StringUtils.isEmpty(stringSourceDTO) || StringUtils.isEmpty(stringSourceDTO.getKey())) {
			return false;
		}

		try {
			stringSources.put(stringSourceDTO);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			 LOGGER.error(e.getMessage(), e);
			 Thread.currentThread().interrupt();
			 return false;
		}
		return true;

	}

	
	@Scheduled(fixedDelay = 1000)
	public void syncSourceToRemoteAndLocal() {
		int index=1;
		LOGGER.debug("begin process queue's collection string to map ComponentSourceDTO ");
		while (!stringSources.isEmpty()) {
			
			StringSourceDTO  strDTO = stringSources.poll();
			strDTO.setLocale(ConstantsKeys.LATEST);
			String key = getKey(strDTO);
			String source = strDTO.getSource();
			String comment = strDTO.getComment();
			String catcheKey = PathUtil.generateCacheKey(strDTO);
			ComponentSourceDTO comp = prepareMap.get(catcheKey);
		   if (StringUtils.isEmpty(comp)) {
			   
			   addNewStringSource(strDTO, catcheKey,  key,  source,  comment);
			   
			}else {
				updateStringSource(comp, key, source,comment);
			}
			
			if(index%2048 == 0) {
				try {
					catcheMapDTO(prepareMap);
				} catch (L10nAPIException e) {
					// TODO Auto-generated catch block
					LOGGER.error(e.getMessage(), e);
				}
			}
			
			index = index+1;

		}
		
		try {
			if(!prepareMap.isEmpty()) {
			  catcheMapDTO(prepareMap);
	 	    }else {
	 	    	try {
	 	    		Thread.sleep(5000);
	 	    	} catch (InterruptedException e) {
	 	    		// TODO Auto-generated catch block
	 	    		LOGGER.error(e.getMessage(), e);
	 	    		Thread.currentThread().interrupt();
	 	    	}
	 	    }
			
		} catch (L10nAPIException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage(), e);
		}
		
	
	
	}	
	
	
    @SuppressWarnings("unchecked")
	private void updateStringSource(ComponentSourceDTO comp, String key, String source, String comment) {
    	   MapUtil.updateKeyValue(comp.getMessages(), key, source);
			if (!StringUtils.isEmpty(comment)) {
				MapUtil.updateKeyValue(comp.getComments(), key, comment);
			}
    }
	
	
	private void  addNewStringSource(StringSourceDTO  strDTO, String catcheKey, String key, String source, String comment) {
		ComponentSourceDTO comp = new ComponentSourceDTO();
		BeanUtils.copyProperties(strDTO, comp);
		comp.setMessages(key, source);
		if (!StringUtils.isEmpty(comment)) {
			comp.setComments(key, comment);
		}
	   prepareMap.put(catcheKey, comp);
	}
	
	
	@SuppressWarnings("unchecked")
	public static void setParpareMap(ComponentSourceDTO compReset, String catcheKey) {
		ComponentSourceDTO comp = prepareMap.get(catcheKey);
	   if (StringUtils.isEmpty(comp)) {
			prepareMap.put(catcheKey, compReset);
		}else {

			Map<String, Object> messages = compReset.getMessages();
			Map<String, Object> comments = compReset.getComments();
			for(Entry<String, Object> entry: messages.entrySet()) {
				String key = entry.getKey();
				String source= (String) entry.getValue();
				String comment = (String) comments.get(key);
				if(StringUtils.isEmpty(comp.getMessages().get(key))){
					comp.setMessages(key, source);
					if (!StringUtils.isEmpty(comment)) {
						comp.setComments(key, comment);
					}
				}
			
			}
		}
	}
	
	
	
	private boolean catcheMapDTO(Map<String, ComponentSourceDTO> sources) throws L10nAPIException{
	     LOGGER.debug("begin process catcheMapDTO collection string to tem cache queue");

	        try {
				DiskQueueUtils.createQueueFile(sources, basePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LOGGER.error(e.getMessage(), e);
				return false;
			}
		    sources.clear();
		
		return true;
}

	/*
	 * get the key from StringSourceDTO, if there's source format(e.g. HTML), will
	 * append '.#'(e.g key.#HTML).
	 */
	private static String getKey(StringSourceDTO stringSourceDTO) {
		String key = stringSourceDTO.getKey();
		if (!StringUtils.isEmpty(stringSourceDTO.getSourceFormat()))
			key = key + ConstantsChar.DOT + ConstantsChar.POUND + stringSourceDTO.getSourceFormat().toUpperCase();

		return key;
	}


}
