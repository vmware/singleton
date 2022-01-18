/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.crons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.vmware.l10n.source.service.SourceService;

/**
 * 
 * This class used to scheduled merge the collection source to cached file
 *
 */
@Service
public class SourceCollectCacheCron {
	private final static long ONESECOND = 1000;

	@Autowired
	private SourceService sourceService;

	/**
	 * merge the collection source to cached file
	 */
	@Scheduled(fixedDelay = ONESECOND)
	public void syncSourceToLocalBundleCron() {
		sourceService.writeSourceToCachedFile();
	}

}
