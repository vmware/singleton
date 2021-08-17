/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.crons;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.ehcache.Cache;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.l10n.record.dao.SqlLiteDao;
import com.vmware.l10n.record.model.SyncRecordModel;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.source.service.RemoteSyncService;
import com.vmware.l10n.source.service.impl.SourceServiceImpl;
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
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;


/**
 * This implementation of interface SourceService.
 */
@Service
@PropertySource("classpath:application.properties")
public class SourceSendingCron {
	private static Logger LOGGER = LoggerFactory.getLogger(SourceSendingCron.class);
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
	
	@Value("${vip.server.authentication.enable}")
	private boolean remoteVIPAuthEnable;
	
	@Value("${vip.server.authentication.appId:#}")
	private String remoteVIPAuthAppId;
	
	@Value("${vip.server.authentication.token:#}")
	private String remoteVIPAuthAppToken;
	
	@Value("${spring.profiles.active}")
	private String activeDaoType;
	
	@Autowired
	private SourceDao sourceDao;
	
	
	@Autowired
	private SqlLiteDao sqlLite;

	@Autowired
	private RemoteSyncService remoteSyncService;

	// it represents the remote service is available.
	//private static boolean connected = true;
	private static boolean grmConnected = true;
	private static boolean singletonConnected = true;
	private static int grmFlag =1;
	private static int singletonFlag = 2;

	@PostConstruct
	public void init() {
		LOGGER.info("The active dao type: {}", activeDaoType);
		if (!syncEnabled) {
			return;
		}
		if (!remoteGRMURL.equalsIgnoreCase(LOCAL_STR)) {
			try {
				remoteSyncService.ping(remoteGRMURL);

				List<SyncRecordModel> syncGrmList = sqlLite.getSynRecords(grmFlag);
				if (syncGrmList != null) {
					for (SyncRecordModel syncGrm : syncGrmList) {
						ComponentSourceDTO comDTO = getCachedLocalBundle(syncGrm);
						remoteSyncService.send(comDTO, remoteGRMURL);

					}
				}

			} catch (L10nAPIException e) {
				setGrmConnected(false);
				LOGGER.error("Remote [" + remoteGRMURL + "] is not connected.", e);

			}
		}

		if (!remoteVIPURL.equalsIgnoreCase(LOCAL_STR)) {
			try {
				pingSingleton(remoteVIPURL);
				List<SyncRecordModel> syncSlgList = sqlLite.getSynRecords(singletonFlag);
				if (syncSlgList != null) {
					for (SyncRecordModel syncSlg : syncSlgList) {
						ComponentSourceDTO comDTO = getCachedLocalBundle(syncSlg);
						putDataToRemoteVIP(comDTO);
					}
				}

			} catch (L10nAPIException | VIPHttpException e) {
				setSingletonConnected(false);
				LOGGER.error("Remote [" + remoteVIPURL + "] is not connected.", e);

			}
		}
	}
	
	@SuppressWarnings("null")
	private ComponentSourceDTO getCachedLocalBundle(SyncRecordModel syncModel) {
		SingleComponentDTO scd = new SingleComponentDTO();
		scd.setProductName(syncModel.getProduct());
		scd.setVersion(syncModel.getVersion());
		scd.setComponent(syncModel.getComponent());
		scd.setLocale(syncModel.getLocale());
		String sourceStr = sourceDao.getFromBundle(scd);
		if (!StringUtils.isEmpty(sourceStr)) {
			JSONObject genreJsonObject = null;
			try {
				genreJsonObject = (JSONObject) JSONValue.parseWithException(sourceStr);
			} catch (ParseException e) {
	           return null;
			}
			
			if (genreJsonObject == null) {
			ComponentSourceDTO csdto = new ComponentSourceDTO();
			csdto.setProductName(scd.getProductName());
			csdto.setVersion(scd.getVersion());
			csdto.setComponent((String) genreJsonObject.get(ConstantsKeys.COMPONENT));
			csdto.setLocale((String) genreJsonObject.get(ConstantsKeys.lOCALE));
			@SuppressWarnings("unchecked")
			Map<String, String> msgMap = (Map<String, String>) genreJsonObject.get(ConstantsKeys.MESSAGES);
			for (Entry<String, String> entry : msgMap.entrySet()) {
				csdto.setMessages(entry.getKey(), entry.getValue());
			}
			return csdto;
			}

		}

		return null;

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
							ComponentMessagesDTO sdto = new ComponentMessagesDTO();
							BeanUtils.copyProperties(cachedComDTO, sdto);
							boolean updateFlag = false;
							// update the source to bundle.
							updateFlag = sourceDao.updateToBundle(sdto);
							if (updateFlag) {
								syncSource2remote(cachedComDTO);
							} else {

								LOGGER.warn("Failed to update source: {}", ehcachekey);
								if (activeDaoType.equalsIgnoreCase("s3")) {
									mapObj.remove(ehcachekey);
									processFailedUpdate(cachedComDTO, ehcachekey);
								} else {
									// put the failed the source to collection queue
									SourceServiceImpl.setParpareMap(cachedComDTO, ehcachekey);
								}
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
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
	
	
	private void syncSource2remote(ComponentSourceDTO cachedComDTO) {
		// push the source to GRM.
		if (!remoteGRMURL.equalsIgnoreCase(LOCAL_STR) && grmConnected) {
			try {
				remoteSyncService.send(cachedComDTO, remoteGRMURL);
			} catch (L10nAPIException e) {
				setGrmConnected(false);
			}
		}else if(!remoteGRMURL.equalsIgnoreCase(LOCAL_STR) && !grmConnected) {
			sqlLite.createSyncRecord(cachedComDTO, grmFlag, System.currentTimeMillis());
		}
		// push the source to VIP.
		if (!remoteVIPURL.equalsIgnoreCase(LOCAL_STR) && singletonConnected) {
			try {
				putDataToRemoteVIP(cachedComDTO);
			} catch (VIPHttpException e) {
				setSingletonConnected(false);
			}
		}else if (!remoteVIPURL.equalsIgnoreCase(LOCAL_STR) && !singletonConnected) {
			sqlLite.createSyncRecord(cachedComDTO, singletonFlag, System.currentTimeMillis());
		}


	}
	
	public void pingSingleton(String remoteURL) throws L10nAPIException{
		String reqUrl = remoteURL + APIV2.BROWSER_LOCALE;
		Map<String, String> header =null;
		if(remoteVIPAuthEnable) {
			header = new HashMap<String, String>();
			header.put("appId", remoteVIPAuthAppId);
			header.put("token", remoteVIPAuthAppToken);
		}
		if(StringUtils.isEmpty(HTTPRequester.getData(reqUrl, "GET", header))){
			throw new L10nAPIException("Error occur when send to remote ["+ reqUrl + "].");
		}
	}

	private void processFailedUpdate(ComponentSourceDTO compDTO, String cachedKey) {
	
        Map<String, ComponentSourceDTO> map = new HashMap<String,ComponentSourceDTO>();
        map.put(cachedKey, compDTO);
        try {
			DiskQueueUtils.createQueueFile(map, basePath);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
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
		Map<String, String> header =null;
		if(remoteVIPAuthEnable) {
			header = new HashMap<String, String>();
			header.put("appId", remoteVIPAuthAppId);
			header.put("token", remoteVIPAuthAppToken);
		}
			HTTPRequester.putJSONStr(jsonStr, urlStr, header);
		
	}

	

	
	/**
	 * Polling the service of GRM is available.
	 */
	@Scheduled(cron = "${polling.grm.schedule.cron}")
	public void syncBkSourceToRemote() {
		if (!syncEnabled) {
			return;
		}
		
		if (!remoteGRMURL.equalsIgnoreCase(LOCAL_STR) && !grmConnected) {
			try {
				remoteSyncService.ping(remoteGRMURL);
				List<SyncRecordModel> syncGrmList = sqlLite.getSynRecords(grmFlag);
				if(syncGrmList != null) {
					for(SyncRecordModel syncGrm : syncGrmList) {
						ComponentSourceDTO comDTO = getCachedLocalBundle(syncGrm);
						remoteSyncService.send(comDTO, remoteGRMURL);
						
					}
				}
				setGrmConnected(true);
			} catch (L10nAPIException e) {
				LOGGER.error("Remote [" + remoteGRMURL + "] is not connected.", e);

			}
		}

		if (!remoteVIPURL.equalsIgnoreCase(LOCAL_STR) && !singletonConnected) {
			try {
				pingSingleton(remoteVIPURL);
				List<SyncRecordModel> syncSlgList = sqlLite.getSynRecords(singletonFlag);
				if(syncSlgList != null) {
					for(SyncRecordModel syncSlg : syncSlgList) {
						ComponentSourceDTO comDTO = getCachedLocalBundle(syncSlg);
						 putDataToRemoteVIP(comDTO);
					}
				}
				setSingletonConnected(true);
				
			} catch (L10nAPIException e) {
				LOGGER.error("Remote [" + remoteVIPURL + "] is not connected.", e);

			} catch (VIPHttpException e) {
				LOGGER.error(e.getMessage(), e);
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

	public static void setSingletonConnected(boolean singletonConnected) {
		SourceSendingCron.singletonConnected = singletonConnected;
	}

	public static void setGrmConnected(boolean grmConnected) {
		SourceSendingCron.grmConnected = grmConnected;
	}
}
