/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt.server;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.messages.api.opt.BaseOpt;
import com.vmware.vipclient.i18n.messages.api.opt.Opt;
import com.vmware.vipclient.i18n.messages.api.url.V2URL;
import com.vmware.vipclient.i18n.util.ConstantsKeys;
import com.vmware.vipclient.i18n.util.StringUtil;

public class ComponentsBasedOpt extends BaseOpt implements Opt {
	private final Logger logger = LoggerFactory.getLogger(ComponentsBasedOpt.class.getName());
	private final List<String> components;
	private final List<Locale> locales;

	/**
	 * @param components
	 * @param locales
	 */
	public ComponentsBasedOpt(List<String> components, List<Locale> locales) {
		this.components = components;
		this.locales = locales;
	}

	public JSONObject getComponentsMessages() {
		List<String> localestrList = locales.stream().map(Locale::toLanguageTag)
				.collect(Collectors.toList());

		String url = V2URL.getComponentsTranslationURL(components, localestrList,
				VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL());
		//		if(ConstantsKeys.LATEST.equals(locales)) {
		//			url =  url.replace("pseudo=false", "pseudo=true");
		//		}
		String responseStr = VIPCfg.getInstance().getVipService().getHttpRequester().request(url, ConstantsKeys.GET, null);
		if (StringUtil.isEmpty(responseStr)) {
			return null;
		} else {
			//			if(ConstantsKeys.LATEST.equals(locales)) {
			//				responseStr = responseStr.replace(ConstantsKeys.PSEUDOCHAR, "");
			//			}

			return (JSONObject) this.getMessagesFromResponse(responseStr, ConstantsKeys.MESSAGES);
		}
	}
}
