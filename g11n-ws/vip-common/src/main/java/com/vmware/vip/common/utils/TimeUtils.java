/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

	private TimeUtils() {}
	
	public static void sleep(long millisecond) {
		try {
			TimeUnit.MILLISECONDS.sleep(millisecond);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
