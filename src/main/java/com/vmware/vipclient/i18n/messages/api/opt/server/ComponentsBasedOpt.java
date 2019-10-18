/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.util.Set;

import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.exceptions.VIPJavaClientException;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.StringUtil;

public class ComponentsBasedOpt extends BaseOpt implements Opt {
	private final Logger logger = LoggerFactory.getLogger(ComponentsBasedOpt.class.getName());
	private final Set<String> components;
	private final Set<String> locales;

	/**
	 * @param components
	 * @param locales
	 */
	public ComponentsBasedOpt(Set<String> components, Set<String> locales) {
		this.components = components;
		this.locales = locales;
	}

	public JSONArray getComponentsMessages() {
		String url = V2URL.getComponentsTranslationURL(components, locales,
				VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());

		String responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(url, ConstantsKeys.GET, null);

		if (StringUtil.isEmpty(responseStr)) {
			throw new VIPJavaClientException("Server returns empty.");
		}

		int statusCode = Integer.parseInt(getStatusFromResponse(responseStr, ConstantsKeys.CODE).toString());
		if (statusCode < 200 || statusCode > 299) {
			throw new VIPJavaClientException(
					String.format("Server returns error! Status: %d. Message: %s", statusCode,
							getStatusFromResponse(responseStr, ConstantsKeys.MESSAGE)));
		}

		JSONArray bundles = (JSONArray) getMessagesFromResponse(responseStr, ConstantsKeys.BUNDLES);
		if (null == bundles) {
			throw new VIPJavaClientException("Unknown server error.");
		}

		return bundles;
	}
}
