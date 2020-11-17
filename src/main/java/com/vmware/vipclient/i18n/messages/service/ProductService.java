/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.messages.api.opt.ProductOpt;
import com.vmware.vipclient.i18n.messages.dto.BaseDTO;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ProductService {
    private BaseDTO dto = null;
    Logger logger = LoggerFactory.getLogger(ProductService.class);

    public ProductService(BaseDTO dto) {
        this.dto = dto;
    }

    /**
     * Retrieves translated messages of all components of a product in the requested locale (See the dto object).
     *
     * @return translated messages of all components of a product locale specified in the dto object
     */
    public List<Map> getAllComponentTranslation() {
        List<Map> list = new ArrayList<Map>();
        Set<Locale> locales = this.getSupportedLocales();
        List<String> components = this.getComponents();
        if (locales != null && components != null) {
            for (Locale locale : locales) {
                for (Object component : components) {
                    MessagesDTO msgDTO = new MessagesDTO(((String) component).trim(), LocaleUtility.fmtToMappedLocale(locale).toString().trim(),
                            dto.getProductID(), dto.getVersion());
                    Map<String, String> retMap = new ComponentService(msgDTO).getMessages(null).getCachedData();
                    if (retMap != null) {
                        list.add(retMap);
                    }
                }
            }
        }
        return list;
    }

    /**
     * Retrieves the list of components of a product. It recursively applies data source fallback mechanism in case of failure.
     *
     * @return list of components of the product specified in the dto object
     */
    public List<String> getComponents(){
        List<String> components = null;
        Iterator<DataSourceEnum> msgSourceQueueIter = VIPCfg.getInstance().getMsgOriginsQueue().iterator();
        while((components == null || components.isEmpty()) && msgSourceQueueIter.hasNext()){
            DataSourceEnum dataSource = msgSourceQueueIter.next();
            ProductOpt opt = dataSource.createProductOpt(dto);
            components = opt.getComponents();
            // If failed to get components from the data source, log the error.
            if (components == null || components.isEmpty()) {
                logger.error(ConstantsMsg.GET_COMPONENTS_FAILED, dataSource.toString());
            }
        }
        return components;
    }

    public Set<Locale> getSupportedLocales() {
        return getSupportedLocales(true);
    }

    /**
     * Retrieves the list of locales of a product.
     *
     * @param withCacheRefresh If true, it recursively applies data source fallback mechanism in case of failure.
     * @return list of locales of the product specified in the dto object, or an empty list in case of failure to retrieve from any data source.
     */
    public Set<Locale> getSupportedLocales(boolean withCacheRefresh) {
        return langTagtoLocaleSet(getSupportedLanguageTags(withCacheRefresh));
    }

    public Set<Locale> getSupportedLocales(boolean withCacheRefresh, DataSourceEnum dataSource) {
        return langTagtoLocaleSet(getSupportedLanguageTags(withCacheRefresh, dataSource));
    }

    private Set<String> getSupportedLanguageTags(boolean withCacheRefresh) {
        Iterator<DataSourceEnum> msgSourceQueueIter = VIPCfg.getInstance().getMsgOriginsQueue().iterator();
        Set<String> supportedLangTags = new HashSet<>();
        while(msgSourceQueueIter.hasNext()) {
            supportedLangTags.addAll(getSupportedLanguageTags(withCacheRefresh, msgSourceQueueIter.next()));
        }
        return supportedLangTags;
    }

    private Set<String> getSupportedLanguageTags(boolean withCacheRefresh, DataSourceEnum dataSource) {
        if (withCacheRefresh)
            return getSupportedLanguageTags(dataSource);
        CacheService cs = new CacheService(new MessagesDTO(dto));
        MessageCacheItem cacheItem = cs.getCacheOfLocales(dataSource);
        return cacheItem == null ? new HashSet<>() : cacheItem.getCachedData().keySet();
    }

    public Set<String> getSupportedLanguageTags(DataSourceEnum dataSource) {
        CacheService cs = new CacheService(new MessagesDTO(dto));
        MessageCacheItem cacheItem = cs.getCacheOfLocales(dataSource);
        if (cacheItem != null) {
            if (cacheItem.isExpired())
                refreshLocalesCacheItemTask(cacheItem, dataSource);
            return cacheItem.getCachedData().keySet();
        } else {
            cacheItem = createLocalesCacheItem(dataSource);
            if (cacheItem == null)
                return new HashSet<>();
            return cacheItem.getCachedData().keySet();
        }
    }

    public boolean isSupportedLocale(boolean withCacheRefresh, Locale locale) {
        return getSupportedLanguageTags(withCacheRefresh).contains(LocaleUtility.fmtToMappedLocale(locale).toLanguageTag());
    }

    public boolean isSupportedLocale(boolean withCacheRefresh, DataSourceEnum dataSource, Locale locale) {
        return getSupportedLanguageTags(withCacheRefresh, dataSource).contains(LocaleUtility.fmtToMappedLocale(locale).toLanguageTag());
    }

    private void refreshLocalesCacheItem(final MessageCacheItem cacheItem, DataSourceEnum dataSource) {
        long timestampOld = cacheItem.getTimestamp();
        dataSource.createProductOpt(dto).getSupportedLocales(cacheItem);
        long timestamp = cacheItem.getTimestamp();
        if (timestampOld == timestamp) {
            logger.debug(FormatUtils.format(ConstantsMsg.GET_LOCALES_FAILED, dataSource.toString()));
        }
    }

    private void refreshLocalesCacheItemTask(MessageCacheItem cacheItem, DataSourceEnum dataSource) {
        Callable<MessageCacheItem> callable = () -> {
            try {
                refreshLocalesCacheItem(cacheItem, dataSource);
                return cacheItem;
            } catch (Exception e) {
                return null;
            }
        };
        FutureTask<MessageCacheItem> task = new FutureTask<>(callable);
        Thread thread = new Thread(task);
        thread.start();
    }

    private MessageCacheItem createLocalesCacheItem(DataSourceEnum dataSource) {
        CacheService cs = new CacheService(new MessagesDTO(dto));
        MessageCacheItem cacheItem = new MessageCacheItem();
        refreshLocalesCacheItem(cacheItem, dataSource);
        if (!cacheItem.getCachedData().isEmpty()) {
            cs.addCacheOfLocales(cacheItem, dataSource);
            return cacheItem;
        }
        return null;
    }
    private Set<Locale> langTagtoLocaleSet (Set<String> languageTags) {
        Set<Locale> locales = new HashSet<>();
        if (languageTags != null) {
            for (String languageTag : languageTags) {
                locales.add(Locale.forLanguageTag(languageTag));
            }
        }
        return locales;
    }
}
