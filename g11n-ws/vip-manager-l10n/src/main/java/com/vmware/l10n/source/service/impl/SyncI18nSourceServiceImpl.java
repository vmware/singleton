/*
 * Copyright 2019-2023 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.l10n.source.service.SyncI18nSourceService;
import com.vmware.l10n.utils.DiskQueueUtils;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.APIV2;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.exceptions.VIPHttpException;
import com.vmware.vip.common.http.HTTPRequester;
import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

/**
 * 
 * This class used to send the collection source to singleton server
 *
 */

@Service
public class SyncI18nSourceServiceImpl implements SyncI18nSourceService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(SyncI18nSourceServiceImpl.class);
	private final static String LOCAL_STR = "local";
	private final static String I18N_STR = "i18n";

	/** the path of local resource file,can be configured in spring config file **/
	@Value("${source.bundle.file.basepath}")
	private String basePath;

	/** the url of Singleton API,can be configed in spring config file **/
	@Value("${vip.server.url}")
	private String remoteVIPURL;

	@Value("${vip.server.authentication.enable}")
	private boolean remoteVIPAuthEnable;

	@Value("${vip.server.authentication.appId:#}")
	private String remoteVIPAuthAppId;

	@Value("${vip.server.authentication.token:#}")
	private String remoteVIPAuthAppToken;

	/**
	 * it represents the remote singleton service is available.
	 */
	private boolean singletonConnected = false;

	/**
	 * 
	 * Synchronize the updated source to remote Singleton server
	 *
	 */
	@Override
	public void sendSourceToI18n() {
		if (LOCAL_STR.equalsIgnoreCase(remoteVIPURL)) {
			return;
		}
		try {
			pingSingleton(this.remoteVIPURL);
			setSingletonConnected(true);
			processSingletonQueueFiles();
		} catch (L10nAPIException e) {
			LOGGER.error("Remote [" + remoteVIPURL + "] is not connected.", e);
			setSingletonConnected(false);
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
     * scan the Singleton cached source file DIR and process the cached source file
     */
	private synchronized void processSingletonQueueFiles() {

		List<File> queueFiles = DiskQueueUtils.listQueueFiles(new File(basePath + DiskQueueUtils.L10N_TMP_I18N_PATH));
		if (queueFiles == null) {
			return;
		}
		LOGGER.debug("the Singleton cache file size---{}", queueFiles.size());

		for (File quefile : queueFiles) {
			try {
				Map<String, ComponentSourceDTO> mapObj = DiskQueueUtils.getQueueFile2Obj(quefile);
				for (Entry<String, ComponentSourceDTO> entry : mapObj.entrySet()) {
					ComponentSourceDTO cachedComDTO = entry.getValue();
					sendData2RemoteVIP(cachedComDTO);
				}
				DiskQueueUtils.moveFile2IBackupPath(basePath, quefile, I18N_STR);
			} catch (VIPHttpException e) {
				LOGGER.error("Send source file to Singleton error:", e);
				break;
			} catch (Exception e) {
				LOGGER.error("Read source file from singleton directory error:" + quefile.getAbsolutePath(), e);
				DiskQueueUtils.moveFile2ExceptPath(basePath, quefile, I18N_STR);
			} 
		}
	}

	/**
	 * process send the source to Singleton by ComponentSourceDTO object
	 * @param cachedComDTO
	 * @throws VIPHttpException
	 */
	private void sendData2RemoteVIP(ComponentSourceDTO cachedComDTO) throws VIPHttpException {
		
			if (!StringUtils.isEmpty(cachedComDTO) && isSingletonConnected()) {
				String urlStr = remoteVIPURL + APIV2.PRODUCT_TRANSLATION_PUT
						.replace("{" + APIParamName.PRODUCT_NAME + "}", cachedComDTO.getProductName())
						.replace("{" + APIParamName.VERSION2 + "}", cachedComDTO.getVersion());
				String locale = ConstantsKeys.LATEST;
				String jsonStr = "{\"data\":{\"productName\": \"" + cachedComDTO.getProductName()
						+ "\",\"pseudo\": false,\"translation\": [{\"component\": \"" + cachedComDTO.getComponent()
						+ "\",\"locale\": \"" + locale + "\",\"messages\": " + cachedComDTO.getMessages().toJSONString()
						+ "}],\"version\": \"" + cachedComDTO.getVersion() + "\"},\"requester\": \""
						+ ConstantsKeys.VL10N + "\"}";
				Map<String, String> header = new HashMap<String, String>();
				header.put(ConstantsKeys.CSP_AUTH_TOKEN, ConstantsKeys.VL10N);
				if (remoteVIPAuthEnable) {
					header.put("appId", remoteVIPAuthAppId);
					header.put("token", remoteVIPAuthAppToken);
				}
				HTTPRequester.putJSONStr(jsonStr, urlStr, header);
			}else if(!isSingletonConnected()) {
				throw new VIPHttpException("remote singleton service not available");
			}
	}

	/**
	 * the singleton server heart beat test 
	 * @param remoteURL
	 * @throws L10nAPIException
	 */
	private void pingSingleton(String remoteURL) throws L10nAPIException {
		String reqUrl = remoteURL + APIV2.BROWSER_LOCALE;
		Map<String, String> header = null;
		if (remoteVIPAuthEnable) {
			header = new HashMap<String, String>();
			header.put("appId", remoteVIPAuthAppId);
			header.put("token", remoteVIPAuthAppToken);
		}
		if (StringUtils.isEmpty(HTTPRequester.getData(reqUrl, "GET", header))) {
			throw new L10nAPIException("Error occur when send to singleton [" + reqUrl + "].");
		}
	}

	private boolean isSingletonConnected() {
		return singletonConnected;
	}

	private void setSingletonConnected(boolean singletonConnected) {
		this.singletonConnected = singletonConnected;
	}

	public String getBasePath() {
		return basePath;
	}

}
