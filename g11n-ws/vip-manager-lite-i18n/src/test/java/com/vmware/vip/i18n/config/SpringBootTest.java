/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.config;

import com.vmware.vip.common.constants.ConstantsTomcat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vip.LiteBootApplication;



public class SpringBootTest {
	private static Logger logger = LoggerFactory.getLogger(SpringBootTest.class);

	@Test
	public void test001serviceswich() {
		String[] args = { "-c" };
		try {
			System.setProperty("server.scheme", ConstantsTomcat.HTTP_HTTPS);
		    LiteBootApplication.main(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
		

		logger.info("TestSpringBoot");
	}


}
