/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test;

import org.junit.Test;

import com.vmware.l10n.utils.SourceUtils;

public class TestUtils {
	  @Test
	   public void test001sourceUtils() {
		  SourceUtils.createSourceDTO("TEST", "1.0.0", "test", "en", "test.key", "this is test", "", "");
		  SourceUtils.handleSourceResponse(true);
		  SourceUtils.handleSourceResponse(false);
	  }

}
