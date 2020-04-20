/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.service.impl;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.vmware.vip.common.l10n.exception.L10nAPIException;
import com.vmware.l10n.source.dao.SourceDao;
import com.vmware.l10n.source.service.RemoteSyncService;
import com.vmware.vip.api.rest.APIParamName;
import com.vmware.vip.api.rest.l10n.L10NAPIV1;
import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.constants.ConstantsUnicode;
import com.vmware.vip.common.l10n.source.dto.ComponentSourceDTO;

/**
 * This implementation of interface SourceService.
 */
@Service
public class RemoteSyncServicempl implements RemoteSyncService {

	@Autowired
	private SourceDao sourceDao;

	public void ping(String remoteURL) throws L10nAPIException {
		ComponentSourceDTO componentSourceDTO = new ComponentSourceDTO();
		componentSourceDTO.setProductName("test");
		componentSourceDTO.setVersion("test");
		componentSourceDTO.setLocale("test");
		componentSourceDTO.setComponent("test");
		componentSourceDTO.setComments("", "");
		componentSourceDTO.setMessages("", "");
		send(componentSourceDTO, remoteURL);
	}

	/*
	 * synchronize the updated source GRM if the switch is on.
	 */
	@SuppressWarnings("unchecked")
	public void send(ComponentSourceDTO componentSourceDTO, String remoteURL)
			throws L10nAPIException {
		boolean pushFlag = false;
		if (!StringUtils.isEmpty(componentSourceDTO)) {
			StringBuilder url = new StringBuilder();
			url.append(remoteURL).append(
					L10NAPIV1.GRM_SEND_SOURCE
							.replace("{" + APIParamName.PRODUCT_NAME + "}",
									componentSourceDTO.getProductName())
							.replace("{" + APIParamName.VERSION + "}",
									componentSourceDTO.getVersion())
							.replace(
									"{" + APIParamName.COMPONENT + "}",
									componentSourceDTO.getComponent()).replace(
											"{" + APIParamName.LOCALE + "}",
											ConstantsUnicode.EN));
			JSONObject requestParam = new JSONObject();
			requestParam.put(ConstantsKeys.MESSAGES,
					componentSourceDTO.getMessages());
			requestParam.put(ConstantsKeys.COMMENTS,
					componentSourceDTO.getComments());
			pushFlag = sourceDao.sendToRemote(url.toString(), requestParam);
		}
		if (!pushFlag) {
			throw new L10nAPIException("Error occur when send to remote ["
					+ remoteURL + "].");
		}
	}

}
