/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.StringBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.JSONUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.JSONObject;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringService extends BaseService{
    Logger              logger = LoggerFactory.getLogger(StringService.class);

    private MessagesDTO dto    = null;

    public StringService() {
    }

    public StringService(MessagesDTO dto) {
        this.dto = dto;
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    public String getString(MessagesDTO dto) {
    	MessageCacheItem cacheItem = new ComponentService(dto).getMessages();
    	Map<String, String> cachedData = cacheItem.getCachedData();
		return cachedData.get(dto.getKey());
    }

    public String postString(MessagesDTO dto) {
        String r = "";
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
            ComponentBasedOpt dao = new ComponentBasedOpt(dto);
            r = dao.postString();
        }
        if (null != r && !r.equals("")) {
            dto.setLocale(ConstantsKeys.LATEST);
            CacheService c = new CacheService(dto);
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put(dto.getKey(), dto.getSource());
            
            c.updateCacheOfComponent(new MessageCacheItem(dataMap));
        }
        return r;
    }

    public boolean postStrings(List<JSONObject> sources, MessagesDTO dto) {
        boolean r = false;
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
            ComponentBasedOpt dao = new ComponentBasedOpt(dto);
            r = "200".equalsIgnoreCase(dao.postSourceSet(sources.toString()));
        }
        if (r) {
            dto.setLocale(ConstantsKeys.LATEST);
            CacheService c = new CacheService(dto);
            Map<String, String> dataMap = new HashMap<>();
            for (JSONObject jo : sources) {
                dataMap.put((String) jo.get(ConstantsKeys.KEY),
                        jo.get(ConstantsKeys.SOURCE) == null ? "" : (String) jo.get(ConstantsKeys.SOURCE));
            }
            
            c.updateCacheOfComponent(new MessageCacheItem(dataMap));
        }
        return r;
    }

    public boolean isStringAvailable(MessagesDTO dto) {
        boolean r = false;
        String status = "";
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
            CacheService c = new CacheService(dto);
            Map<String, String> statusMap = c.getCacheOfStatus();
            if (statusMap != null && !statusMap.isEmpty()) {
                status = statusMap.get(dto.getKey());
            } else if (!c.isContainStatus()) {
                StringBasedOpt dao = new StringBasedOpt(dto);
                String json = dao.getTranslationStatus();
                Map m = null;
                if (!JSONUtils.isEmpty(json)) {
                    try {
                    	JSONObject jsonObject = new JSONObject(json);
                        m = jsonObject.toMap();
                        if (m != null) {
                            status = m.get(dto.getKey()) == null ? ""
                                    : (String) m.get(dto.getKey());
                        }
                    } catch (JSONException e) {
                        logger.error(e.getMessage());
                    }
                }
                
                c.addCacheOfStatus(m);

            }
            r = "1".equalsIgnoreCase(status);
        }
        return r;
    }

    public ComponentService.TranslationsDTO getMultiVersionKeyCacheItem(String version, Iterator<Locale> fallbackLocalesIter) {
        MessagesDTO dto4LocaleList = new MessagesDTO(this.dto);
        dto4LocaleList.setVersion(version);
        dto4LocaleList.setLocale(this.dto.getLocale());
        this.doLocaleMatching(dto4LocaleList);
        this.dto.setLocale(dto4LocaleList.getLocale());

        CacheService cacheService = new CacheService(this.dto);
        MessageCacheItem cacheItem = cacheService.getCacheOfMultiVersionKey();
        if (cacheItem != null) { // Item is in cache
            if (cacheItem.isExpired())
                refreshMultiVersionKeyCacheItemTask(dto4LocaleList, cacheItem); // Refresh the cacheItem in a separate thread
        } else { // Item is not in cache.
            cacheItem = createMultiVersionKeyCacheItem(dto4LocaleList); // Fetch for the requested locale from data store, create cacheItem and store in cache
            if (cacheItem.getCachedData().isEmpty()) {  // Failed to fetch messages for the requested locale
                return getFallbackLocaleMessages(version, fallbackLocalesIter);
            }
        }
        return new ComponentService.TranslationsDTO(dto.getLocale(), cacheItem);
    }

    private ComponentService.TranslationsDTO getFallbackLocaleMessages(String version, Iterator<Locale> fallbackLocalesIter) {
        if (fallbackLocalesIter != null && fallbackLocalesIter.hasNext()) {
            Locale fallbackLocale = fallbackLocalesIter.next();
            if (fallbackLocale.toLanguageTag().equals(dto.getLocale())) {
                return getFallbackLocaleMessages(version, fallbackLocalesIter);
            }
            // Use MessageCacheItem of the next fallback locale.
            MessagesDTO fallbackLocaleDTO = new MessagesDTO(dto.getProductID(), dto.getVersion(), dto.getComponent(), fallbackLocale.toLanguageTag(), dto.getKey());
            return new StringService(fallbackLocaleDTO).getMultiVersionKeyCacheItem(version, fallbackLocalesIter);
        }
        return new ComponentService.TranslationsDTO(dto.getLocale(), new MessageCacheItem());
    }

    private MessageCacheItem createMultiVersionKeyCacheItem(MessagesDTO dto4LocaleList) {
        CacheService cacheService = new CacheService(dto);
        // Create a new cacheItem object to be stored in cache
        MessageCacheItem cacheItem = new MessageCacheItem();

        refreshMultiVersionKeyCacheItem(dto4LocaleList, cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().iterator());
        if (!cacheItem.getCachedData().isEmpty()) {
            cacheService.addCacheOfMultiVersionKey(cacheItem);
        }
        return cacheItem;
    }

    public void refreshMultiVersionKeyCacheItem(MessagesDTO dto4LocaleList, MessageCacheItem cacheItem, Iterator<DataSourceEnum> msgSourceQueueIter) {
        if (!msgSourceQueueIter.hasNext()) {
            logger.debug(FormatUtils.format(ConstantsMsg.GET_MULTI_VERSION_KEY_MESSAGES_FAILED_ALL, dto.getVersion(), dto.getComponent(), dto.getLocale(), dto.getKey()));
            return;
        }

        DataSourceEnum dataSource = msgSourceQueueIter.next();

        if (!proceed(dto4LocaleList, dataSource)) { //Requested locale is not supported, does not match any supported locales
            refreshMultiVersionKeyCacheItem(dto4LocaleList, cacheItem, msgSourceQueueIter); // Try the next dataSource
        } else {
            long timestampOld = cacheItem.getTimestamp();
            String localeOrig = dto.getLocale();
            if (dataSource.equals(DataSourceEnum.VIP) && dto.getLocale().equals(ConstantsKeys.SOURCE)) {
                dto.setLocale(ConstantsKeys.LATEST);
            }
            dataSource.createKeyBasedOpt(dto).fetchMultiVersionKeyMessages(cacheItem);
            long timestamp = cacheItem.getTimestamp();
            if (timestampOld == timestamp) {
                logger.debug(FormatUtils.format(ConstantsMsg.GET_MULTI_VERSION_KEY_MESSAGES_FAILED, dto.getVersion(), dto.getComponent(), dto.getLocale(), dto.getKey(), dataSource.toString()));
            }
            dto.setLocale(localeOrig);

            // If timestamp is 0, it means that cacheItem not yet in cache. So try the next data source.
            if (timestamp == 0) {
                // Try the next dataSource in the queue
                refreshMultiVersionKeyCacheItem(dto4LocaleList, cacheItem, msgSourceQueueIter);
            }
        }
    }

    private void refreshMultiVersionKeyCacheItemTask(MessagesDTO dto4LocaleList, MessageCacheItem cacheItem) {
        Runnable runnable = () -> {
            try {
                refreshMultiVersionKeyCacheItem(dto4LocaleList, cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
            } catch (Exception e) {
            }
        };
        new Thread(runnable).start();
    }
}
