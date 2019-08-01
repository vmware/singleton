/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n;

import java.util.Random;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vmware.vipclient.i18n.VIPCfg;

public class BaseTestClass {
	protected Logger logger;
	VIPCfg vipCfg = VIPCfg.getInstance();

	@Rule
	public final TestRule watchman = new TestWatcher() {
		@Override
		public Statement apply(Statement base, Description description) {
			logger = LoggerFactory.getLogger(description.getTestClass().getSimpleName());
			return super.apply(base, description);
		}

		@Override
		protected void failed(Throwable e, Description description) {
			logger.error(description.getMethodName()+" Failed.", e);
		}

		@Override
		protected void starting(Description description) {
			logger.info("Statring test: " + description.getMethodName());
		}
	};
	
	
	
	protected String getSaltString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 10) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}

}
