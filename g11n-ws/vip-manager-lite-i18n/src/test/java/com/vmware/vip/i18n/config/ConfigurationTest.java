/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.config;

import org.apache.catalina.connector.Connector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.vip.LiteBootApplication;
import com.vmware.vip.core.Interceptor.LiteAPICacheControlInterceptor;
import com.vmware.vip.core.conf.LiteServerProperties;
import com.vmware.vip.core.conf.LiteTomcatConfig;
import com.vmware.vip.core.conf.LiteTomcatConnectionCustomizer;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiteBootApplication.class)
public class ConfigurationTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	
	@Test
	public void testServerProperites() {
	    LiteServerProperties sp = webApplicationContext.getBean(LiteServerProperties.class);
	    sp.getServerPort();
	    sp.getServerScheme();
	    sp.getHttpPort();
	    sp.getHttpsKeyStore();
	    sp.getHttpsKeyStorePassword();
	    sp.getHttpsKeyAlias();
	    sp.isAllowTrace();
	}
	
	
	@Test
    public void testServerProperites1() {
		LiteServerProperties sp = new LiteServerProperties();
        sp.setServerPort(300);
        sp.setServerScheme("testScheme");
        sp.setHttpPort(123);
        sp.getServerPort();
        sp.getServerScheme();
        sp.getHttpPort();
        sp.setHttpsKeyStore("testkeyStore");
        sp.getHttpsKeyStore();
        sp.setHttpsKeyStorePassword("testpassword");
        sp.getHttpsKeyStorePassword();
        sp.setHttpsKeyAlias("testAlias");
        sp.getHttpsKeyAlias();
        sp.setAllowTrace(true);
        sp.isAllowTrace();
    }

	@Test
	//VIPTomcatConnectionCustomizer
	public void test003VIPTomcatConnectionCustomizer(){
		LiteServerProperties sp  = webApplicationContext.getBean(LiteServerProperties.class);
		LiteTomcatConnectionCustomizer vcs = new LiteTomcatConnectionCustomizer(sp, "on", 2048);
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		vcs.customize(connector);
	}
	
	
	@Test
	public void test004TomcatConfig(){
		LiteServerProperties sp  = webApplicationContext.getBean(LiteServerProperties.class);
		 LiteTomcatConfig tc = webApplicationContext.getBean(LiteTomcatConfig.class);
		 tc.servletContainer(sp);
	}
	
   //APICacheControlInterceptor
	@Test
	public void test005APICacheControlInterceptor(){
		LiteAPICacheControlInterceptor aci = new LiteAPICacheControlInterceptor("maxage=1024");
		try {
			aci.afterCompletion(null, null, null, null);
		} catch (Exception e) {
		}
	}
	

}
