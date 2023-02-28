/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.synch.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.l10n.synch.model.UpdateSyncInfoResp;
/**
 * 
 *
 * @author shihu
 *
 */
public class SynchInfoService {
	private static Logger logger = LoggerFactory.getLogger(UpdateSyncInfoResp.class);
	private static UpdateSyncInfoResp cacheToken= new UpdateSyncInfoResp();
	
	private SynchInfoService() {}

	public static UpdateSyncInfoResp getCacheToken() {
		return cacheToken;
	}

	public synchronized static void updateCacheToken(String productName, String version) {
		cacheToken.updateCacheToken(productName, version);
		logger.info("the l10n's catche token is: {}", cacheToken.getUpdateCacheToken());
	}
}
