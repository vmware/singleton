/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test.i18n;

import org.junit.Assert;
import org.junit.Test;

import com.vmware.i18n.utils.PathUtils;

public class PathUtilsTest {
	
	@Test
	public void testGetResourcePath(){
		
		String result = PathUtils.getResourcePath();
		Assert.assertNotNull(result);
	}
	
	@Test
	public void testGetCoreResourcePath(){
		String result = PathUtils.getCoreResourcePath();
		Assert.assertNotNull(result);
	}
}
