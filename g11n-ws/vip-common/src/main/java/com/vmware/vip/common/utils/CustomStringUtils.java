/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * File utility class for common operation to files
 *
 */
public class CustomStringUtils {

	/**
	 * split string with commas
	 *
	 * @param str
	 *            string with commas
	 * @return string list
	 */
	public static String[] splitCommasSeperatedString(String str) {
		String[] ret;
		if (StringUtils.isBlank(str)) {
			ret = new String[] { "" };
		} else {
			ret = str.split(",");
		}
		return ret;
	}

	/**
	 * connect string array(e.g. {"a", "b", "c"} ) to a string(e.g. "a,b,c")
	 *
	 * @param strs
	 *            string array
	 * @return string
	 */
/*	public static String connectStringArrayAsString(String[] strs) {
		StringBuilder sb = new StringBuilder();
		for (String str : strs) {
			sb.append(str).append(",");
		}
		return sb.substring(0, sb.lastIndexOf(","));
	}*/
}
