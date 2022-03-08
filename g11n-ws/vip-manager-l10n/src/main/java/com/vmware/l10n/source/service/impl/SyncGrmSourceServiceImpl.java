/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.l10n.source.service.RemoteSyncService;
import com.vmware.l10n.source.service.SyncGrmSourceService;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

/**
 * 
 * This class used to send the collection source to GRM server
 *
 */
@Service
public class SyncGrmSourceServiceImpl implements SyncGrmSourceService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SyncGrmSourceServiceImpl.class);
	private final static String LOCAL_STR = "local";
	private final static String GRM_STR = "grm";
	private final static long REQ_SPLIT_LIMIT = 1024*1024*10;

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

	/**
	 * it represents the remote service is available.
	 */
	private boolean grmConnected = false;

	/**
	 * 
	 * Synchronize the updated source to remote GRM server
	 *
	 */
	@Override
	public synchronized void sendSourceToGRM() {

		if (LOCAL_STR.equalsIgnoreCase(remoteGRMURL)) {
			return;
		}
		try {
			remoteSyncService.ping(remoteGRMURL);
			setGrmConnected(true);
			processGRMQueueFiles();
		} catch (L10nAPIException e) {
			setGrmConnected(false);
			LOGGER.error("Remote [" + remoteGRMURL + "] is not connected.", e);
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
    /**
     * scan the GRM cached source file DIR and process the cached source file
     * @throws IOException 
     */
	private void processGRMQueueFiles() throws IOException {
        
		List<File> queueFiles = DiskQueueUtils.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_GRM_PATH));
		if (queueFiles == null) {
			return;
		}
		LOGGER.debug("the GRM cache file size---{}", queueFiles.size());
        
		for (File quefile : queueFiles) {
			if(quefile.length()>REQ_SPLIT_LIMIT) {
				DiskQueueUtils.moveFile2ExceptPath(basePath, quefile, GRM_STR);
			}else {
				try {
					Map<String, ComponentSourceDTO> mapObj = DiskQueueUtils.getQueueFile2Obj(quefile);
					for (Entry<String, ComponentSourceDTO> entry : mapObj.entrySet()) {
						ComponentSourceDTO cachedComDTO = entry.getValue();
						sendData2GRM(cachedComDTO);
					}
					routeQueueFilePath(basePath, quefile);
				}catch (L10nAPIException e) {
					LOGGER.error("Send source file to GRM error:", e);
					break;
				}
			}
		}
	}
	
    /**
     * process send the source to GRM by keys
     * @param cachedComDTO
     * @throws L10nAPIException
     * @throws IOException
     */
	private void sendData2GRM(ComponentSourceDTO cachedComDTO) throws L10nAPIException{
	
			if (!StringUtils.isEmpty(cachedComDTO) && isGrmConnected()) {	
				remoteSyncService.send(cachedComDTO, remoteGRMURL);
			}else if(!isGrmConnected()) {
				throw new L10nAPIException("Remote [" + remoteGRMURL + "] is not connected.");
			}
	}
	
	
	/**
	 * According to the remote url route the send file to I18n DIR or backup DIR
	 * 
	 * @param basePath
	 * @param source
	 * @throws IOException
	 */
	private void routeQueueFilePath(String basePath, File source) throws IOException {

		if (this.remoteVIPURL.equalsIgnoreCase(LOCAL_STR)) {
			DiskQueueUtils.moveFile2IBackupPath(basePath, source, GRM_STR);
		} else {
			DiskQueueUtils.moveFile2I18nPath(basePath, source);
		}
	}

	private boolean isGrmConnected() {
		return this.grmConnected;
	}

	private void setGrmConnected(boolean grmConnected) {
		this.grmConnected = grmConnected;
	}

	public String getBasePath() {
		return basePath;
	}

}
