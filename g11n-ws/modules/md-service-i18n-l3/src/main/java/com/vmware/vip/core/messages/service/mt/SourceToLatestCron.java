/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.mt;

import java.util.List;
import javax.annotation.Resource;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.core.messages.service.singlecomponent.IOneComponentService;
import com.vmware.vip.messages.data.dao.exception.DataException;

/**
 * This implementation of interface SourceService.
 */
@Service
@ConditionalOnProperty(value ="translation.mt.sourcecache.enable", havingValue="true", matchIfMissing = true)
public class SourceToLatestCron {
	private static Logger LOGGER = LoggerFactory.getLogger(SourceToLatestCron.class);

	@Resource
	private IProductService productService;

	@Resource
	private IOneComponentService oneComponentService;
	
	
	@Scheduled(cron = "0 0/1 * * * ?")
	public void syncBkSourceToRemote() {
	
		    	List<String> keys;
		    	try {
		    		try {
		    		keys = TranslationCache3.getKeys(CacheName.MTSOURCE, ComponentMessagesDTO.class);
		    		}catch(NullPointerException e) {
		    			keys = null;
		    		}
		    		if(keys != null) {
		    			for(String key: keys) {
			    			ComponentMessagesDTO d = TranslationCache3.getCachedObject(CacheName.MTSOURCE, key, ComponentMessagesDTO.class);
			    			if(d != null && !StringUtils.isEmpty(d.getMessages()) && key.contains(ConstantsKeys.LATEST + ConstantsChar.UNDERLINE + ConstantsKeys.MT)) {
			    				LOGGER.info("Sync data to latest.json for " + key);
			    				productService.updateTranslation(d);
			    				TranslationCache3.deleteCachedObject(CacheName.MTSOURCE, key, ComponentMessagesDTO.class);
			    			}
			    		}
		    		}
		    	} catch (VIPCacheException | DataException | ParseException e1) {
		    		// TODO Auto-generated catch block
		    		LOGGER.error(e1.getMessage(), e1);
		    		
		    	}
		    	
		    	
		    }
		
		
	

}
