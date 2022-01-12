/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.l10n.exception;

import com.vmware.vip.common.exceptions.VIPAPIException;

public class L10nAPIException extends VIPAPIException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1223691544415557879L;

	public L10nAPIException() {
		super();
	}

	public L10nAPIException(String message) {
		super(message);
	}

	public L10nAPIException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
