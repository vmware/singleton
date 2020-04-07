/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.CacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.StringBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.JSONUtils;

public class StringService {
    Logger              logger = LoggerFactory.getLogger(StringService.class);
    
    @SuppressWarnings("unchecked")
    public String getString(MessagesDTO dto) {
    	String key = dto.getKey();
    	CacheService cacheService = new CacheService(dto);
    	CacheItem cacheItem = cacheService.getCacheOfComponent();
    	Map<String, String> cacheOfComponent = null;
    	if (cacheItem != null) { // Item is in cache
    		cacheOfComponent = cacheItem.getCachedData();
    		if (cacheService.isExpired()) { // cacheItem has expired
    			// Update the cache in a separate thread
    			populateCacheTask(cacheItem.getCacheProperties(), cacheService, dto); 		
    		}
    	} else { // Item is not in cache
    		// Create a new HashMap to store cache properties.
    		cacheOfComponent = populateCache(new HashMap<String, Object>(), cacheService, dto);
       } 
       return (cacheOfComponent == null || cacheOfComponent.get(key) == null ? "" : cacheOfComponent.get(key));
    }
    
    private volatile boolean running = true;
	private void populateCacheTask(Map<String, Object> cacheProps, final CacheService cacheService, MessagesDTO dto) {
		Runnable task = () -> {
	    	while (running) {
	    		try {
			    	// Use the cacheProps that is already in the cache.
			    	populateCache(cacheProps, cacheService, dto);
	    		} finally {
			    	running = false;
			    }
	    	}
		    
		};
		new Thread(task).start();
	}
	
	private Map<String, String> populateCache(Map<String, Object> cacheProps, 
			CacheService cacheService, MessagesDTO dto) {
    	// Pass cacheProps to getMessages so that:
		// 1. A previously stored ETag, if any, can be used for the next HTTP request.
		// 2. Cached properties can be refreshed with new properties from the next HTTP response.	
		Map<String, String>  cacheOfComponent = new ComponentService(dto).getMessages(cacheProps);
		
		//Store the CacheItem object in cache
		cacheService.addCacheOfComponent(new CacheItem (cacheOfComponent, cacheProps));
		
		return cacheOfComponent;
    }

    public String postString(MessagesDTO dto) {
        String r = "";
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
            ComponentBasedOpt dao = new ComponentBasedOpt(dto);
            r = dao.postString();
        }
        if (null != r && !r.equals("")) {
            dto.setLocale(ConstantsKeys.LATEST);
            CacheService c = new CacheService(dto);
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put(dto.getKey(), dto.getSource());
            
            c.updateCacheOfComponent(new CacheItem(dataMap));
        }
        return r;
    }

    public boolean postStrings(List<JSONObject> sources, MessagesDTO dto) {
        boolean r = false;
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
            ComponentBasedOpt dao = new ComponentBasedOpt(dto);
            r = "200".equalsIgnoreCase(dao.postSourceSet(sources.toString()));
        }
        if (r) {
            dto.setLocale(ConstantsKeys.LATEST);
            CacheService c = new CacheService(dto);
            Map<String, String> dataMap = new HashMap<>();
            for (JSONObject jo : sources) {
                dataMap.put((String) jo.get(ConstantsKeys.KEY),
                        jo.get(ConstantsKeys.SOURCE) == null ? "" : (String) jo.get(ConstantsKeys.SOURCE));
            }
            
            c.updateCacheOfComponent(new CacheItem(dataMap));
        }
        return r;
    }

    public boolean isStringAvailable(MessagesDTO dto) {
        boolean r = false;
        String status = "";
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
            CacheService c = new CacheService(dto);
            Map<String, String> statusMap = c.getCacheOfStatus();
            if (statusMap != null && !statusMap.isEmpty()) {
                status = statusMap.get(dto.getKey());
            } else if (!c.isContainStatus()) {
                StringBasedOpt dao = new StringBasedOpt(dto);
                String json = dao.getTranslationStatus();
                Map m = null;
                if (!JSONUtils.isEmpty(json)) {
                    try {
                        m = (Map) JSONValue.parseWithException(json);
                        if (m != null) {
                            status = m.get(dto.getKey()) == null ? ""
                                    : (String) m.get(dto.getKey());
                        }
                    } catch (ParseException e) {
                        logger.error(e.getMessage());
                    }
                }
                
                c.addCacheOfStatus(m, new HashMap<String, Object>());

            }
            r = "1".equalsIgnoreCase(status);
        }
        return r;
    }
}
