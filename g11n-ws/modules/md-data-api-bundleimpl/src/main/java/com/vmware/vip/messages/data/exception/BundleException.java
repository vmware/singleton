/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.exception;

import com.vmware.vip.messages.data.dao.exception.DataException;

public class BundleException extends DataException {

	private static final long serialVersionUID = 2012590835476758881L;

	public BundleException() {
		super();
	}

	public BundleException(String message) {
		super(message);
	}

	public BundleException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
