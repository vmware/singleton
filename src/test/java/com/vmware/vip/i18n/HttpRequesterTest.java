/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.HashMap;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;

public class HttpRequesterTest extends BaseTestClass {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8089);

	@Before
	public void init() {
		VIPCfg cfg = VIPCfg.getInstance();
		cfg.initialize("vipconfig");
		cfg.setVipServer("http://localhost:8089");
		cfg.setInitializeCache(false);
		cfg.initializeVIPService();
		cfg.createTranslationCache(MessageCache.class);
		cfg.createFormattingCache(FormattingCache.class);
		I18nFactory.getInstance(cfg);

	}

	@Test
	public void addHeaderParamsTest() {
		String url = "/i18n/api/v2/.*";


		HashMap<String, String> params = new HashMap<>();
		String key1 = "key-1";
		String value1 = "value-1";
		String key2 = "key-2";
		String value2 = "value-2";
		params.put(key1, value1);
		params.put(key2, value2);
		VIPCfg.getInstance().getVipService().setHeaderParams(params);

		WireMock.stubFor(WireMock.get(WireMock.urlMatching(url)).willReturn(WireMock.aResponse().withStatus(200)));

		TranslationMessage tm = (TranslationMessage) I18nFactory.getInstance()
				.getMessageInstance(TranslationMessage.class);
		tm.getString2("default", "JAVA", new Locale("zh", "Hans"), "table.host");

		WireMock.verify(WireMock.getRequestedFor(WireMock.urlMatching(url)).withHeader(key1, WireMock.equalTo(value1)).withHeader(key2, WireMock.equalTo(value2)));
	}
}
