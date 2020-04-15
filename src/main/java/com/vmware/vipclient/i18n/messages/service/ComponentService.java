/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.Map;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.CacheMode;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.base.cache.persist.DiskCacheLoader;
import com.vmware.vipclient.i18n.base.cache.persist.Loader;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
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
    public void getMessages(final MessageCacheItem cacheItem) {
    	VIPCfg.getInstance().getMessageOrigin().createMessageOpt(dto).getComponentMessages(cacheItem);
    }

    public Map<String, String> getComponentTranslation() {
        CacheService cs = new CacheService(dto);
 
        if (cs.isContainComponent()) {
        	return cs.getCacheOfComponent().getCachedData();
        } else {
	        // Messages are not cached in memory, so try to look in disk cache
	        if (VIPCfg.getInstance().getCacheMode() == CacheMode.DISK) {
	            Loader loader = VIPCfg.getInstance().getCacheManager()
	                    .getLoaderInstance(DiskCacheLoader.class);
	            Map<String, String> cachedMessages = loader.load(dto.getCompositStrAsCacheKey());
	            if (cachedMessages != null) // Messages are in disk cache
	            	return cachedMessages;
	        }
	        
			// Prepare a new CacheItem to store cache properties
	        MessageCacheItem cacheItem = new MessageCacheItem();
			// Pass this cacheItem to getMessages so that it will be populated from the http request
			this.getMessages(cacheItem);
			// Store the messages and properties in cache using a single CacheItem object
			cs.addCacheOfComponent(cacheItem);
			return cacheItem.getCachedData(); 
        }
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
