/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.crons;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.l10n.source.service.RemoteSyncService;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

@Service
public class SourceSendGRMCron {
	
	private static Logger logger = LoggerFactory.getLogger(SourceSendGRMCron.class);
	
	private final static String LOCAL_STR = "local";
	private final static String GRM_STR = "grm";
	
	/**switch of the sync collection translation cached file**/
	@Value("${sync.source.enable}")
	private boolean syncEnabled;
	
	/** the path of local resource file,can be configured in spring config file **/
	@Value("${source.bundle.file.basepath}")
	private String basePath;
	
	/** the url of GRM API,can be configured in spring config file **/
	@Value("${grm.server.url}")
	private String remoteGRMURL;

	/** the url of Singleton API,can be configured in spring config file **/
	@Value("${vip.server.url}")
	private String remoteVIPURL;
	
	@Autowired
	private RemoteSyncService remoteSyncService;

	//it represents the remote service is available.
	//private static boolean connected = true;
	private static boolean grmConnected = false;
	
	
	/**
	 * 
	 * Synchronize the updated source to remote GRM server
	 *
	 */
	@Scheduled(cron = "${sync.source.schedule.cron}")
	public void syncSource2GRMCron(){
		if (!syncEnabled) {
			return;
		}
		try {
			remoteSyncService.ping(remoteGRMURL);
			setGrmConnected(true);
			processGRMQueueFiles();
		} catch (L10nAPIException e) {
			setGrmConnected(false);
			logger.error("Remote [" + remoteGRMURL + "] is not connected.", e);

		}
	}
	
	
	private synchronized void processGRMQueueFiles() {
        
		List<File> queueFiles = DiskQueueUtils.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_GRM_PATH));
		if (queueFiles == null) {
			return;
		}
		logger.debug("the GRM cache file size---{}", queueFiles.size());
		
		for (File quefile : queueFiles) {
			try {
			Map<String, ComponentSourceDTO> mapObj = DiskQueueUtils.getQueueFile2Obj(quefile);
			for (Entry<String, ComponentSourceDTO> entry : mapObj.entrySet()) {
				ComponentSourceDTO cachedComDTO = entry.getValue();
				sendSource2GRM(cachedComDTO);
			}
			routeQueueFilePath(basePath, quefile);
			} catch (IOException e) {
				logger.error("Read source file to GRM error:"+ quefile.getAbsolutePath(), e);
				DiskQueueUtils.moveFile2ExceptPath(basePath, quefile, GRM_STR);
				continue;
			} catch (L10nAPIException e) {
				break;
			}
		}
	}

	
	

	public void sendSource2GRM(ComponentSourceDTO cachedComDTO) throws L10nAPIException {
		try {
		if (!StringUtils.isEmpty(cachedComDTO) && isGrmConnected()) {
			  remoteSyncService.send(cachedComDTO, remoteGRMURL);
		}
		} catch (L10nAPIException e) {
			logger.error("Send source file to GRM error:"+ cachedComDTO.toJSONString(), e);
			setGrmConnected(false);
			throw e;
		}
	}
	
	
	/**
	 * According to the remote url route the send file to I18n DIR or backup DIR
	 * @param basePath
	 * @param source
	 * @throws IOException
	 */
	private void routeQueueFilePath(String basePath, File source) throws IOException {

		if(this.remoteVIPURL.equalsIgnoreCase(LOCAL_STR)) {
			DiskQueueUtils.moveFile2IBackupPath(basePath, source, GRM_STR);
		}else {
			DiskQueueUtils.moveFile2I18nPath(basePath, source);
		}
	}
		

	public static boolean isGrmConnected() {
		return grmConnected;
	}

	public static void setGrmConnected(boolean grmConnected) {
		SourceSendGRMCron.grmConnected = grmConnected;
	}

	
}
