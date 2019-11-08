/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

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
        Map<String, Map<String, String>> respMap = new HashMap<String, Map<String, String>>();
        for (String language : languages) {
            language = language.toLowerCase();
            Map<String, String> regionMap = null;
            logger.info("look for region list of '" + language + "' from cache");
            Cache c = VIPCfg.getInstance().getCacheManager()
                    .getCache(VIPCfg.CACHE_L2);
            if (c != null) {
                regionMap = (Map<String, String>) c.get(REGION_PREFIX
                        + language);
            }
            if (regionMap != null) {
                respMap.put(language, regionMap);
                continue;
            }
            logger.info("get region list of '" + language
                    + "' data from backend");
            Map<String, String> tmpMap = new LocaleOpt()
                    .getTerritoriesFromCLDR(language);
            regionMap = JSONUtils.map2SortMap(tmpMap);
            respMap.put(language, regionMap);
            if (c != null) {
                c.put(REGION_PREFIX + language, regionMap);
            }
        }
        return respMap;
    }

    public Map<String, String> getDisplayNamesFromCLDR(String language) {
        Map<String, String> dispMap = null;
        logger.info("look for displayNames from cache");
        Cache c = VIPCfg.getInstance().getCacheManager()
                .getCache(VIPCfg.CACHE_L2);
        if (c != null) {
            dispMap = (Map<String, String>) c.get(DISPN_PREFIX + language);
            if (dispMap == null || dispMap.size() == 0) {
                logger.info("get displayname data from backend");
                Map<String, String> tmpMap = new LocaleOpt()
                        .getDisplayNamesFromCLDR(language);
                dispMap = JSONUtils.map2SortMap(tmpMap);
                if (dispMap != null && dispMap.size() > 0) {
                    c.put(DISPN_PREFIX + language, dispMap);
                }
            }
        }
        return dispMap;
    }

}
