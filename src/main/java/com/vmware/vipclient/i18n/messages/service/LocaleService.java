/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.messages.api.opt.server.LocaleOpt;
import com.vmware.vipclient.i18n.util.JSONUtils;

public class LocaleService {

    Logger                      logger        = LoggerFactory.getLogger(LocaleService.class.getName());
    private static final String REGION_PREFIX = "region_";
    private static final String DISPN_PREFIX  = "dispn_";

    public LocaleService() {
    }

    public Map<String, Map<String, String>> getTerritoriesFromCLDR(
            List<String> languages) {
        Map<String, Object> cacheProps = null;
        
        Map<String, Map<String, String>> respMap = new HashMap<String, Map<String, String>>();
        for (String language : languages) {
            language = language.toLowerCase();
            Map<String, String> regionMap = null;
            logger.trace("look for region list of '" + language + "' from cache");
            Cache c = VIPCfg.getInstance().getCacheManager()
                    .getCache(VIPCfg.CACHE_L2);
            if (c != null) {
            	Map<String, Object> cache = c.get(REGION_PREFIX
                        + language);    
                regionMap = (Map<String, String>) cache.get(Cache.MESSAGES);
                cacheProps = (Map<String, Object>) cache.get(Cache.CACHE_PROPERTIES);
            }
            if (regionMap != null) {
                respMap.put(language, regionMap);
                continue;
            }
            logger.trace("get region list of '" + language
                    + "' data from backend");
            Map<String, String> tmpMap = new LocaleOpt()
				        .getTerritoriesFromCLDR(language);
            regionMap = JSONUtils.map2SortMap(tmpMap);
            respMap.put(language, regionMap);
            if (c != null) {
            	
                c.put(REGION_PREFIX + language, regionMap, cacheProps);
            }
        }
        return respMap;
    }

    public Map<String, String> getDisplayNamesFromCLDR(String language) {
    	// TODO pass map of cache properties such as etag and cache control headers
        Map<String, Object> cacheProps = null;
        Map<String, String> dispMap = null;
        logger.trace("look for displayNames from cache");
        Cache c = VIPCfg.getInstance().getCacheManager()
                .getCache(VIPCfg.CACHE_L2);
        if (c != null) {
        	Map<String, Object> cache = c.get(DISPN_PREFIX + language);           
            dispMap = (Map<String, String>) cache.get(Cache.MESSAGES);
            if (dispMap == null || dispMap.size() == 0) {
                logger.trace("get displayname data from backend");
                Map<String, String> tmpMap = new LocaleOpt()
					        .getDisplayNamesFromCLDR(language);
                dispMap = JSONUtils.map2SortMap(tmpMap);
                if (dispMap != null && dispMap.size() > 0) {
                	
                    c.put(DISPN_PREFIX + language, dispMap, cacheProps);
                }
            }
        }
        return dispMap;
    }

}
