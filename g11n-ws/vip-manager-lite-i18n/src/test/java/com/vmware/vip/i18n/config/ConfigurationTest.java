/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.config;

import org.apache.catalina.connector.Connector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.vip.LiteBootApplication;
import com.vmware.vip.core.Interceptor.APICacheControlInterceptor;
import com.vmware.vip.core.conf.ServerProperties;
import com.vmware.vip.core.conf.TomcatConfig;
import com.vmware.vip.core.conf.VIPTomcatConnectionCustomizer;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiteBootApplication.class)
public class ConfigurationTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	
	@Test
	public void testServerProperites() {
	    ServerProperties sp = webApplicationContext.getBean(ServerProperties.class);
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
        ServerProperties sp = new ServerProperties();
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
	public void test003VIPTomcatConnectionCustomizer(){
		 ServerProperties sp  = webApplicationContext.getBean(ServerProperties.class);
		VIPTomcatConnectionCustomizer vcs = new VIPTomcatConnectionCustomizer(sp, "on", 123);
		vcs.customize(new Connector());
	}
	
	@Test
	public void test004VIPTomcatConfig(){
		TomcatConfig tc  = webApplicationContext.getBean(TomcatConfig.class);
		ServerProperties sp = webApplicationContext.getBean(ServerProperties.class);
		tc.servletContainer(sp);
	}
   //APICacheControlInterceptor
	@Test
	public void test005APICacheControlInterceptor(){
		APICacheControlInterceptor aci = new APICacheControlInterceptor("maxage=1024");
		try {
			aci.afterCompletion(null, null, null, null);
		} catch (Exception e) {
		}
	}
}
