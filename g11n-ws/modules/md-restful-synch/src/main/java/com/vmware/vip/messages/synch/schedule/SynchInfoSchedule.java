/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.synch.schedule;

import java.util.List;

import com.vmware.vip.common.cache.SingletonCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;
import com.vmware.vip.common.cache.CacheName;
import com.vmware.vip.common.exceptions.VIPCacheException;
import com.vmware.vip.core.messages.service.singlecomponent.ComponentMessagesDTO;
import com.vmware.vip.messages.synch.model.UpdateSyncInfoResp;
import com.vmware.vip.messages.synch.utils.HttpsUtils;

/**
 *
 *
 * @author shihu
 *
 */
@Service
public class SynchInfoSchedule {
	private final static long SYNCSECOND= 30000L;
	private static Logger logger = LoggerFactory.getLogger(SynchInfoSchedule.class);

	private static UpdateSyncInfoResp syncInfo =null;
	@Value("${source.cache.server.url}")
	private String sourceCacheServerUrl;

	@Value("${translation.synch.git.flag}")
	private String translationSynchGitFlag;

	@Autowired
	private SingletonCache singletonCache;

	@Scheduled(fixedDelay = SYNCSECOND)
	@ConditionalOnProperty(value = "translation.synch.git.flag")
	public void syncSourceToRemoteAndLocal() {
		if(Boolean.parseBoolean(translationSynchGitFlag)) {
			logger.info("begin to sync cache token");
			queryL10n();
		}else {
			try {
				logger.info("local i18n sync cache token is disable");
				Thread.sleep(SYNCSECOND);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.warn(e.getMessage(), e);
				Thread.currentThread().interrupt();
			}
		}



	}


	private void queryL10n() {
		String url = sourceCacheServerUrl+L10NAPIV1.SYNC_TRANSLATION_CACHE_L10N;
		try {
			logger.info("synch cache token url: {}", url);
			String resultJson = HttpsUtils.doGet(url);
			logger.info("result json: {}", resultJson);

			UpdateSyncInfoResp resp =JSON.parseObject(resultJson, UpdateSyncInfoResp.class);

			if(syncInfo!= null) {
				logger.info("the local i18n cache token: {}", syncInfo.getUpdateCacheToken());
				long result = resp.getUpdateCacheToken()-syncInfo.getUpdateCacheToken();

				if(result==1) {
					removeProductCacheKey(resp);
				}else if(result>1) {
					removeAllCacheKey();
				}
			}
			syncInfo = resp;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}

	}

	private void removeProductCacheKey(UpdateSyncInfoResp resp) {
		logger.info("sync product cache key");
		try {
			List<String> singleList = singletonCache.getKeys(CacheName.ONECOMPONENT, ComponentMessagesDTO.class);
			for(String key: singleList) {
				if(key.contains(resp.getUpdateCacheProductName()) && key.contains(resp.getUpdateCacheProductVersion())) {
					singletonCache.deleteCachedObject(CacheName.ONECOMPONENT, key, ComponentMessagesDTO.class);
				}
			}

		} catch (VIPCacheException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	private void removeAllCacheKey() {
		try {
			logger.info("sync All product cache key");
			singletonCache.removeAll(CacheName.ONECOMPONENT, ComponentMessagesDTO.class);
		} catch (VIPCacheException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}



}
