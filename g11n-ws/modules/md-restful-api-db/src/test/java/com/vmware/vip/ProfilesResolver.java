/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip;

import org.springframework.test.context.ActiveProfilesResolver;

public class ProfilesResolver implements ActiveProfilesResolver{
	
		@Override
		public String[] resolve(Class<?> testClass) {
			// TODO Auto-generated method stub
			return new String[] {"db_test"};
		}
	

}
