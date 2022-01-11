/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	private static Logger logger = LoggerFactory.getLogger(SyncGrmSourceServiceImpl.class);

	private final static String LOCAL_STR = "local";
	private final static String GRM_STR = "grm";
	private final static int reqSplitLimit = 1024*1024*10;

	/** switch of the sync collection translation cached file **/
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

	// it represents the remote service is available.
	// private static boolean connected = true;
	private boolean grmConnected = false;

	/**
	 * 
	 * Synchronize the updated source to remote GRM server
	 *
	 */
	@Override
	public synchronized void sendSourceToGRM() {

		if (!syncEnabled || LOCAL_STR.equalsIgnoreCase(remoteGRMURL)) {
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
	
    /**
     * scan the GRM cached source file DIR and process the cached source file
     */
	private synchronized void processGRMQueueFiles() {
        
		//Get the all source cached files under L10N_TMP_GRM_PATH Directory 
		List<File> queueFiles = DiskQueueUtils.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_GRM_PATH));
		if (queueFiles == null) {
			return;
		}
		logger.debug("the GRM cache file size---{}", queueFiles.size());
        //Trace the file 
		for (File quefile : queueFiles) {
			try {
				//Read the source cached file and convert to Object
				Map<String, ComponentSourceDTO> mapObj = DiskQueueUtils.getQueueFile2Obj(quefile);
				for (Entry<String, ComponentSourceDTO> entry : mapObj.entrySet()) {
					ComponentSourceDTO cachedComDTO = entry.getValue();
					sendSource2GRM(cachedComDTO);
				}
				routeQueueFilePath(basePath, quefile);
			}catch (L10nAPIException e) {
				//process the network issue exception
				break;
			}catch (Exception e) {
				//process the read source error, convert json to object error 
				//and bigger source issue
				logger.error("Read source file to GRM error:" + quefile.getAbsolutePath(), e);
				DiskQueueUtils.moveFile2ExceptPath(basePath, quefile, GRM_STR);
				continue;
			}
		}
	}
	
    /**
     * process send the source to GRM by keys or one key
     * @param cachedComDTO
     * @throws L10nAPIException
     * @throws IOException
     */
	public void sendSource2GRM(ComponentSourceDTO cachedComDTO) throws L10nAPIException, IOException {
		try {
			if (!StringUtils.isEmpty(cachedComDTO) && isGrmConnected()) {
				//compare the GRM request body size, if the size bigger than limit, send
				//the source by one key
				if(cachedComDTO.toJSONString().getBytes(StandardCharsets.UTF_8).length > reqSplitLimit) {
					sendKeySource2GRM(cachedComDTO, remoteGRMURL);
				}else {
					remoteSyncService.send(cachedComDTO, remoteGRMURL);
				}
			}
		} catch (L10nAPIException e) {
			//process the network not work issue 
			logger.error("Send source file to GRM error:" + cachedComDTO.toJSONString(), e);
			setGrmConnected(false);
			throw new L10nAPIException(e.getMessage(), e);
		} 
	}
	
	/**
	 * send the source to GRM by one key if the request source bigger than reqSplitLimit
	 * @param componentSourceDTO
	 * @param remoteGRM
	 * @throws L10nAPIException
	 * @throws IOException
	 */
	private void sendKeySource2GRM(ComponentSourceDTO componentSourceDTO, String remoteGRM) throws L10nAPIException, IOException{
		@SuppressWarnings("unchecked")
		Set<Entry<String, String>> entrys = componentSourceDTO.getMessages().entrySet();
		for(Entry<String, String> entry : entrys) {
			ComponentSourceDTO comDTO = new ComponentSourceDTO();
			comDTO.setProductName(componentSourceDTO.getProductName());
			comDTO.setVersion(componentSourceDTO.getVersion());
			comDTO.setComponent(componentSourceDTO.getComponent());
			comDTO.setLocale(componentSourceDTO.getLocale());
			comDTO.setMessages(entry.getKey(), entry.getValue());
			try {
				remoteSyncService.send(comDTO, remoteGRM);
			} catch (L10nAPIException e) {
				//if the network issue, then it will throw L10nAPIException exception 
				remoteSyncService.ping(remoteGRMURL);
				//if not network issue, then it will print bigger source content
				// and throw the IOException exception
				String content = comDTO.toJSONString();
				int size = content.getBytes(StandardCharsets.UTF_8).length;
				String msg = String.format("Sync source to GRM failule: size: %s, productName: %s, version: %s, component: %s, locale: %s, key: %s", size, comDTO.getProductName(), comDTO.getVersion(), comDTO.getComponent(), comDTO.getLocale(), entry.getKey());
				logger.error(msg);
				throw new IOException(msg);
			}
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

}
