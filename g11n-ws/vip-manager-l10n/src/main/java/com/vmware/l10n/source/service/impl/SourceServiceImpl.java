/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
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
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;
import com.vmware.vip.common.l10n.source.dto.StringSourceDTO;
import com.vmware.vip.common.l10n.source.util.PathUtil;

/**
 * This implementation of interface SourceService.
 */
@Service
public class SourceServiceImpl implements SourceService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SourceService.class);
	private final static List<String> SOURSE_FORMATS = Arrays.asList("MD", "HTML");
	private final static BlockingQueue<StringSourceDTO> STRING_SOURCES = new LinkedBlockingQueue<StringSourceDTO>();
	private final static ConcurrentMap<String, ComponentSourceDTO> PREPARE_MAP = new  ConcurrentHashMap<String, ComponentSourceDTO>();
	
	/** the path of local resource file,can be configed in spring config file **/
	@Value("${source.bundle.file.basepath}")
	private String basePath;
	

	public boolean cacheSource(StringSourceDTO stringSourceDTO){
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
			boolean flashFlag = mergeSource2Map();
			if (index % 512 == 0 || flashFlag) {
				cacheMapDTO(PREPARE_MAP);
			}
			index = index + 1;

		}

		if (!PREPARE_MAP.isEmpty()) {
			cacheMapDTO(PREPARE_MAP);
		}

	}	
	
	
	/**
	 * merge the string queue source to component Map source
	 */
	private boolean mergeSource2Map() {

		StringSourceDTO strDTO = STRING_SOURCES.poll();
		strDTO.setLocale(ConstantsKeys.LATEST);
		String key = strDTO.getKey();
		String source = strDTO.getSource();
		String comment = strDTO.getComment();
		String sourceFormat = strDTO.getSourceFormat();
		String catcheKey = PathUtil.generateCacheKey(strDTO);
		ComponentSourceDTO comp = PREPARE_MAP.get(catcheKey);
		
		if (StringUtils.isEmpty(comp)) {
			addNewStringSource(strDTO, catcheKey, key, source, comment, sourceFormat);
		} else {
			updateStringSource(comp, key, source, comment, sourceFormat);
		}
        
		return SOURSE_FORMATS.contains(sourceFormat);
		
	}
	
    @SuppressWarnings("unchecked")
	private void updateStringSource(ComponentSourceDTO comp, String key, String source, String comment, String sourceFormat) {
    	   MapUtil.updateKeyValue(comp.getMessages(), key, source);
			if (!StringUtils.isEmpty(comment)) {
				comp.setComments(key, comment);
			}
			if (!StringUtils.isEmpty(sourceFormat)) {
				comp.setSourceFormats(key, sourceFormat);
			}
    }
	
	
	private void  addNewStringSource(StringSourceDTO  strDTO, String catcheKey, String key, String source, String comment, String sourceFormat) {
		ComponentSourceDTO comp = new ComponentSourceDTO();
		BeanUtils.copyProperties(strDTO, comp);
		comp.setMessages(key, source);
		if (!StringUtils.isEmpty(comment)) {
			comp.setComments(key, comment);
		}
		if (!StringUtils.isEmpty(sourceFormat)) {
			comp.setSourceFormats(key, sourceFormat);
		}
		PREPARE_MAP.put(catcheKey, comp);
	}
	
	
	
	
	private boolean cacheMapDTO(Map<String, ComponentSourceDTO> sources) {
		LOGGER.debug("begin process catcheMapDTO collection string to tem cache queue");

		try {
			File file = DiskQueueUtils.createQueueFile(sources, basePath);
			if(file.exists()) {
				sources.clear();
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		}
		return true;
	}




}
