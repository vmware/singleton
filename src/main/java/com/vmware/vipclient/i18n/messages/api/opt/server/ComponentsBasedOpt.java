/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.util.ConstantsKeys;

public class ComponentsBasedOpt extends BaseOpt implements Opt {
	private final Logger logger = LoggerFactory.getLogger(ComponentsBasedOpt.class.getName());
	private final List<String> components;
	private final String locale;

	/**
	 * @param components
	 * @param locale
	 */
	public ComponentsBasedOpt(List<String> components, String locale) {
		this.components = components;
		this.locale = locale;
	}

	public JSONObject getComponentsMessages() {
		String url = V2URL.getComponentsTranslationURL(components, locale, VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());
		if(ConstantsKeys.LATEST.equals(locale)) {
			url =  url.replace("pseudo=false", "pseudo=true");
		}
		String responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(url, ConstantsKeys.GET, null);
		if (null == responseStr || responseStr.equals("")) {
			return null;
		} else {
			if(ConstantsKeys.LATEST.equals(locale)) {
				responseStr = responseStr.replace(ConstantsKeys.PSEUDOCHAR, "");
			}

			final JSONObject msgObject = (JSONObject) this.getMessagesFromResponse(responseStr,
					ConstantsKeys.MESSAGES);

			return msgObject;
		}
	}
}
