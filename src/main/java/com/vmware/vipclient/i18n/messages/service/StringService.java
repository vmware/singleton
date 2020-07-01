/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.StringBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.JSONUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;

@Deprecated
public class StringService {
    Logger              logger = LoggerFactory.getLogger(StringService.class);
    
    @SuppressWarnings("unchecked")
    @Deprecated
    public String getString(MessagesDTO dto) {
    	String key = dto.getKey();
    	
    	MessageCacheItem cacheItem = new ComponentService(dto).getMessages();
    	
    	// While failed to get MessageCacheItem, use MessageCacheItem of the next fallback locale. 
    	Iterator<Locale> fallbackLocalesIter = LocaleUtility.getFallbackLocales().iterator();
    	while (cacheItem.getCachedData().isEmpty() && fallbackLocalesIter.hasNext()) {
    		Locale fallback = fallbackLocalesIter.next();
			MessagesDTO fallbackLocaleDTO = new MessagesDTO(dto.getComponent(), 
					key, dto.getSource(), fallback.toLanguageTag(), null);
			cacheItem = new ComponentService(fallbackLocaleDTO).getMessages();
			
			// Cache a reference to the MessageCacheItem of the fallback locale 
			if (!cacheItem.getCachedData().isEmpty()) {
				CacheService cacheService = new CacheService(dto);
				cacheService.addCacheOfComponent(cacheItem);
			}
		}
    	
    	Map<String, String> cachedData = cacheItem.getCachedData();
		return cachedData.get(key);
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
                        m = (Map) JSONValue.parseWithException(json);
                        if (m != null) {
                            status = m.get(dto.getKey()) == null ? ""
                                    : (String) m.get(dto.getKey());
                        }
                    } catch (ParseException e) {
                        logger.error(e.getMessage());
                    }
                }
                
                c.addCacheOfStatus(m);

            }
            r = "1".equalsIgnoreCase(status);
        }
        return r;
    }
}
