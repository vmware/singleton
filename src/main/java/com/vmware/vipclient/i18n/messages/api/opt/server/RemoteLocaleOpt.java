/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.LocaleCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.messages.dto.LocaleDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteLocaleOpt extends RemoteL2BaseOpt implements LocaleOpt{

    private Logger logger = LoggerFactory.getLogger(RemoteLocaleOpt.class.getName());

    private LocaleDTO dto = null;

    public RemoteLocaleOpt(LocaleDTO dto) {
        this.dto = dto;
    }

    public void getRegions(String locale, LocaleCacheItem cacheItem) {
        logger.debug("Look for regions from Singleton Service for locale [{}]!", locale);
        try {
            Map<String, Object> response = getResponse(
                    V2URL.getRegionListURL(locale, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                    ConstantsKeys.GET, null, cacheItem);

            Integer responseCode = (Integer) response.get(URLUtils.RESPONSE_CODE);

            if (responseCode != null && (responseCode.equals(HttpURLConnection.HTTP_OK) ||
                    responseCode.equals(HttpURLConnection.HTTP_NOT_MODIFIED))) {

                long timestamp = response.get(URLUtils.RESPONSE_TIMESTAMP) != null ? (long) response.get(URLUtils.RESPONSE_TIMESTAMP) : System.currentTimeMillis();
                String etag = URLUtils.createEtagString((Map<String, List<String>>) response.get(URLUtils.HEADERS));
                Long maxAgeMillis = response.get(URLUtils.MAX_AGE_MILLIS) != null ? (Long) response.get(URLUtils.MAX_AGE_MILLIS) : null;

                if (responseCode.equals(HttpURLConnection.HTTP_OK)) {
                    try {
                        String responseBody = (String) response.get(URLUtils.BODY);
                        Map<String, String> territories = getTerritoriesFromResponse(responseBody);
                        if (territories != null) {
                            logger.debug("Found the regions from Singleton Service for locale [{}].\n", locale);
                            territories = JSONUtils.map2SortMap(territories);
                            cacheItem.set(territories, etag, timestamp, maxAgeMillis);
                        } else {
                            logger.warn("Didn't find the regions from Singleton Service for locale [{}].\n", locale);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to get region data from Singleton Service!");
                    }
                }else{
                    logger.debug("There is no update on Singleton Service for the regions of locale [{}].\n", locale);
                    cacheItem.set(etag, timestamp, maxAgeMillis);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void getSupportedLanguages(String locale, LocaleCacheItem cacheItem) {
        logger.debug("Look for supported languages from Singleton Service for product [{}], version [{}], locale [{}]!",
                dto.getProductID(), dto.getVersion(), locale);
        try {
            Map<String, Object> response = (Map<String, Object>) getResponse(
                        V2URL.getSupportedLanguageListURL(
                                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL(), dto, locale),
                        ConstantsKeys.GET, null, cacheItem);

            Integer responseCode = (Integer) response.get(URLUtils.RESPONSE_CODE);

            if (responseCode != null && (responseCode.equals(HttpURLConnection.HTTP_OK) ||
                    responseCode.equals(HttpURLConnection.HTTP_NOT_MODIFIED))) {

                long timestamp = response.get(URLUtils.RESPONSE_TIMESTAMP) != null ? (long) response.get(URLUtils.RESPONSE_TIMESTAMP) : System.currentTimeMillis();
                String etag = URLUtils.createEtagString((Map<String, List<String>>) response.get(URLUtils.HEADERS));
                Long maxAgeMillis = response.get(URLUtils.MAX_AGE_MILLIS) != null ? (Long) response.get(URLUtils.MAX_AGE_MILLIS) : null;

                if (responseCode.equals(HttpURLConnection.HTTP_OK)) {
                    try {
                        String responseBody = (String) response.get(URLUtils.BODY);
                        Map<String, String> languages = getLanguagesFromResponse(responseBody);
                        if (languages != null) {
                            logger.debug("Found the supported languages from Singleton Service for product [{}], version [{}], locale [{}].\n", dto.getProductID(), dto.getVersion(), locale);
                            languages = JSONUtils.map2SortMap(languages);
                            cacheItem.set(languages, etag, timestamp, maxAgeMillis);
                        }else{
                            logger.warn("Didn't find the supported languages from Singleton Service for product [{}], version [{}], locale [{}].\n", dto.getProductID(), dto.getVersion(), locale);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to get language data from remote!");
                    }
                }else{
                    logger.debug("There is no update on Singleton Service for the supported languages for product [{}], version [{}], locale [{}].\n", dto.getProductID(), dto.getVersion(), locale);
                    cacheItem.set(etag, timestamp, maxAgeMillis);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private Map<String, String> getTerritoriesFromResponse(String responseBody){
        List<Map<String, Object>> dataNode = (List<Map<String, Object>>) getDataFromResponse(responseBody);
        if (dataNode != null && !dataNode.isEmpty()) {
            Map<String, Object> regionMap = dataNode.get(0);
            if(regionMap != null) {
                return (Map<String, String>) regionMap.get(ConstantsKeys.TERRITORIES);
            }
        }
        return null;
    }

    private Map<String, String> getLanguagesFromResponse(String responseBody){
        Map<String, Object> dataNode = (Map<String, Object>) getDataFromResponse(responseBody);
        if (dataNode == null || dataNode.isEmpty()) {
            return null;
        }
        Map<String, String> dispMap = null;
        List<Map<String, String>> languagesArray = (List<Map<String, String>>) dataNode.get(ConstantsKeys.LANGUAGES);
        if (languagesArray != null && !languagesArray.isEmpty()) {
            dispMap = new HashMap<String, String>();
            for (int i = 0; i < languagesArray.size(); i++) {
                Map<String, String> languageNode = languagesArray.get(i);
                if(languageNode != null) {
                    dispMap.put(languageNode.get(ConstantsKeys.LANGUAGE_TAG),
                            languageNode.get(ConstantsKeys.DISPLAY_NAME));
                }
            }
        }
        return dispMap;
    }
}
