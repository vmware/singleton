/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.Iterator;
import java.util.LinkedList;
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
		if (!msgSourceQueueIter.hasNext()) {
			logger.debug(FormatUtils.format(ConstantsMsg.GET_MESSAGES_FAILED_ALL, dto.getComponent(), dto.getLocale()));
			return;
		}

		long timestampOld = cacheItem.getTimestamp();
		DataSourceEnum dataSource = msgSourceQueueIter.next();
		String localeOrig = dto.getLocale();
		if (dataSource.equals(DataSourceEnum.VIP) && dto.getLocale().equals(ConstantsKeys.SOURCE)) {
			dto.setLocale(ConstantsKeys.LATEST);
		}
		dataSource.createMessageOpt(dto).getComponentMessages(cacheItem);
		long timestamp = cacheItem.getTimestamp();
		if (timestampOld == timestamp) {
			logger.debug(FormatUtils.format(ConstantsMsg.GET_MESSAGES_FAILED, dto.getComponent(), dto.getLocale(), dataSource.toString()));
		}
		dto.setLocale(localeOrig);

		// If timestamp is 0, it means that cacheItem not yet in cache. So try the next data source.
		// If locale is "source", use messages from DatasourceEnum.Bundle data store if it exists.
		if (timestamp == 0 || dto.getLocale().equals(ConstantsKeys.SOURCE)) {
			// Try the next dataSource in the queue
			refreshCacheItem(cacheItem, msgSourceQueueIter);
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
	 * 	 	<li>The messages in a fallback locale</li>
	 * </ul>
	 */
    public MessageCacheItem getMessages(Iterator<Locale> fallbackLocalesIter) {
    	CacheService cacheService = new CacheService(dto);
    	MessageCacheItem cacheItem = cacheService.getCacheOfComponent();
    	if (cacheItem != null) { // Item is in cache
    		if (cacheItem.getCachedData().isEmpty()) { // This means that the data to be used is from a fallback locale.
				// If expired, try to first create and store cacheItem for the requested locale in a separate thread.
				if (cacheItem.isExpired())
					this.createCacheItemTask(null); // Pass null so that locale fallback will not be applied.

    			// Use cached data of cacheItem.getLocale() --> fallback locale
				MessagesDTO fallbackLocaleDTO = new MessagesDTO(dto.getComponent(), cacheItem.getLocale(), dto.getProductID(), dto.getVersion());
				cacheItem = new ComponentService(fallbackLocaleDTO).getMessages(null);
			} else if (cacheItem.isExpired()) {
    			// Refresh the cacheItem in a separate thread
    			refreshCacheItemTask(cacheItem);
    		}
    	} else { // Item is not in cache. Create and store cacheItem for the requested locale
			cacheItem = createCacheItem(fallbackLocalesIter);
    	}
    	return cacheItem;
    }

	/**
	 * Creates a new MessageCacheItem for the DTO and stores it in cache.
	 *
	 * @param fallbackLocalesIter The fallback locale queue to use in case of failure. If null, no locale fallback will be applied.
	 *
	 */
    private MessageCacheItem createCacheItem(Iterator<Locale> fallbackLocalesIter) {
		CacheService cacheService = new CacheService(dto);
		// Create a new cacheItem object to be stored in cache
		MessageCacheItem cacheItem = new MessageCacheItem();

		// If the requested locale is not supported, but matches a supported locale (eg. requested locale "fr_CA matches supported locale "fr"),
		// return the messages of the supported locale that best matches the requested locale.
		ProductService ps = new ProductService(dto);
		if (!ps.isSupportedLocale(Locale.forLanguageTag(dto.getLocale()))) {
			Locale matchedLocale = LocaleUtility.pickupLocaleFromList(new LinkedList<>(ps.getSupportedLocales()), Locale.forLanguageTag(dto.getLocale()));
			if (ps.isSupportedLocale(matchedLocale)) {
				MessagesDTO matchedLocaleDTO = new MessagesDTO(dto.getComponent(), matchedLocale.toLanguageTag(), dto.getProductID(), dto.getVersion());
				cacheItem = new ComponentService(matchedLocaleDTO).getMessages(null);
				MessageCacheItem cacheItemCopy = new MessageCacheItem(matchedLocale.toLanguageTag(), null, null, System.currentTimeMillis(), cacheItem.getMaxAgeMillis());
				cacheService.addCacheOfComponent(cacheItemCopy);
				return cacheItem;
			}
		}

		// Will proceed with the following code if
		// a. requested locale is supported OR
		// b. requested locale is not supported and does not match any supported locale
		refreshCacheItem(cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().iterator());
		if (!cacheItem.getCachedData().isEmpty()) {
			cacheService.addCacheOfComponent(cacheItem);
		} else if (!dto.getLocale().equals(ConstantsKeys.SOURCE) && fallbackLocalesIter != null && fallbackLocalesIter.hasNext()) {
			// If failed to fetch message for the requested DTO, use MessageCacheItem of the next fallback locale.
			MessagesDTO fallbackLocaleDTO = new MessagesDTO(dto.getComponent(), fallbackLocalesIter.next().toLanguageTag(), dto.getProductID(), dto.getVersion());
			cacheItem = new ComponentService(fallbackLocaleDTO).getMessages(fallbackLocalesIter);
			if (!cacheItem.getCachedData().isEmpty()) {
				// Cache a copy of the fallback locale's cacheItem, but with only the locale and maxAgeMillis. Use current timestamp.
				MessageCacheItem cacheItemCopy = new MessageCacheItem(cacheItem.getLocale(), null, null, System.currentTimeMillis(), cacheItem.getMaxAgeMillis());
				cacheService.addCacheOfComponent(cacheItemCopy);
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
