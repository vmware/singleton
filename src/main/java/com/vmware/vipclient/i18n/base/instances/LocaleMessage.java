/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.base.instances;

import com.vmware.vipclient.i18n.messages.service.LocaleService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This class is a utility class to provide APIs related locale of specific product supported
 * by VIP, on behind the APIs will fetch data from VIP service and wrapper the result as basic
 * java util class to be called by prodcut's codes.
 */
public class LocaleMessage implements Message {

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
    public Map<String, Map<String, String>> getRegionList(List<String> supportedLanguageList) {
        return new LocaleService().getTerritoriesFromCLDR(supportedLanguageList);
    }

    /**
     * Get the display language map of the configured product
     * 
     * @param displayLanguage
     *            the display name's localized language of returned result
     * @return a map contains display name mapped by language tag, the names could be localized which
     *         determined by the displanLanguage parameter
     */
    public Map<String, String> getDisplayLanguagesList(String displayLanguage) {
        return new LocaleService().getDisplayNamesFromCLDR(displayLanguage);
    }

    /**
     * Get the supported language tag list of the configured product
     * 
     * @return a list contains the supported language tags
     */
    public List<String> getSupportedLanguageTagList() {
        Map<String, String> languageTagMap = new LocaleService()
                .getDisplayNamesFromCLDR(java.util.Locale.ENGLISH.toLanguageTag());
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
    public List<String> getSupportedDisplayNameList(String displayLanguage) {
        Map<String, String> dispNameMap = new LocaleService().getDisplayNamesFromCLDR(displayLanguage);
        Collection<String> valueCollection = dispNameMap.values();
        List<String> dispNameList = new ArrayList<String>(valueCollection);
        return dispNameList;
    }

}
