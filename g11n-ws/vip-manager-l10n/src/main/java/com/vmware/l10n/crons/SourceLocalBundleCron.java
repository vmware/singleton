/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.crons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.vmware.l10n.source.service.SyncLocalBundleService;

/**
 * 
 * This class used to scheduled merg the collection source to locale bundle
 *
 */
@Service
@ConditionalOnProperty(value="sync.source.enable", havingValue="true",  matchIfMissing=false)
public class SourceLocalBundleCron {
	private final static long THREESECOND = 3000;

	@Autowired
	private SyncLocalBundleService syncLocalBundleService;
	
	/**
	 * 
	 * Synchronize the updated source to local bundle or s3 bundle
	 *
	 */
	@Scheduled(fixedDelay = THREESECOND)
	public void syncSourceToLocalBundleCron() {
		syncLocalBundleService.mergeSourceToLocalBundle();
	}

	
	
	
	

}
