/*
 * Copyright 2019-2023 VMware, Inc.
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.source.service.SyncLocalBundleService;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentMessagesDTO;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

/**
 * 
 * This class used to merge the collection source to locale bundle
 *
 */
@Service
public class SyncLocalBundleServiceImpl implements SyncLocalBundleService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SyncLocalBundleServiceImpl.class);
	private final static String LOCAL_STR = "local";

	/** the path of local resource file,can be configured in spring config file **/
	@Value("${source.bundle.file.basepath}")
	private String basePath;
	
	/** the l10n server start type: bundle or S3 **/
	@Value("${spring.profiles.active}")
	private String activeDaoType;

	/** the url of GRM API,can be configured in spring config file **/
	@Value("${grm.server.url}")
	private String remoteGRMURL;

	/** the url of Singleton API,can be configured in spring config file **/
	@Value("${vip.server.url}")
	private String remoteVIPURL;

	@Autowired
	private SourceDao sourceDao;

	/**
	 * According to the remote url route the send file to GRM or I18n path
	 * 
	 * @param basePath
	 * @param source
	 * @throws IOException
	 */
	private void processSendFilePath(String basePath, File source) throws IOException {

		if (!this.remoteGRMURL.equalsIgnoreCase(LOCAL_STR)) {
			DiskQueueUtils.moveFile2GRMPath(basePath, source);
		} else if (!this.remoteVIPURL.equalsIgnoreCase(LOCAL_STR)) {
			DiskQueueUtils.moveFile2I18nPath(basePath, source);
		} else {
			DiskQueueUtils.delQueueFile(source);
		}

	}

	/**
	 * Synchronize the updated source to local bundle file
	 */
	@Override
	public synchronized void mergeSourceToLocalBundle() {
		
		LOGGER.debug("--Synchronize the updated source to local--");
		List<File> queueFiles = DiskQueueUtils.listSourceQueueFile(basePath);
		if (queueFiles == null) {
			return;
		}
		LOGGER.debug("the source cache file size---{}", queueFiles.size());
		for (File quefile : queueFiles) {
			try {
				Map<String, ComponentSourceDTO> mapObj = DiskQueueUtils.getQueueFile2Obj(quefile);
				for (Entry<String, ComponentSourceDTO> entry : mapObj.entrySet()) {

					String ehcachekey = entry.getKey();
					ComponentSourceDTO cachedComDTO = entry.getValue();
					if (!StringUtils.isEmpty(cachedComDTO)) {
						ComponentMessagesDTO sdto = new ComponentMessagesDTO();
						BeanUtils.copyProperties(cachedComDTO, sdto);
						boolean updateFlag = sourceDao.updateToBundle(sdto);
						if (!updateFlag) {
							throw new L10nAPIException("Failed to update source:" + ehcachekey);
						}

					}
				}
				processSendFilePath(this.basePath, quefile);
			} catch (L10nAPIException e) {
				LOGGER.warn(e.getMessage(), e);
				LOGGER.warn("The source cache file:{} will re-update to {}", quefile.getAbsolutePath(), this.activeDaoType);
				break;
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				DiskQueueUtils.moveFile2ExceptPath(basePath, quefile, LOCAL_STR);
			}
		}


	}

}
