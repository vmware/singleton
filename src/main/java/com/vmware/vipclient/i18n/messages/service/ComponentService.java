/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.service;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

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

		DataSourceEnum dataSource = msgSourceQueueIter.next();
		if (!proceed(dataSource)) { //Requested locale is not supported, does not match any supported locales
			refreshCacheItem(cacheItem, msgSourceQueueIter); // Try the next dataSource
		} else {
			long timestampOld = cacheItem.getTimestamp();
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
			if (timestamp == 0) {
				// Try the next dataSource in the queue
				refreshCacheItem(cacheItem, msgSourceQueueIter);
			}
		}
	}

	/**
	 * @return 'true' for either of the following cases. Otherwise, false (locale not supported in data source).
	 * <ul>
	 * 	<li>the dataSource's set of supported locales is not in cache. If the list is not in cache, it should not block refreshCacheItem</li>
	 * 	<li>the requested locale is found in the data source's cached set of supported locales.</li>
	 * </ul>
	 */
	private boolean proceed(DataSourceEnum dataSource) {
		ProductService ps = new ProductService(dto);
		Set<String> supportedLocales = ps.getCachedSupportedLocales(dataSource);
		logger.debug("supported languages: [{}]", supportedLocales);

		/*
		 * Do not block refreshCacheItem if set of supported locales is not in cache (i.e. supportedLocales.isEmpty()).
		 * This happens either when cache is not initialized, OR previous attempts to fetch the set had failed.
		 */
		return (supportedLocales.isEmpty() || supportedLocales.contains(dto.getLocale()) || VIPCfg.getInstance().isPseudo());
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
		this.doLocaleMatching();

		CacheService cacheService = new CacheService(dto);
		MessageCacheItem cacheItem = cacheService.getCacheOfComponent();
		if (cacheItem != null) { // Item is in cache
			if (cacheItem.isExpired())
				refreshCacheItemTask(cacheItem); // Refresh the cacheItem in a separate thread
		} else { // Item is not in cache.
			cacheItem = createCacheItem(); // Fetch for the requested locale from data store, create cacheItem and store in cache
			if (cacheItem.getCachedData().isEmpty())  // Failed to fetch messages for the requested locale
				cacheItem = getFallbackLocaleMessages(fallbackLocalesIter);
		}

		return cacheItem;
	}

	private void doLocaleMatching() {
		dto.setLocale(LocaleUtility.fmtToMappedLocale(dto.getLocale()).toLanguageTag());

		//Match against list of supported locales that is already in the cache
		Set<Locale> supportedLocales = LocaleUtility.langTagtoLocaleSet(new ProductService(dto).getCachedSupportedLocales());
		Locale matchedLocale = LocaleUtility.pickupLocaleFromList(supportedLocales, Locale.forLanguageTag(dto.getLocale()));
		if (matchedLocale != null) { // Requested locale matches a supported locale (eg. requested locale "fr_CA matches supported locale "fr")
			dto.setLocale(matchedLocale.toLanguageTag());
		}
	}

    /**
     * Gets the messages in the next fallback locale by passing the next fallback locale DTO to a new instance of ComponentService
	 * and then invoking {@link #getMessages(Iterator)}.
	 * @param fallbackLocalesIter The fallback locale queue to use in case of failure. If null, no locale fallback will be applied.
     */
    private MessageCacheItem getFallbackLocaleMessages(Iterator<Locale> fallbackLocalesIter) {
		if (fallbackLocalesIter != null && fallbackLocalesIter.hasNext()) {
			Locale fallbackLocale = fallbackLocalesIter.next();
			if (fallbackLocale.toLanguageTag().equals(dto.getLocale())) {
				return getFallbackLocaleMessages(fallbackLocalesIter);
			}
			// Use MessageCacheItem of the next fallback locale.
			MessagesDTO fallbackLocaleDTO = new MessagesDTO(dto.getComponent(), fallbackLocale.toLanguageTag(), dto.getProductID(), dto.getVersion());
			return new ComponentService(fallbackLocaleDTO).getMessages(fallbackLocalesIter);
		}
		return new MessageCacheItem();
	}

	/**
	 * Creates a new MessageCacheItem for the DTO and stores it in cache.
	 */
    private MessageCacheItem createCacheItem() {
		CacheService cacheService = new CacheService(dto);
		// Create a new cacheItem object to be stored in cache
		MessageCacheItem cacheItem = new MessageCacheItem();

		refreshCacheItem(cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().iterator());
		if (!cacheItem.getCachedData().isEmpty()) {
			cacheService.addCacheOfComponent(cacheItem);
		}

		return cacheItem;
	}

	private void refreshCacheItemTask(MessageCacheItem cacheItem) {
		Runnable runnable = () -> {
    		try {
    			refreshCacheItem(cacheItem, VIPCfg.getInstance().getMsgOriginsQueue().listIterator());
    		} catch (Exception e) {
		    }
		};
		new Thread(runnable).start();
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
