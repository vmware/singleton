/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.fetcher.monitor;

/**
 * The class represents a main function that startup a thread for translation update.
 * At present, this code is not used
 */
public class TranslationMonitor {

	public static void main(String[] args) {
		UpdateThread updateThread = new UpdateThread();
		Thread t1 = new Thread(updateThread);
		t1.start();
	}

}
