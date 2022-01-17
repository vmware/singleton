/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service.impl;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.l10n.source.service.SourceService;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.l10n.utils.MapUtil;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;
import com.vmware.vip.common.l10n.source.util.PathUtil;

/**
 * This implementation of interface SourceService.
 */
@Service
public class SourceServiceImpl implements SourceService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SourceService.class);
	private final static BlockingQueue<StringSourceDTO> STRING_SOURCES = new LinkedBlockingQueue<StringSourceDTO>();
	private final static ConcurrentMap<String, ComponentSourceDTO> PREPARE_MAP = new  ConcurrentHashMap<String, ComponentSourceDTO>();
	
	/** the path of local resource file,can be configed in spring config file **/
	@Value("${source.bundle.file.basepath}")
	private String basePath;
	

	public boolean cacheSource(StringSourceDTO stringSourceDTO) throws L10nAPIException {
		if (StringUtils.isEmpty(stringSourceDTO) || StringUtils.isEmpty(stringSourceDTO.getKey())) {
			return false;
		}

		try {
			STRING_SOURCES.put(stringSourceDTO);
		} catch (InterruptedException e) {
			 LOGGER.error(e.getMessage(), e);
			 Thread.currentThread().interrupt();
			 return false;
		}
		return true;

	}

	
    /**
     * write the mapped source to cached file
     */
	public void writeSourceToCachedFile() {
		int index = 1;
		LOGGER.debug("begin process queue's collection string to map ComponentSourceDTO ");
		while (!STRING_SOURCES.isEmpty()) {
			mergeSource2Map();
			if (index % 2048 == 0) {
				catcheMapDTO(PREPARE_MAP);
			}
			index = index + 1;

		}

		if (!PREPARE_MAP.isEmpty()) {
			catcheMapDTO(PREPARE_MAP);
		}

	}	
	
	
	/**
	 * merge the string queue source to component Map source
	 */
	private void mergeSource2Map() {

		StringSourceDTO strDTO = STRING_SOURCES.poll();
		strDTO.setLocale(ConstantsKeys.LATEST);
		String key = getKey(strDTO);
		String source = strDTO.getSource();
		String comment = strDTO.getComment();
		String catcheKey = PathUtil.generateCacheKey(strDTO);
		ComponentSourceDTO comp = PREPARE_MAP.get(catcheKey);
		
		if (StringUtils.isEmpty(comp)) {
			addNewStringSource(strDTO, catcheKey, key, source, comment);
		} else {
			updateStringSource(comp, key, source, comment);
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
		PREPARE_MAP.put(catcheKey, comp);
	}
	
	
	
	
	private boolean catcheMapDTO(Map<String, ComponentSourceDTO> sources) {
		LOGGER.debug("begin process catcheMapDTO collection string to tem cache queue");

		try {
			DiskQueueUtils.createQueueFile(sources, basePath);
			sources.clear();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		}
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
