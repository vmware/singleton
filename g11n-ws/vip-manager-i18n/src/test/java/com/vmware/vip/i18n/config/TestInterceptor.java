/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.i18n.config;

import org.junit.Test;

import com.vmware.vip.core.Interceptor.APISecurityInterceptor;
import com.vmware.vip.core.conf.ServerProperties;

public class TestInterceptor {

	@Test
	public void test002APISecurityInterceptor() {
		APISecurityInterceptor interceptor = new APISecurityInterceptor();
		try {
			interceptor.preHandle(null, null, null);
			interceptor.postHandle(null, null, null, null);
			interceptor.afterCompletion(null, null, null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
