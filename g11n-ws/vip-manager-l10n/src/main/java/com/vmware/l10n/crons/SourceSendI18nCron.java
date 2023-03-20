/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.crons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.vmware.l10n.source.service.SyncI18nSourceService;
/**
 * 
 * This class used to scheduled send the update source to Singleton server
 *
 */
@Service
@ConditionalOnProperty(value="sync.source.enable", havingValue="true",  matchIfMissing=false)
public class SourceSendI18nCron {

	@Autowired
	private SyncI18nSourceService syncI18nSourceService;
	
	/**
	 * Synchronize the updated source to remote Singleton server
	 */
	@Scheduled(fixedDelayString  = "${sync.source.schedule.fixed-delay:3000}")
	public void syncSource2I18nCron() {
		syncI18nSourceService.sendSourceToI18n();
	}

}
