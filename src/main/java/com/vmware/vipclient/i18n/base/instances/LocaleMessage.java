/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.instances;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.dto.LocaleDTO;
import com.vmware.vipclient.i18n.messages.service.LocaleService;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a utility class to provide APIs related locale of specific product supported
 * by VIP, on behind the APIs will fetch data from VIP service and wrapper the result as basic
 * java util class to be called by prodcut's codes.
 */
public class LocaleMessage implements Message {
    Logger logger = LoggerFactory.getLogger(LocaleMessage.class);

    private VIPCfg cfg;

    public LocaleMessage() {
        super();
    }

    /**
     * Get the region data from CLDR of supported languages of the configured product
     * 
     * @param supportedLanguageList
     *            a list contain all supported language tags of the configured product,
     *            it could be obtained by function
     *            {@link com.vmware.vipclient.i18n.LocaleUtil.getSupportedLanguageTagList}
     * @return map the key is the supported language tag, value is all region data mapped by
     *         language tag
     */
    public Map<String, Map<String, String>> getRegionList(List<String> localeList) {
        Map<String, Map<String, String>> respMap = new HashMap<String, Map<String, String>>();
        LocaleService localeService = new LocaleService(null);
        for (String locale : localeList) {
            if(locale != null && !locale.isEmpty()) {
                Map<String, String> regionMap = localeService.getRegions(locale);
                respMap.put(locale, regionMap);
            }
        }
        return respMap;
    }

    /**
     * Get the display language map of the configured product
     * 
     * @param displayLanguage
     *            the display name's localized language of returned result
     * @return a map contains display name mapped by language tag, the names could be localized which
     *         determined by the displanLanguage parameter
     */
    public Map<String, String> getDisplayLanguagesList(String displayLocale) {
        return getDisplayNamesFromCLDR(displayLocale);
    }

    /**
     * Get the supported language tag list of the configured product
     * 
     * @return a list contains the supported language tags
     */
    public List<String> getSupportedLanguageTagList() {
        Map<String, String> languageTagMap = getDisplayNamesFromCLDR(LocaleUtility.getDefaultLocale().toLanguageTag());
        if(languageTagMap != null) {
            Collection<String> keyCollection = languageTagMap.keySet();
            return new ArrayList<String>(keyCollection);
        }
        return new ArrayList();
    }

    /**
     * Get supported display name list of the configured product
     * 
     * @param displayLanguage
     *            a language tag determines the display name's localization
     * @return a list contains the display names
     */
    public List<String> getSupportedDisplayNameList(String displayLocale) {
        Map<String, String> dispNameMap = getDisplayNamesFromCLDR(displayLocale);
        if(dispNameMap != null) {
            Collection<String> valueCollection = dispNameMap.values();
            return new ArrayList<String>(valueCollection);
        }
        return new ArrayList();
    }

    private Map<String, String> getDisplayNamesFromCLDR(String locale) {
        if(locale == null || locale.isEmpty()) {
            logger.warn("Locale is empty!");
            return null;
        }
        LocaleDTO dto = new LocaleDTO();
        if(cfg != null) {
            dto.setProductID(cfg.getProductName());
            dto.setVersion(cfg.getVersion());
        }else{
            dto.setProductID(VIPCfg.getInstance().getProductName());
            dto.setVersion(VIPCfg.getInstance().getVersion());
        }
        LocaleService localeService = new LocaleService(dto);
        return localeService.getDisplayNames(locale);
    }

    public VIPCfg getCfg() {
        return this.cfg;
    }

    public void setCfg(final VIPCfg cfg) {
        this.cfg = cfg;
    }
}
