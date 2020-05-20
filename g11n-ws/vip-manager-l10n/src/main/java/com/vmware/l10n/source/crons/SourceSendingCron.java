/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.crons;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.source.service.RemoteSyncService;
import com.vmware.l10n.source.service.SourceService;
import com.vmware.l10n.source.service.impl.SourceServiceImpl;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.cache.TranslationCache3;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.common.exceptions.VIPHttpException;
import com.vmware.vip.common.http.HTTPRequester;
import com.vmware.vip.common.i18n.dto.SingleComponentDTO;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;


/**
 * This implementation of interface SourceService.
 */
@Service
@PropertySource("classpath:application.properties")
public class SourceSendingCron {
	private static Logger LOGGER = LoggerFactory.getLogger(SourceService.class);
	private final static String LOCAL_STR = "local";
	private final static BlockingQueue<String> instruments = new LinkedBlockingQueue<String>();

	private final static long THREESECOND = 1000L;
	// syncSourceToRemoteAndLocal instrument
	private final static String SYNCSRC2RMTLCINS = "syncS2RL";

	@Value("${sync.source.enable}")
	private boolean syncEnabled;

	/** the path of local resource file,can be configed in spring config file **/
	@Value("${source.bundle.file.basepath}")
	private String basePath;

	/** the url of GRM API,can be configed in spring config file **/
	@Value("${grm.server.url}")
	private String remoteGRMURL;

	/** the url of GRM API,can be configed in spring config file **/
	@Value("${vip.server.url}")
	private String remoteVIPURL;

	@Autowired
	private SourceDao sourceDao;

	@Autowired
	private RemoteSyncService remoteSyncService;

	// it represents the remote service is available.
	private static boolean connected = true;

	@PostConstruct
	public void init() {
		if (!syncEnabled) {
			return;
		}
		try {

			if (!remoteGRMURL.equalsIgnoreCase(LOCAL_STR)) {
				remoteSyncService.ping(remoteGRMURL);
			}
			List<String> keyList = TranslationCache3.getKeys(CacheName.SOURCEBACKUP, ComponentSourceDTO.class);
			if (connected && keyList.size() > 0) {
				for (String key : keyList) {
					ComponentSourceDTO comDTO =  TranslationCache3.getCachedObject(CacheName.SOURCEBACKUP, key);
					
					if (!remoteGRMURL.equalsIgnoreCase(LOCAL_STR)) {
						remoteSyncService.send(comDTO, remoteGRMURL);
					}

					if (!remoteVIPURL.equalsIgnoreCase(LOCAL_STR)) {

						putDataToRemoteVIP(comDTO);
					}
					TranslationCache3.deleteCachedObject(CacheName.SOURCEBACKUP, key, ComponentSourceDTO.class);
				}
			}
		} catch (VIPCacheException e) {
			LOGGER.error("Error occur in cache when perform init method.");
		} catch (L10nAPIException e) {
			setConnected(false);
			LOGGER.error("Remote [" + remoteGRMURL + "] is not connected.", e);

		} catch (VIPHttpException e) {
			LOGGER.error("Error occur in cache when perform init method to push backup data to VIP.", e);

		}
	}

	@Scheduled(cron = "${sync.source.schedule.cron}")
	public void syncSourceToRemoteAndLocalInstrument() {
		try {

			LOGGER.debug("!!!!!!!!!!!!!!!!!!!!!!put updated source to instrument queue!!!!!!!!!!!!!!!!!!!!!!");

			instruments.put(SYNCSRC2RMTLCINS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage(), e);
			Thread.currentThread().interrupt();

		}
	}

	/**
	 * Synchronize the updated source to local resource file and GRM timingly
	 */

	@Scheduled(fixedDelay = THREESECOND)
	public void syncSourceToRemoteAndLocal() {
		if (syncEnabled) {
			if (instruments.isEmpty()) {
				return;
			}
			do {
				instruments.poll();

			} while (!instruments.isEmpty());

			LOGGER.debug(
					"------------------------Synchronize the updated source to local------------------------------");

			try {
				List<File> queueFiles = DiskQueueUtils.listSourceQueueFile(basePath);
				if(queueFiles == null ) {
					return;
				}
				LOGGER.debug("the source cache file size ----------------{}", queueFiles.size());

				for (File quefile : queueFiles) {

					Map<String, ComponentSourceDTO> mapObj = null;
					try {
						mapObj = DiskQueueUtils.getQueueFile2Obj(quefile);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						LOGGER.error(e.getMessage(), e);
						continue;
					}
					
					if(mapObj == null) {
						continue;
					}

					for (Entry<String, ComponentSourceDTO> entry : mapObj.entrySet()) {

						String ehcachekey = entry.getKey();
						ComponentSourceDTO cachedComDTO = entry.getValue();

						if (!StringUtils.isEmpty(cachedComDTO)) {
							SingleComponentDTO sdto = new SingleComponentDTO();
							BeanUtils.copyProperties(cachedComDTO, sdto);
							String result = sourceDao.getFromBundle(sdto, basePath);
							ComponentMessagesDTO componentMessagesDTO = sourceDao.mergeCacheWithBundle(cachedComDTO,
									result);
							boolean updateFlag = false;
							// update the source to bundle.
							updateFlag = sourceDao.updateToBundle(componentMessagesDTO, basePath);
							if (updateFlag) {
								if (connected) {
									// push the source to GRM.
									if (!remoteGRMURL.equalsIgnoreCase(LOCAL_STR)) {
										remoteSyncService.send(cachedComDTO, remoteGRMURL);
									}
									// push the source to VIP.
									if (!remoteVIPURL.equalsIgnoreCase(LOCAL_STR)) {
										putDataToRemoteVIP(cachedComDTO);
									}

								} else {
									flushCacheToDisk(TranslationCache3.getCache(CacheName.SOURCEBACKUP, ComponentSourceDTO.class));
								}

							} else {

								LOGGER.warn("Failed to update source: {}", ehcachekey);

								// put the failed the source to collection queue
								SourceServiceImpl.setParpareMap(cachedComDTO, ehcachekey);
							}
						}
					}
					
					try {
						DiskQueueUtils.delQueueFile(quefile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						LOGGER.error(e.getMessage(), e);
					}
				}
			} catch (VIPCacheException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (L10nAPIException e) {

				setConnected(false);
				try {
					flushCacheToDisk(TranslationCache3.getCache(CacheName.SOURCEBACKUP, ComponentSourceDTO.class));
				} catch (VIPCacheException e1) {

					LOGGER.error(e1.getMessage(), e1);
				}
				LOGGER.info("Fail to push source to remote.");
			} catch (VIPHttpException e) {

				LOGGER.error("Http request error occurs.", e);
			}

		}
	}

	private void putDataToRemoteVIP(ComponentSourceDTO cachedComDTO) throws VIPHttpException {
		String urlStr = remoteVIPURL + APIV2.PRODUCT_TRANSLATION_PUT
				.replace("{" + APIParamName.PRODUCT_NAME + "}", cachedComDTO.getProductName())
				.replace("{" + APIParamName.VERSION2 + "}", cachedComDTO.getVersion());
		String locale = ConstantsKeys.LATEST;
		String jsonStr = "{\"data\":{\"productName\": \"" + cachedComDTO.getProductName()
				+ "\",\"pseudo\": false,\"translation\": [{\"component\": \"" + cachedComDTO.getComponent()
				+ "\",\"locale\": \"" + locale + "\",\"messages\": " + cachedComDTO.getMessages().toJSONString()
				+ "}],\"version\": \"" + cachedComDTO.getVersion() + "\"},\"requester\": \"" + ConstantsKeys.VL10N
				+ "\"}";
		HTTPRequester.putJSONStr(jsonStr, urlStr);
	}

	/*
	 * flush the cache content to disk
	 */
	private void flushCacheToDisk(Cache<String, ComponentSourceDTO> bkSourceCache) throws VIPCacheException {
		TranslationCache3.copy(TranslationCache3.getCache(CacheName.SOURCE, ComponentSourceDTO.class), bkSourceCache);
	}

	/**
	 * Polling the service of GRM is available.
	 */
	@Scheduled(cron = "${polling.grm.schedule.cron}")
	public void syncBkSourceToRemote() {
		if (!connected) {
			try {
				if (!remoteGRMURL.equalsIgnoreCase(LOCAL_STR)) {
					remoteSyncService.ping(remoteGRMURL);
				}

				setConnected(true);
				List<String> keyList = TranslationCache3.getKeys(CacheName.SOURCEBACKUP, ComponentSourceDTO.class);
				for (String key : keyList) {
					ComponentSourceDTO cdto = (ComponentSourceDTO) TranslationCache3
							.getCachedObject(CacheName.SOURCEBACKUP, key);
					if (!remoteGRMURL.equalsIgnoreCase(LOCAL_STR)) {
						remoteSyncService.send(cdto, remoteGRMURL);
					}
					TranslationCache3.deleteCachedObject(CacheName.SOURCEBACKUP, key, ComponentSourceDTO.class);
				}
			} catch (VIPCacheException e) {
				LOGGER.error("Error occur in cache when perform syncBkSourceToRemote method.", e);

			} catch (L10nAPIException e) {
				LOGGER.info("The remote server is unavailable.", e);
			}
		}
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void setRemoteGRMURL(String grmUrl) {
		// TODO Auto-generated method stub
		this.remoteGRMURL = grmUrl;
	}

	public void setSyncEnabled(boolean b) {
		// TODO Auto-generated method stub
		this.syncEnabled = b;
	}

	public void setRemoteVIPURL(String vipurl) {
		// TODO Auto-generated method stub
		this.remoteVIPURL = vipurl;
	}

	private static void setConnected(boolean connected) {
		SourceSendingCron.connected = connected;
	}

}
