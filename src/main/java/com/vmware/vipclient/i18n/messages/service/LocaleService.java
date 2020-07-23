/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.*;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.FormatCacheItem;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.RemoteLocaleOpt;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.JSONUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocaleService {

    Logger                      logger        = LoggerFactory.getLogger(LocaleService.class.getName());
    private static final String REGION_PREFIX = "region_";
    public static final String DISPN_PREFIX  = "dispn_";

    public LocaleService() {
    }

    public Map<String, String> getSupportedLanguages(Iterator<DataSourceEnum> msgSourceQueueIter) {
        if (!msgSourceQueueIter.hasNext()) {
            return null;
        }

        DataSourceEnum dataSource = msgSourceQueueIter.next();
        LocaleOpt opt = dataSource.createLocaleOpt();
        Map<String, String> languages =  opt.getLanguages(LocaleUtility.getDefaultLocale().toLanguageTag());
        if (languages == null) {
            // If failed to get languages from the data source
            logger.debug(FormatUtils.format(ConstantsMsg.GET_LANGUAGES_FAILED, dataSource.toString()));
            if (msgSourceQueueIter.hasNext()) {
                languages = getSupportedLanguages(msgSourceQueueIter);
            } else {
                // If failed to get languages from any data source
                logger.error(FormatUtils.format(ConstantsMsg.GET_LANGUAGES_FAILED_ALL));
            }
        }
        return languages;
    }
    public Map<String, Map<String, String>> getTerritoriesFromCLDR(
            List<String> languages) {
        
        Map<String, Map<String, String>> respMap = new HashMap<String, Map<String, String>>();
        for (String language : languages) {
            language = language.toLowerCase();
            Map<String, String> regionMap = null;
            logger.trace("look for region list of '" + language + "' from cache");
            Cache c = VIPCfg.getInstance().getCacheManager()
                    .getCache(VIPCfg.CACHE_L2);
            if (c != null) {
            	FormatCacheItem cacheItem = (FormatCacheItem) c.get(REGION_PREFIX
                        + language);    
                regionMap = cacheItem == null ? regionMap : cacheItem.getCachedData();
            }
            if (regionMap != null) {
                respMap.put(language, regionMap);
                continue;
            }
            logger.trace("get region list of '" + language
                    + "' data from backend");
            Map<String, String> tmpMap = new RemoteLocaleOpt()
				        .getTerritoriesFromCLDR(language);
            regionMap = JSONUtils.map2SortMap(tmpMap);
            respMap.put(language, regionMap);
            if (c != null) {
            	FormatCacheItem cacheItem = new FormatCacheItem(regionMap);
                c.put(REGION_PREFIX + language, cacheItem);
            }
        }
        return respMap;
    }

    public Map<String, String> getDisplayNamesFromCLDR(String language, 
    		ListIterator<DataSourceEnum> msgSourceQueueIter) {
    	Map<String, String> dispMap = new HashMap<String, String>(); 	
    	if (!msgSourceQueueIter.hasNext()) 
    		return dispMap;
        
        logger.trace("look for displayNames from cache");
        Cache c = VIPCfg.getInstance().getCacheManager()
                .getCache(VIPCfg.CACHE_L2);
        if (c != null) {
        	FormatCacheItem cacheItem = (FormatCacheItem) c.get(DISPN_PREFIX + language); 
        	if (cacheItem == null) {
        		cacheItem = new FormatCacheItem();
        	}
            dispMap = cacheItem.getCachedData();
            if (dispMap.isEmpty()) {
            	DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
            	Map<String, String> tmpMap = dataSource.createLocaleOpt().getLanguages(language);
                dispMap = JSONUtils.map2SortMap(tmpMap);
                if (dispMap != null && dispMap.size() > 0) {
                    c.put(DISPN_PREFIX + language, new FormatCacheItem(dispMap));
                }
            }
        }
        
        if (dispMap == null || dispMap.isEmpty()) {
        	return getDisplayNamesFromCLDR(language, msgSourceQueueIter);
        }
        return dispMap;
    }

}
