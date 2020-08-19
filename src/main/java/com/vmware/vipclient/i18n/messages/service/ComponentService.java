/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.DataSourceEnum;
import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;
import com.vmware.vipclient.i18n.common.ConstantsMsg;
import com.vmware.vipclient.i18n.messages.api.opt.server.ComponentBasedOpt;
import com.vmware.vipclient.i18n.messages.dto.MessagesDTO;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.FormatUtils;
import com.vmware.vipclient.i18n.util.JSONUtils;
import com.vmware.vipclient.i18n.util.LocaleUtility;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentService {
    private MessagesDTO dto    = null;
    Logger              logger = LoggerFactory.getLogger(ComponentService.class);

    public ComponentService(MessagesDTO dto) {
        this.dto = dto;
    }

	/**
	 * Fetches data and populates the MessageCacheItem
	 *
	 * @param cacheItem The MessageCacheItem to populate/refresh data in. The following properties of cacheItem will be populated/refreshed:
	 * <ul>
	 * 		<li>The cachedData map which holds the localized messages</li>
	 * 		<li>The timestamp of when the messages were fetched</li>
	 * 		<li>The maxAgeMillis which tells how long before the cacheData map is considered to be expired.</li>
	 * 	    <li>The eTag, if any, which will be used in the succeeding cache refresh.</li>
	 * </ul>
	 *  @param msgSourceQueueIter Iterator of the message source queue (e.g. [DataSourceEnum.VIP, DataSourceEnum.Bundle])
	 */
	@SuppressWarnings("unchecked")
	private void refreshCacheItem(final MessageCacheItem cacheItem, Iterator<DataSourceEnum> msgSourceQueueIter) {
		if (!msgSourceQueueIter.hasNext())
			return;

		long timestampOld = cacheItem.getTimestamp();
		DataSourceEnum dataSource = msgSourceQueueIter.next();
		dataSource.createMessageOpt(dto).getComponentMessages(cacheItem);
		long timestamp = cacheItem.getTimestamp();
		if (timestampOld == timestamp) {
			logger.debug(FormatUtils.format(ConstantsMsg.GET_MESSAGES_FAILED, dto.getComponent(), dto.getLocale(), dataSource.toString()));
		}

		// Skip this block if timestamp is not 0 (which means cacheItem is in the cache) regardless if cacheItem is expired or not.
		// Otherwise, try the next dataSource in the queue.
		if (timestamp == 0) {
			// Try the next dataSource in the queue
			if (msgSourceQueueIter.hasNext()) {
				refreshCacheItem(cacheItem, msgSourceQueueIter);
				// If no more data source in queue, log the error. This means that neither online nor offline fetch succeeded.
			} else {
				logger.debug(FormatUtils.format(ConstantsMsg.GET_MESSAGES_FAILED_ALL, dto.getComponent(), dto.getLocale()));
			}
		}
	}

	/**
	 * Get MessageCacheItem from cache.
	 * The cache is refreshed if MessageCacheItem is expired or not found.
	 * Pre-configured locale fallback queue is used on failure.
	 *
	 * @return A MessageCacheItem whose message map is one of the items in the following priority-ordered list:
	 * <ul>
	 * 		<li>The messages in the requested locale</li>
	 * 		<li>The messages in a default locale</li>
	 * 		<li>The source messages</li>
	 * 		<li>An empty map</li>
	 * </ul>
	 */
	public MessageCacheItem getMessages() {
		Iterator<Locale> fallbackLocalesIter = LocaleUtility.getFallbackLocales().iterator();
		return this.getMessages(fallbackLocalesIter);
	}

	/**
	 * Get MessageCacheItem from cache.
	 * The cache is refreshed if MessageCacheItem is expired or not found.
	 *
	 * @param fallbackLocalesIter The locale fallback queue to be used on failure. If null, there will be no fallback mechanism on failure so the message map will be empty.
	 *
	 * @return A MessageCacheItem whose data map is one of the following:
	 * <ul>
	 * 		<li>The messages in the requested locale</li>
	 * 	 	<li>An empty map if the messages are not available in the requested locale</li>
	 * </ul>
	 */
    public MessageCacheItem getMessages(Iterator<Locale> fallbackLocalesIter) {
    	CacheService cacheService = new CacheService(dto);
    	MessageCacheItem cacheItem = null;
    	if (cacheService.isContainComponent()) { // Item is in cache
    		cacheItem = cacheService.getCacheOfComponent();
    		if (cacheItem.isExpired()) { // cacheItem has expired
    			// Refresh the cacheItem in a separate thread
    			refreshCacheItemTask(cacheItem);
    		}
			// If the cacheItem is for a fallback locale, create and store cacheItem for the requested locale in a separate thread.
    		if (!LocaleUtility.isSameLocale(cacheItem.getLocale(), this.dto.getLocale())) {
				this.createCacheItemTask(fallbackLocalesIter);
			}
    	} else { // Item is not in cache. Create and store cacheItem for the requested locale
    		cacheItem = createCacheItem(fallbackLocalesIter);
    	}
    	return cacheItem;
    }

	/**
	 * Creates a new MessageCacheItem for the DTO and stores it in cache.
	 *
	 * @param fallbackLocalesIter The fallback locale queue to use in case of failure.
	 *
	 */
    private MessageCacheItem createCacheItem(Iterator<Locale> fallbackLocalesIter) {
		CacheService cacheService = new CacheService(dto);
		// Create a new cacheItem object to be stored in cache
		MessageCacheItem cacheItem = new MessageCacheItem();
		refreshCacheItem(cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().iterator());

		if (!cacheItem.getCachedData().isEmpty()) {
			cacheService.addCacheOfComponent(cacheItem);
		} else if (!dto.getLocale().equals(ConstantsKeys.SOURCE) && fallbackLocalesIter!=null && fallbackLocalesIter.hasNext()) {
			// If failed to fetch message for the requetsed DTO, use MessageCacheItem of the next fallback locale.
			MessagesDTO fallbackLocaleDTO = new MessagesDTO(dto.getComponent(), fallbackLocalesIter.next().toLanguageTag(), dto.getProductID(), dto.getVersion());
			cacheItem = new ComponentService(fallbackLocaleDTO).getMessages(fallbackLocalesIter);
			if (!cacheItem.getCachedData().isEmpty()) {
				cacheService.addCacheOfComponent(cacheItem);
			}
		}
		return cacheItem;
	}

	private void createCacheItemTask(Iterator<Locale> fallbackLocalesIter) {
		Callable<MessageCacheItem> callable = () -> {
			try {
				return this.createCacheItem(fallbackLocalesIter);
			} catch (Exception e) {
				// To make sure that the thread will close
				// even when an exception is thrown
				return null;
			}
		};
		FutureTask<MessageCacheItem> task = new FutureTask<>(callable);
		Thread thread = new Thread(task);
		thread.start();
	}

	private void refreshCacheItemTask(MessageCacheItem cacheItem) {
		Callable<MessageCacheItem> callable = () -> {
    		try {
    			// Get the locale of the cacheItem object. It may not be the same as the requested DTO's locale (e.g. the cacheItem is for a fallback locale).
				String cacheItemLocale = cacheItem.getLocale();

				// Refresh the properties of the cacheItem accordingly by passing a DTO with the correct locale
				// to ComponentService, so that it will fetch messages for the correct locale to refresh the cacheItem.
				MessagesDTO cacheItemDTO = new MessagesDTO(dto.getComponent(), cacheItemLocale, dto.getProductID(), dto.getVersion());
				new ComponentService(cacheItemDTO).refreshCacheItem(cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());

				return cacheItem;
    		} catch (Exception e) { 
    			// To make sure that the thread will close 
    			// even when an exception is thrown
    			return null;
		    }
		};
		FutureTask<MessageCacheItem> task = new FutureTask<>(callable);
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
