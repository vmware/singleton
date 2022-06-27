/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test.i18n;

import org.junit.Test;

import com.vmware.i18n.utils.PathUtils;

public class PathUtilsTest {
	
	@Test
	public void testGetResourcePath(){
		PathUtils.getResourcePath();
	}
	
	@Test
	public void testGetCoreResourcePath(){
		PathUtils.getCoreResourcePath();
	}
}
