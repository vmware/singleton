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
import com.vmware.vipclient.i18n.base.cache.Cache.CacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.StringBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.JSONUtils;

public class StringService {
    private MessagesDTO dto    = null;

    Logger              logger = LoggerFactory.getLogger(StringService.class);

    public StringService(MessagesDTO dto) {
        this.dto = dto;
    }

    @SuppressWarnings("unchecked")
    public String getString() {
    	String key = dto.getKey();
    	CacheService cacheservice = new CacheService(dto);
    	CacheItem cacheItem = cacheservice.getCacheOfComponent();
    	Map<String, String> cacheOfComponent = null;
    	if ((cacheItem == null && !cacheservice.isContainComponent()) || cacheservice.isExpired()) {
    		// If messages are not yet in cache, create a HashMap 'cacheProps' for cache properties.
    		// Otherwise, use the cacheProps that is already in the cache.
    		Map<String, Object> cacheProps = cacheItem == null ? new HashMap<String, Object>() : cacheItem.getCacheProperties();
    		
    		// Pass cacheProps to getMessages so that:
    		// 1. ETag (and others) can be used for the next HTTP request.
    		// 2. Cached properties can be refreshed with new properties from the next HTTP response.
    		Object o = new ComponentService(dto).getMessages(cacheProps);
    		
    		// If cacheProps from the cache was passed to getMessages above, 
    		// then the cache already contains the new properties from the response (eg. 200 or 304) at this point
    		// so only call cacheservice.addCacheOfComponent to refresh cached messages.
    		if (o != null) { // Messages were retrieved from the http response
    			cacheOfComponent = (Map<String, String>) o;
    			cacheservice.addCacheOfComponent(new CacheItem (cacheOfComponent, cacheProps));
    		}	
       } else if (cacheItem != null) { // cacheItem has not expired
    	   cacheOfComponent = cacheItem.getCachedData();
       }
       return (cacheOfComponent == null || cacheOfComponent.get(key) == null ? "" : cacheOfComponent.get(key));
    }

    public String postString() {
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

    public boolean postStrings(List<JSONObject> sources) {
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

    public boolean isStringAvailable() {
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
