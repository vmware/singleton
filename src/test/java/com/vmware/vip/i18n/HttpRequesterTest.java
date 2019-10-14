/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.io.IOException;
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

	private static String mockServer = "http://localhost:8089";

	private String realServer = null;

	@Before
	public void init() throws IOException {
		VIPCfg cfg = VIPCfg.getInstance();
		cfg.initialize("vipconfig.yaml");
		setConfig(cfg, "vipServer", mockServer);
		cfg.setInitializeCache(false);
		cfg.initializeVIPService();
		cfg.createTranslationCache(MessageCache.class);
		cfg.createFormattingCache(FormattingCache.class);
		I18nFactory.getInstance(cfg);
		realServer = VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL();
		VIPCfg.getInstance().getVipService().getHttpRequester().setBaseURL(mockServer);
	}

	@Test
	public void addHeaderParamsTest() {
		String url = "/i18n/api/v2/translation/products/JavaclientTest/versions/1.0.0/locales/[^/]*?/components/default\\?pseudo=false";

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

	@After
	public void teardown() {
		VIPCfg.getInstance().getVipService().getHttpRequester().setBaseURL(realServer);
	}
}
