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
import com.vmware.vipclient.i18n.messages.api.opt.server.RemoteLocaleOpt;
import com.vmware.vipclient.i18n.messages.dto.LocaleDTO;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocaleService {
    Logger logger = LoggerFactory.getLogger(LocaleService.class.getName());
    private static final String REGION_PREFIX = "region_";
    public static final String DISPN_PREFIX  = "dispn_";
    private LocaleDTO dto;

    public LocaleService() {

    }
    public LocaleService(LocaleDTO dto) {
        this.dto = dto;
    }

    public Map<String, String> getSupportedLanguages(Iterator<Locale> fallbackLocalesIter) {
        Map<String, String> dispMap = new HashMap<String, String>();

        // if display language is not set in the dto, just proceed to the next available fallback locale
        if (dto.getDisplayLanguage() == null) {
            if (fallbackLocalesIter.hasNext()) {
                dto.setDisplayLanguage(fallbackLocalesIter.next().toLanguageTag());
            } else {
                return dispMap;
            }
        }

        // TODO: Huihui has implemented this in another PR
        /*logger.debug("Look for supported languages from cache for locale [{}]", locale);
        String productName = dto.getProductID();
        String version = dto.getVersion();
        FormattingCacheService formattingCacheService = new FormattingCacheService();
        //dispMap = formattingCacheService.getSupportedLanguages(dto, locale);
        if (dispMap != null) {
            logger.debug("Found displayNames from cache for product [{}], version [{}], locale [{}]!", productName, version, locale);
            return dispMap;
        }*/

        dispMap = getSupportedLanguagesFromDS(VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
        if ((dispMap == null || dispMap.isEmpty()) && fallbackLocalesIter.hasNext()) {
            LocaleDTO fallbackLocaleDTO = new LocaleDTO(dto.getProductID(), dto.getVersion(), fallbackLocalesIter.next().toLanguageTag());
            dispMap = new LocaleService(fallbackLocaleDTO).getSupportedLanguages(fallbackLocalesIter);
            if (dispMap != null && !dispMap.isEmpty()) {
                // TODO: Huihui has implemented this in another PR
                /*formattingCacheService.addSupportedLanguages(dto, locale, dispMap);
                logger.debug("List of supported languages added to cache for product [{}], version [{}], locale [{}]!\n\n", productName, version, locale);*/
            }
        }
        return dispMap;
    }

    private Map<String, String> getSupportedLanguagesFromDS(ListIterator<DataSourceEnum> msgSourceQueueIter) {
        Map<String, String> dispMap = new HashMap<String, String>();
        if (!msgSourceQueueIter.hasNext()) {
            logger.error(FormatUtils.format(ConstantsMsg.GET_LANGUAGES_FAILED_ALL));
            return dispMap;
        }
        DataSourceEnum dataSource = msgSourceQueueIter.next();
        dispMap = dataSource.createLocaleOpt().getSupportedLanguages(dto.getDisplayLanguage());
        if (dispMap == null || dispMap.isEmpty()) {
            logger.debug(FormatUtils.format(ConstantsMsg.GET_LANGUAGES_FAILED, dataSource.toString()));
            dispMap = getSupportedLanguagesFromDS(msgSourceQueueIter);
        }
        return dispMap;
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
            	Map<String, String> tmpMap = dataSource.createLocaleOpt().getSupportedLanguages(language);
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
