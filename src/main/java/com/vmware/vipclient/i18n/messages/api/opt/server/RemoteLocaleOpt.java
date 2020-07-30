/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.LocaleCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.LocaleOpt;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.messages.dto.LocaleDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteLocaleOpt extends L2RemoteBaseOpt implements LocaleOpt{

    private Logger logger = LoggerFactory.getLogger(RemoteLocaleOpt.class.getName());

    private LocaleDTO dto = null;

    public RemoteLocaleOpt(LocaleDTO dto) {
        this.dto = dto;
    }

    public void getRegions(String locale, LocaleCacheItem cacheItem) {
        logger.debug("Look for regions from Singleton Service for locale [{}]!", locale);
        try {
            List<Map<String, Object>> dataNode = (List<Map<String, Object>>) getDataFromResponse(
                V2URL.getRegionListURL(locale, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL()),
                ConstantsKeys.GET, null, cacheItem);
            if (dataNode != null && !dataNode.isEmpty()) {
                Map<String, Object> regionMap = dataNode.get(0);
                Map<String, String> territories = (Map<String, String>) regionMap.get(ConstantsKeys.TERRITORIES);
                if (territories != null) {
                    cacheItem.addCachedData(territories);
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
            Map<String, Object> dataNode = (Map<String, Object>) getDataFromResponse(
                        V2URL.getSupportedLanguageListURL(
                                VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL(), dto, locale),
                        ConstantsKeys.GET, null, cacheItem);
            if (dataNode == null || dataNode.isEmpty()) {
                return;
            }
            List<Map<String, String>> languagesArray = (List<Map<String, String>>) dataNode.get(ConstantsKeys.LANGUAGES);
            if (languagesArray != null && !languagesArray.isEmpty()) {
                Map<String, String> dispMap = new HashMap<String, String>();
                for (int i = 0; i < languagesArray.size(); i++) {
                    Map<String, String> languageNode = languagesArray.get(i);
                    dispMap.put(languageNode.get(ConstantsKeys.LANGUAGE_TAG),
                            languageNode.get(ConstantsKeys.DISPLAY_NAME));
                }
                cacheItem.addCachedData(dispMap);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
