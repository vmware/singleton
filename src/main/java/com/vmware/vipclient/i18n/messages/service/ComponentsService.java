/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentsBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class ComponentsService {

    Logger                          logger = LoggerFactory.getLogger(ComponentsService.class);

    private final VIPCfg cfg;


    /**
     * @param config
     */
    public ComponentsService(final VIPCfg config) {
        this.cfg = config;
    }

    public Map<Locale, Map<String, Map<String, String>>> getTranslation(final Set<String> components,
            final Set<Locale> locales) {
        Iterator<Locale> fallbackLocalesIter = LocaleUtility.getFallbackLocales().iterator();
        return this.getTranslation(components, locales, fallbackLocalesIter);
    }

    public Map<Locale, Map<String, Map<String, String>>> getTranslation(final Set<String> components,
                                                                        final Set<Locale> locales, Iterator<Locale> fallbackLocalesIter) {

        final Map<String, Map<String, Map<String, String>>> dataMap = new HashMap<>();
        final TreeSet<String> localesSet = new TreeSet<>(
                locales.stream().map(Locale::toLanguageTag).collect(Collectors.toSet()));

        // Get data from cache first. If cache doens't have, query from server.
        final SortedSet<String> componentsToQuery = new TreeSet<>();
        final SortedSet<String> localesToQuery = new TreeSet<>();
        for (final String locale : localesSet) {
            final Map<String, Map<String, String>> localeMap = new HashMap<>();
            for (final String component : components) {
                final MessagesDTO dto = new MessagesDTO();
                dto.setProductID(this.cfg.getProductName());
                dto.setVersion(this.cfg.getVersion());
                dto.setLocale(locale);
                dto.setComponent(component);

                final CacheService cs = new CacheService(dto);
                MessageCacheItem cacheItem = cs.getCacheOfComponent();
                if (cacheItem != null) { // Get data from cache.
                	localeMap.put(component, cacheItem.getCachedData());
                } else { // Data is not in cache.
                	componentsToQuery.add(component);
                    localesToQuery.add(locale);
                } 
            }

            dataMap.put(locale, localeMap);
        }

        // Nothing to query, return.
        if (componentsToQuery.isEmpty() || localesToQuery.isEmpty())
            return this.convertDataMap(dataMap, locales);

        // Query from server.
        final ComponentsBasedOpt opt = new ComponentsBasedOpt(this.cfg);
        MessageCacheItem cacheItem = new MessageCacheItem();
        JSONObject data = opt.queryFromServer(componentsToQuery, localesToQuery, cacheItem);
        if(data != null) {
            final JSONArray bundles = (JSONArray) data.get(ConstantsKeys.BUNDLES);
            final JSONArray localesFromServer = (JSONArray) data.get(ConstantsKeys.LOCALES);
            final Map<String, String> localeMap = this.makeLocaleMap(localesToQuery, localesFromServer);

            final SortedSet<String> localesToFallback = new TreeSet<>();
            final SortedSet<String> componentsToFallback = new TreeSet<>();
            // combine data from server into the map to return.
            final Iterator<?> iter = bundles.iterator();
            while (iter.hasNext()) {
                final JSONObject bundle = (JSONObject) iter.next();
                final String locale = localeMap.get(bundle.get(ConstantsKeys.LOCALE));
                final String comp = (String) bundle.get(ConstantsKeys.COMPONENT);
                final JSONObject messages = (JSONObject) bundle.get(ConstantsKeys.MESSAGES);

                if (messages != null && !"".equals(messages)) {
                    // update cache.
                    final MessagesDTO dto = new MessagesDTO();
                    dto.setComponent(comp);
                    dto.setLocale(locale);
                    new CacheService(dto).addCacheOfComponent(new MessageCacheItem(messages, null,
                            cacheItem.getTimestamp(), cacheItem.getMaxAgeMillis()));

                    // update map to return.
                    dataMap.get(locale).put(comp, messages);
                } else {
                    localesToFallback.add(locale);
                    componentsToFallback.add(comp);
                }
            }
            if (!componentsToFallback.isEmpty()) {
                getFallbackLocaleMessages(fallbackLocalesIter, componentsToFallback, localesToFallback, dataMap);
            }
        }else {
            getFallbackLocaleMessages(fallbackLocalesIter, componentsToQuery, localesToQuery, dataMap);
        }
        return this.convertDataMap(dataMap, locales);
    }

    private Map<Locale, Map<String, Map<String, String>>> convertDataMap(
            final Map<String, Map<String, Map<String, String>>> dataMap, final Set<Locale> locales) {
        final Map<Locale, Map<String, Map<String, String>>> retMap = new HashMap<>();
        for (final Locale locale : locales) {
            retMap.put(locale, dataMap.get(locale.toLanguageTag()));
        }

        return retMap;
    }

    private Map<String, String> makeLocaleMap(final SortedSet<String> localesToQuery,
            final List<String> localesFromServer) {
        if (localesToQuery.size() != localesFromServer.size())
            throw new VIPJavaClientException(ConstantsMsg.SERVER_CONTENT_ERROR);

        final HashMap<String, String> map = new HashMap<>();
        final Iterator<String> iterOriginal = localesToQuery.iterator();
        final Iterator<String> iterServer = localesFromServer.iterator();
        while (iterOriginal.hasNext()) {
            map.put(iterServer.next(), iterOriginal.next());
        }

        return map;
    }

    private void getFallbackLocaleMessages(Iterator<Locale> fallbackLocalesIter, final Set<String> componentsToFallback,
                                                                       final Set<String> localesToFallback, final Map<String, Map<String, Map<String, String>>> dataMap) {
        if (fallbackLocalesIter != null && fallbackLocalesIter.hasNext()) {
            Locale fallbackLocale = fallbackLocalesIter.next();
            if (fallbackLocale.toLanguageTag().equals(ConstantsKeys.SOURCE)) {
                fallbackLocale = Locale.forLanguageTag(ConstantsKeys.LATEST);
            }
            Set<Locale> localeSet = new HashSet<Locale>();
            localeSet.add(fallbackLocale);
            Map<Locale, Map<String, Map<String, String>>> localeDataMap = getTranslation(componentsToFallback, localeSet, fallbackLocalesIter);
            Map<String, Map<String, String>> componentsMap = localeDataMap.get(fallbackLocale);
            if (componentsMap != null && !componentsMap.isEmpty()) {
                final SortedSet<String> localesFallbackFailed = new TreeSet<>();
                final SortedSet<String> componentsFallbackFailed = new TreeSet<>();
                for (String locale : localesToFallback) {
                    for (String component : componentsToFallback) {
                        if(dataMap.get(locale).get(component) == null){
                            Map<String, String> componentMap = componentsMap.get(component);
                            if (componentMap != null) {
                                dataMap.get(locale).put(component, componentMap);
                            } else {
                                localesFallbackFailed.add(locale);
                                componentsFallbackFailed.add(component);
                            }
                        }
                    }
                }
                if(!componentsFallbackFailed.isEmpty())
                    logger.warn(FormatUtils.format(ConstantsMsg.GET_FALLBACK_MESSAGES_FAILED, componentsFallbackFailed.toString(), localesFallbackFailed.toString()));
            } else {
                logger.warn(FormatUtils.format(ConstantsMsg.GET_FALLBACK_MESSAGES_FAILED, componentsToFallback.toString(), localeSet.toString()));
            }
        }
    }
}
