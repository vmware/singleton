/*
 * Copyright 2019-2022 VMware, Inc.
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
        Set<String> locales = this.getSupportedLocales();
        List<String> components = this.getComponents();
        if (locales != null && components != null) {
            for (String locale : locales) {
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
     * Retrieves the combined set of supported locales from all data sources.
     *
     * @return The combined set of supported locales from all data sources. An empty set if fetch fails.
     */
    private Set<String> getSupportedLocales() {
        return combineSupportedLocales(true);
    }

    /**
     * Retrieves the combined set of cached supported locales from all data sources.
     * It also refreshes/populates the cache if expired/not yet cached in a separate thread.
     *
     * @return The combined set of supported locales from all data sources found in cache.
     * An empty set if cache is empty or not yet populated.
     */
    public Set<String> getCachedSupportedLocales() {
        return combineSupportedLocales(false);
    }

    /**
     * Retrieves the combined set of supported locales from all data sources.
     *
     * @param refreshCache If true, it will trigger a cache populate or refresh as necessary before returning.
     *                         If false, it will return the data from the cache as is, or an empty Set if not in cache.
     * @return The set of supported locales.
     */
    private Set<String> combineSupportedLocales(boolean refreshCache) {
        Iterator<DataSourceEnum> msgSourceQueueIter = VIPCfg.getInstance().getMsgOriginsQueue().iterator();
        Set<String> supportedLangTags = new HashSet<>();
        while(msgSourceQueueIter.hasNext()) {
            if (refreshCache)
                supportedLangTags.addAll(getSupportedLocales(msgSourceQueueIter.next()));
            else
                supportedLangTags.addAll(getCachedSupportedLocales(msgSourceQueueIter.next()));
        }
        return supportedLangTags;
    }

    /**
     * Retrieves the cached set of locales that are supported in the given data source.
     *
     * @param dataSource The data source
     * @return The data from the cache as is, or an empty Set if not in cache.
     */
    public Set<String> getCachedSupportedLocales(DataSourceEnum dataSource) {
        return processSupportedLocales(dataSource, false);
    }

    /**
     * Retrieves the set of locales that are supported in the given data source.
     * It will trigger a cache populate or refresh as necessary before returning.
     *
     * @param dataSource The data source
     * @return The set of locales supported in the given data source. An empty set if fetch fails.
     */
    public Set<String> getSupportedLocales(DataSourceEnum dataSource) {
        return processSupportedLocales(dataSource, true);
    }

    private Set<String> processSupportedLocales(DataSourceEnum dataSource, boolean waitFetchFromDS) {
        CacheService cs = new CacheService(new MessagesDTO(dto));
        MessageCacheItem cacheItem = cs.getCacheOfLocales(dataSource);
        if (cacheItem != null) {
            if (cacheItem.isExpired())
                refreshLocalesCacheItemTask(cacheItem, dataSource);
            return cacheItem.getCachedData().keySet();
        } else {
            if (waitFetchFromDS)
                cacheItem = createLocalesCacheItem(dataSource);
            else
                createLocalesCacheItemTask(dataSource);

            if (cacheItem == null)
                return new HashSet<>();
            return cacheItem.getCachedData().keySet();
        }
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

    private void createLocalesCacheItemTask(DataSourceEnum dataSource) {
        Runnable runnable = () -> {
            try {
                createLocalesCacheItem(dataSource);
            } catch (Exception e) {
                logger.error("Failed to refresh list of supported locales for data source " + dataSource);
            }
        };
        new Thread(runnable).start();
    }
}
