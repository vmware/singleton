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
        Set<String> locales = this.getSupportedLanguageTags();
        List<String> components = this.getComponents();
        if (locales != null && components != null) {
            for (String languageTag : locales) {
                for (Object component : components) {
                    MessagesDTO msgDTO = new MessagesDTO(((String) component).trim(), LocaleUtility.fmtToMappedLocale(Locale.forLanguageTag(languageTag)).toString().trim(),
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

    public List<Locale> getSupportedLocales() {
        List<Locale> result = new LinkedList<>();
        Set<String> supportedLocales = this.getSupportedLanguageTags();
        for (String supportedLocale : supportedLocales) {
            result.add(Locale.forLanguageTag(supportedLocale));
        }
        return result;
    }

    /**
     * Retrieves the list of language tags of a product. It recursively applies data source fallback mechanism in case of failure.
     *
     * @return list of language tags of the product specified in the dto object
     */
    public Set<String> getSupportedLanguageTags(){
        MessagesDTO msgsDTO = new MessagesDTO();
        msgsDTO.setProductID(dto.getProductID());
        msgsDTO.setVersion(dto.getVersion());
        CacheService cacheService = new CacheService(msgsDTO);
        MessageCacheItem supportedLanguages = cacheService.getSupportedLanguages(dto);
        if (supportedLanguages != null)
            return supportedLanguages.getCachedData().keySet();

        Set<String> locales = new HashSet<>();
        Iterator<DataSourceEnum> msgSourceQueueIter = VIPCfg.getInstance().getMsgOriginsQueue().iterator();
        while(locales.isEmpty() && msgSourceQueueIter.hasNext()){
            DataSourceEnum dataSource = msgSourceQueueIter.next();
            ProductOpt opt = dataSource.createProductOpt(dto);
            locales.addAll(opt.getSupportedLocales());
            // If failed to get locales from the data source, log the error.
            if (locales.isEmpty()) {
                logger.error(ConstantsMsg.GET_LOCALES_FAILED, dataSource.toString());
            }
        }

        // Add list of supported locales to cache
        Map<String, String> cacheMap = new HashMap<>();
        for (String locale : locales) {
            cacheMap.put(locale, "");
        }
        MessageCacheItem cacheItem = new MessageCacheItem(null, cacheMap, null, System.currentTimeMillis(), null);
        cacheService.addSupportedLanguages(dto, cacheItem);
        return locales;
    }
}
