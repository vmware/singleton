/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.test.i18n;

import org.junit.Test;

import com.vmware.i18n.PatternConfig;

public class PatternConfigTest {
	@Test
    public void testSetPatternPath() {
		PatternConfig.getInstance().setPatternPath("test");
    }

}
