/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.Cache;
import com.vmware.vipclient.i18n.base.cache.CacheMode;
import com.vmware.vipclient.i18n.base.cache.persist.DiskCacheLoader;
import com.vmware.vipclient.i18n.base.cache.persist.Loader;
import com.vmware.vipclient.i18n.messages.api.opt.local.LocalMessagesOpt;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.api.url.URLUtils;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.JSONUtils;

public class ComponentService {
    private MessagesDTO dto    = null;
    Logger              logger = LoggerFactory.getLogger(ComponentService.class);

    public ComponentService(MessagesDTO dto) {
        this.dto = dto;
    }

    /*
     * Get messages from local bundle or from remote vip service(non-Javadoc)
     * 
     * @see
     * com.vmware.vipclient.i18n.messages.service.IComponentService#getMessages
     * (com.vmware.vipclient.i18n.base.DataSourceEnum)
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getMessages(final Map<String, Object> cacheProps) {
        Map<String, String> transMap = new HashMap<String, String>();
        ComponentBasedOpt opt = new ComponentBasedOpt(dto);
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
        	Map<String, Object> response = opt.getComponentMessages(cacheProps);
	    	transMap = opt.getMsgsJson(response);
	    	cacheProps.clear();
	    	cacheProps.putAll(response);
        } else if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.Bundle) {
            transMap = new LocalMessagesOpt(dto).getComponentMessages();
        }
        return transMap;
    }

    public Map<String, String> getComponentTranslation() {
        CacheService cs = new CacheService(dto);
        Map<String, Object> cache = cs.getCacheOfComponent();
        Map<String, String> cachedMessages = (Map<String, String>) cache.get(Cache.MESSAGES);
        Map<String, Object> cacheProps = (Map<String, Object>) cache.get(Cache.CACHE_PROPERTIES);
        
        if (cachedMessages == null
                && VIPCfg.getInstance().getCacheMode() == CacheMode.DISK) {
            Loader loader = VIPCfg.getInstance().getCacheManager()
                    .getLoaderInstance(DiskCacheLoader.class);
            cachedMessages = loader.load(dto.getCompositStrAsCacheKey());
        }
        if (cachedMessages == null && !cs.isContainComponent()) {
        	if (cacheProps == null) {
            	cacheProps = new HashMap<String, Object>();
            }
            Object o = this.getMessages(cacheProps);
            Map<String, String> dataMap = (o == null ? null
                    : (Map<String, String>) o);
            
            cs.addCacheOfComponent(dataMap, cacheProps);
            cachedMessages = dataMap;
        }
        return cachedMessages;
    }

    public boolean isComponentAvailable() {
        boolean r = false;
        Long s = null;
        if (VIPCfg.getInstance().getMessageOrigin() == DataSourceEnum.VIP) {
            ComponentBasedOpt dao = new ComponentBasedOpt(dto);
            String json = dao.getTranslationStatus();
            if (!JSONUtils.isEmpty(json)) {
                try {
                    s = (Long) JSONValue.parseWithException(json);
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                }
            }
            r = (s != null) && (s.longValue() == 206);
        }
        return r;
    }
}
