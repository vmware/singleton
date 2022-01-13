/*******************************************************************************
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.vmware.vip.test.javaclient.httprequester;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Locale;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.vmware.vip.test.common.Utils;
import com.vmware.vip.test.common.annotation.TestCase;
import com.vmware.vip.test.javaclient.Constants;
import com.vmware.vip.test.javaclient.TestBase;
import com.vmware.vipclient.i18n.I18nFactory;
import com.vmware.vipclient.i18n.VIPCfg;
import com.vmware.vipclient.i18n.base.cache.FormattingCache;
import com.vmware.vipclient.i18n.base.cache.MessageCache;
import com.vmware.vipclient.i18n.base.instances.TranslationMessage;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class CustomizeHeader extends TestBase{
	private TranslationMessage tm = null;
	private static String mockHost = "localhost";
	private static int mockPort = 8089;
	private static String mockServer = String.format("http://%s:%s", mockHost, mockPort);
	private String originalURL = null;

	@BeforeClass
	public void preparing() throws Exception {
		initVIPServer();
	}

	public void initVIPServer() throws Exception {
		VIPCfg vipCfg = VIPCfg.getInstance();
		vipCfg.initialize(Utils.removeFileExtension(Constants.VIP_CONFIG_FILE_NAME));
		vipCfg.initializeVIPService();
		vipCfg.createTranslationCache(MessageCache.class);
		vipCfg.createFormattingCache(FormattingCache.class);
		tm = (TranslationMessage) I18nFactory.getInstance(vipCfg)
				.getMessageInstance(TranslationMessage.class);
		originalURL = VIPCfg.getInstance().getVipService().getHttpRequester().getBaseURL();
		VIPCfg.getInstance().getVipService().getHttpRequester().setBaseURL(mockServer);
	}

	@Test(enabled=true, priority=0)
	@TestCase(id = "001", name = "Add HeaderParams", description = "test desc")
	public void addHeaderParameters() {
		WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(mockPort));
	    wireMockServer.start();
	    String url = "/i18n/api/v2/translation/products/JavaClient/versions/1.0.0/locales/[^/]*?/components/default\\?pseudo=false";
	    String url2 = "/i18n/api/v2/translation/products/JavaClient/versions/1.0.0/localelist";

		HashMap<String, String> params = new HashMap<>();
		String key1 = "key1";
		String value1 = "value1";
		String key2 = "key-2";
		String value2 = "value-2";
		params.put(key1, value1);
		params.put(key2, value2);
		VIPCfg.getInstance().getVipService().setHeaderParams(params);

		WireMock.configureFor(mockHost, mockPort);
		WireMock.stubFor(WireMock.get(WireMock.urlMatching(url)).willReturn(
				WireMock.aResponse().withStatus(200)));
		WireMock.stubFor(WireMock.get(WireMock.urlMatching(url2)).willReturn(
				WireMock.aResponse().withStatus(200)));

		tm.getString2("default", "JavaClient", new Locale("zh", "CN"), "table.host");
		try {
			WireMock.verify(WireMock.getRequestedFor(WireMock.urlMatching(url2+"|"+url)).
					withHeader(key1, WireMock.equalTo(value1)).withHeader(key2, WireMock.equalTo(value2)));
			log.verifyTrue("Verify parameters in header", true);
		} catch(VerificationException e) {
			log.verifyTrue("Verify parameters in header", false, e.toString());
		} finally {
			wireMockServer.stop();
		}
	}
	
	@Test(enabled=true, priority=0)
	@TestCase(id = "002", name = "Add HeaderParams", description = "test desc")
	public void addHeaderParameters_getMessage() {
		
		WireMockServer wireMockServer = new WireMockServer(wireMockConfig().port(mockPort));
		wireMockServer.start();
		String url = "/i18n/api/v2/translation/products/JavaClient/versions/1.0.0/locales/[^/]*?/components/default\\?pseudo=false";
		String url2 = "/i18n/api/v2/translation/products/JavaClient/versions/1.0.0/localelist";
		
		HashMap<String, String> params = new HashMap<>();
		String key1 = "key1";
		String value1 = "value1";
		String key2 = "key-2";
		String value2 = "value-2";
		params.put(key1, value1);
		params.put(key2, value2);
		VIPCfg.getInstance().getVipService().setHeaderParams(params);

		WireMock.configureFor(mockHost, mockPort);
		WireMock.stubFor(WireMock.get(WireMock.urlMatching(url)).willReturn(WireMock.aResponse().withStatus(200)));
		WireMock.stubFor(WireMock.get(WireMock.urlMatching(url2)).willReturn(WireMock.aResponse().withStatus(200)));

		
		
		try {
			tm.getMessage(new Locale("zh", "CN"), "default", "messages.welcome1");
        } catch (Exception e) {
        	// Exception is expected
        }
		
		try {
			WireMock.verify(WireMock.getRequestedFor(WireMock.urlMatching(url2+"|"+url)).
					withHeader(key1, WireMock.equalTo(value1)).withHeader(key2, WireMock.equalTo(value2)));
			log.verifyTrue("Verify parameters in header1", true);
		} catch(VerificationException e) {
			e.printStackTrace();
			log.verifyTrue("Verify parameters in header2", false, e.toString());
		} finally {
			wireMockServer.stop();
		}
	}

	@AfterClass
	public void releaseMock() {
		VIPCfg.getInstance().getVipService().getHttpRequester().setBaseURL(originalURL);
	}
}
