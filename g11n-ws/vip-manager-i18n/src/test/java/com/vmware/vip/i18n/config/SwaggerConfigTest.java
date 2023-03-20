/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.vip.BootApplication;
import com.vmware.vip.core.conf.ServerProperties;
import com.vmware.vip.core.conf.SwaggerConfig;
import com.vmware.vip.core.conf.TomcatConfig;
import com.vmware.vip.core.conf.VIPTomcatConnectionCustomizer;
import com.vmware.vip.i18n.api.v1.common.ConstantsForTest;
import com.vmware.vip.i18n.api.v1.common.RequestUtil;

import org.apache.catalina.connector.Connector;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootApplication.class)
public class SwaggerConfigTest {
	private static Logger logger = LoggerFactory.getLogger(SwaggerConfigTest.class);
	
	@Autowired
	private WebApplicationContext webApplicationContext;

	/*@Test
	public void test001servicelogin() {
		SwaggerConfig conf = webApplicationContext.getBean(SwaggerConfig.class);
	    
		conf.serviceloginApi();
		logger.info("test001servicelogin");
	}*/

	@Test
	public void test001serviceswich() {
		SwaggerConfig conf = webApplicationContext.getBean(SwaggerConfig.class);
		conf.getAuthConfig().setAuthSwitch("true");
		conf.createRestApi1();
		conf.createRestApi2();
		logger.info("createRestApi1");
		logger.info("createRestApi2");
	}
	
	@Test
	public void test002ServerProperties() {
		ServerProperties sp  = webApplicationContext.getBean(ServerProperties.class);
		sp.setMaxHttpHeaderSize(8192);
		sp.getHttpPort();
		sp.getHttpsKeyAlias();
		sp.getHttpsKeyStore();
		sp.getHttpsKeyStorePassword();
		sp.getHttpsKeyStoreType();
		sp.getServerPort();
		sp.getServerScheme();
	}
	@Test
	//VIPTomcatConnectionCustomizer
	public void test003VIPTomcatConnectionCustomizer(){
		 ServerProperties sp  = webApplicationContext.getBean(ServerProperties.class);
		VIPTomcatConnectionCustomizer vcs = new VIPTomcatConnectionCustomizer(sp, "on", 2048);
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		vcs.customize(connector);
	}
	
	
	@Test
	public void test004TomcatConfig(){
		 ServerProperties sp  = webApplicationContext.getBean(ServerProperties.class);
		 TomcatConfig tc = webApplicationContext.getBean(TomcatConfig.class);
		 tc.servletContainer(sp);
	}
	
}
