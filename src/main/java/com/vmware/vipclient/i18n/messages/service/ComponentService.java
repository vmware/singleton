/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.JSONUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;

public class ComponentService {
    private MessagesDTO dto    = null;
    Logger              logger = LoggerFactory.getLogger(ComponentService.class);

    public ComponentService(MessagesDTO dto) {
        this.dto = dto;
    }

    /**
     * Get messages from either remote vip service or local bundle
     * 
     * @param cacheItem MessageCacheItem object to store the messages
     * @param msgSourceQueueIter ListIterator of the msgSourceQueue (e.g. [DataSourceEnum.VIP, DataSourceEnum.Bundle])
     */
    @SuppressWarnings("unchecked")
    public void getMessages(final MessageCacheItem cacheItem, ListIterator<DataSourceEnum> msgSourceQueueIter) {
    	if (!msgSourceQueueIter.hasNext()) 
    		return;
    	
    	long timestampOld = cacheItem.getTimestamp();
    	DataSourceEnum dataSource = (DataSourceEnum) msgSourceQueueIter.next();
    	dataSource.createMessageOpt(dto).getComponentMessages(cacheItem);
    	long timestampNew = cacheItem.getTimestamp();
    	
    	// If failed to get messages from the data source
    	if (timestampNew == timestampOld) {
    		// Try the next dataSource in the queue
    		if (msgSourceQueueIter.hasNext()) {
    			getMessages(cacheItem, msgSourceQueueIter);
    		// If no more data source in queue, log the error. This means that neither online nor offline fetch succeeded.
    		} else {
    			logger.error(FormatUtils.format(ConstantsKeys.GET_MESSAGES_FAILED, LocaleUtility.getDefaultLocale(), 
    					dto.getComponent(), dto.getLocale()));
    		}
    	}
    }
    
    public Map<String, String> getComponentTranslation() {
    	return fetchMessages().getCachedData();
    }
    
    public MessageCacheItem fetchMessages() {
    	CacheService cacheService = new CacheService(dto);
    	Map<String, String> cacheOfComponent = null;
    	MessageCacheItem cacheItem = null;
    	if (cacheService.isContainComponent()) { // Item is in cache
    		cacheItem = cacheService.getCacheOfComponent();
    		cacheOfComponent = cacheItem.getCachedData();
    		if (cacheItem.isExpired()) { // cacheItem has expired
    			// Update the cache in a separate thread
    			populateCacheTask(cacheService, dto, cacheItem); 		
    		}
    	} else { // Item is not in cache
    		// Create a new cacheItem object to be stored in cache
    		cacheItem = new MessageCacheItem();  
    		getMessages(cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
    		cacheOfComponent = cacheItem.getCachedData();
    		
    		if (cacheOfComponent != null && !cacheOfComponent.isEmpty()) {
    			cacheService.addCacheOfComponent(cacheItem);
    		}
    	} 
    	return cacheItem;
    }
    
	private void populateCacheTask(final CacheService cacheService, MessagesDTO dto, MessageCacheItem cacheItem) {
		Callable<MessageCacheItem> callable = () -> {
    		try {
    			
    			// Pass cacheItem to getMessages so that:
				// 1. A previously stored etag, if any, can be used for the next HTTP request.
				// 2. CacheItem properties such as etag, timestamp and maxAgeMillis can be refreshed 
				// 	 with new properties from the next HTTP response.	
				getMessages(cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
				
    			return cacheItem;
    		} catch (Exception e) { 
    			// To make sure that the thread will close 
    			// even when an exception is thrown
    			return null;
		    }
		};
		FutureTask<MessageCacheItem> task = new FutureTask<MessageCacheItem>(callable); 
		Thread thread = new Thread(task);
		thread.start();	
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
