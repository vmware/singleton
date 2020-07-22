/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.instances;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.service.FormattingCacheService;
import com.vmware.vipclient.i18n.messages.service.LocaleService;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class is a utility class to provide APIs related locale of specific product supported
 * by VIP, on behind the APIs will fetch data from VIP service and wrapper the result as basic
 * java util class to be called by prodcut's codes.
 */
public class LocaleMessage implements Message {
    Logger logger = LoggerFactory.getLogger(LocaleMessage.class);

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
        for (String locale : localeList) {
            if(locale != null && !locale.isEmpty()) {
                Map<String, String> regionMap = getRegionsFromCLDR(locale);
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
        Map<String, String> languageTagMap = getDisplayNamesFromCLDR(java.util.Locale.ENGLISH.toLanguageTag());
        Collection<String> keyCollection = languageTagMap.keySet();
        List<String> languageTagList = new ArrayList<String>(keyCollection);
        return languageTagList;
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
        Collection<String> valueCollection = dispNameMap.values();
        List<String> dispNameList = new ArrayList<String>(valueCollection);
        return dispNameList;
    }

    private Map<String, String> getRegionsFromCLDR(String locale){
        Map<String, String> regionMap = null;
        LocaleService localeService = new LocaleService();
        regionMap = localeService.getRegionsFromCLDR(locale);
        if (regionMap != null) {
            return regionMap;
        }
        if (!LocaleUtility.isDefaultLocale(locale)) {
            logger.info("Can't find regions for locale [{}], look for English regions as fallback!", locale);
            regionMap = localeService.getRegionsFromCLDR(LocaleUtility.getDefaultLocale().toLanguageTag());
            if (regionMap != null) {
                new FormattingCacheService().addRegions(locale, regionMap);
                logger.debug("Default locale's regions is cached for locale [{}]!\n\n", locale);
            }
        }
        return regionMap;
    }

    private Map<String, String> getDisplayNamesFromCLDR(String locale) {
        Map<String, String> dispMap = new HashMap<String, String>();
        if(locale == null || locale.isEmpty()) {
            logger.warn("Locale is empty!");
            return dispMap;
        }
        LocaleService localeService = new LocaleService();
        dispMap = localeService.getSupportedDisplayNames(locale);
        if(dispMap != null && !dispMap.isEmpty()){
            return dispMap;
        }
        if (!LocaleUtility.isDefaultLocale(locale)) {
            logger.info("Can't find supported languages for locale [{}], look for English languages as fallback!", locale);
            Locale fallbackLocale = LocaleUtility.getDefaultLocale();
            dispMap = localeService.getSupportedDisplayNames(fallbackLocale.toLanguageTag());
            if (dispMap != null && dispMap.size() > 0) {
                new FormattingCacheService().addSupportedLanguages(locale, dispMap);
                logger.debug("Default locale's displayNames is cached for product [{}], version [{}], locale [{}]!\n\n",
                        VIPCfg.getInstance().getProductName(), VIPCfg.getInstance().getVersion(), locale);
            }
        }
        return dispMap;
    }
}
