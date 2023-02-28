/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.synch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.l10n.synch.init.SynchInfoService;
import com.vmware.l10n.synch.model.SynchFile2GitReq;
import com.vmware.l10n.synch.model.UpdateSyncInfoResp;
import com.vmware.l10n.synch.schedule.Send2GitSchedule;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;
import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;

/**
 * 
 *
 * @author shihu
 *
 */


@RestController
public class TranslationCommitGitController {
	
private static Logger logger = LoggerFactory.getLogger(TranslationCommitGitController.class); 

 @CrossOrigin
 @RequestMapping(value = L10NAPIV1.SYNC_TRANSLATION_GIT_L10N, method = RequestMethod.POST)
 @ResponseStatus(HttpStatus.OK)
  public Response notifyCommit(@RequestBody SynchFile2GitReq synchFile2GitReq) {
	  
	
	  try {
		Send2GitSchedule.Send2GitQueue.put(synchFile2GitReq);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		logger.error(e.getMessage(), e);
		Thread.currentThread().interrupt();
		return APIResponseStatus.INTERNAL_SERVER_ERROR;
	}
	 
	return APIResponseStatus.OK;
	  
  }
  
  
 @CrossOrigin
 @RequestMapping(value = L10NAPIV1.SYNC_TRANSLATION_CACHE_L10N, method = RequestMethod.GET)
 @ResponseStatus(HttpStatus.OK)
  public  UpdateSyncInfoResp updateCacheCommit() {
	return  SynchInfoService.getCacheToken();
  }
  
  
  

}
