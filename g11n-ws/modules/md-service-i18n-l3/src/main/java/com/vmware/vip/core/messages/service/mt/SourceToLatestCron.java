/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.service.mt;

import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.SingletonCache;
import com.vmware.vip.common.constants.ConstantsChar;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.core.messages.service.product.IProductService;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.core.messages.service.singlecomponent.IOneComponentService;
import com.vmware.vip.messages.data.dao.exception.DataException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * This implementation of interface SourceService.
 */
@Service
@ConditionalOnProperty(value ="translation.mt.sourcecache.enable", havingValue="true", matchIfMissing = true)
public class SourceToLatestCron {
	private static Logger LOGGER = LoggerFactory.getLogger(SourceToLatestCron.class);

	@Autowired
	private IProductService productService;

	@Autowired
	private IOneComponentService oneComponentService;

	@Autowired
	private SingletonCache singletonCache;
	
	
	@Scheduled(cron = "0 0/1 * * * ?")
	public void syncBkSourceToRemote() {
	
		    	List<String> keys;
		    	try {
		    		try {
		    		keys = singletonCache.getKeys(CacheName.MTSOURCE, ComponentMessagesDTO.class);
		    		}catch(NullPointerException e) {
		    			keys = null;
		    		}
		    		if(keys != null) {
		    			for(String key: keys) {
			    			ComponentMessagesDTO d = singletonCache.getCachedObject(CacheName.MTSOURCE, key, ComponentMessagesDTO.class);
			    			if(d != null && !StringUtils.isEmpty(d.getMessages()) && key.contains(ConstantsKeys.LATEST + ConstantsChar.UNDERLINE + ConstantsKeys.MT)) {
			    				LOGGER.info("Sync data to latest.json for " + key);
			    				productService.updateTranslation(d);
			    				singletonCache.deleteCachedObject(CacheName.MTSOURCE, key, ComponentMessagesDTO.class);
			    			}
			    		}
		    		}
		    	} catch (VIPCacheException | DataException | JSONException e1) {
		    		// TODO Auto-generated catch block
		    		LOGGER.error(e1.getMessage(), e1);
		    		
		    	}
		    	
		    	
		    }
		
		
	

}
