/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import com.vmware.vip.BootApplication;
import com.vmware.vip.core.conf.SwaggerConfig;

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
}
