/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * support list
 *
 */
public interface ConstantsSupportList {
	//the support Source Format set
	public final static Set<String> SOURCE_FORMAT_LIST = new HashSet<String>(Arrays.asList("SVG", "MD", "HTML"));
	
	
}
