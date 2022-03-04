/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.crons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.vmware.l10n.source.service.SyncGrmSourceService;

/**
 * 
 * This class used to scheduled send the update source to GRM server
 *
 */
@Service
@ConditionalOnProperty(value="sync.source.enable", havingValue="true",  matchIfMissing=false)
public class SourceSendGRMCron {
	
	@Autowired
	private SyncGrmSourceService syncGrmSourceService;
	
	
	/**
	 * Synchronize the updated source to remote GRM server
	 */
	@Scheduled(fixedDelayString  = "${sync.source.schedule.fixed-delay:3000}")
	public void syncSource2GRMCron(){
		syncGrmSourceService.sendSourceToGRM();
	}
	
		
}
