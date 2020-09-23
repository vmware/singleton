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

    /**
     * Retrieves the list of locales of a product. It recursively applies data source fallback mechanism in case of failure.
     *
     * @return list of locales of the product specified in the dto object, or an empty list in case of failure to retrieve from any data source.
     */
    public Set<Locale> getSupportedLocales() {
        return langTagtoLocaleSet(getSupportedLanguageTags());
    }

    public Set<String> getSupportedLanguageTags() {
        Iterator<DataSourceEnum> msgSourceQueueIter = VIPCfg.getInstance().getMsgOriginsQueue().iterator();
        Set<String> supportedLangTags = new HashSet<>();
        while(msgSourceQueueIter.hasNext() && supportedLangTags.isEmpty()) {
            supportedLangTags = getSupportedLanguageTags(msgSourceQueueIter.next());
        }
        return supportedLangTags;
    }

    public Set<String> getSupportedLanguageTags(DataSourceEnum dataSource) {
        CacheService cs = new CacheService(new MessagesDTO(dto));
        MessageCacheItem cacheItem = cs.getCacheOfLocales(dataSource);
        if (cacheItem != null) {
            refreshCacheItem(cacheItem, dataSource);
        } else {
            cacheItem = createCacheItem(dataSource);
        }
        if (cacheItem == null)
            return new HashSet<>();
        return cacheItem.getCachedData().keySet();
    }

    public boolean isSupportedLocale(Locale locale) {
        return getSupportedLocales().contains(LocaleUtility.fmtToMappedLocale(locale));
    }

    private void refreshCacheItem(final MessageCacheItem cacheItem, DataSourceEnum dataSource) {
        long timestampOld = cacheItem.getTimestamp();
        dataSource.createProductOpt(dto).getSupportedLocales(cacheItem);
        long timestamp = cacheItem.getTimestamp();
        if (timestampOld == timestamp) {
            logger.debug(FormatUtils.format(ConstantsMsg.GET_LOCALES_FAILED, dataSource.toString()));
        }
    }

    private MessageCacheItem createCacheItem (DataSourceEnum dataSource) {
        CacheService cs = new CacheService(new MessagesDTO(dto));
        MessageCacheItem cacheItem = new MessageCacheItem();
        refreshCacheItem(cacheItem, dataSource);
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
