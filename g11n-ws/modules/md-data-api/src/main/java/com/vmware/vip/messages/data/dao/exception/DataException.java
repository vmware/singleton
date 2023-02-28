/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.exception;

/**
 * 
 *
 * @author shihu
 *
 */
public class DataException extends Exception {
	private static final long serialVersionUID = -4408979501427331605L;

	public DataException() {
		super();
	}

	public DataException(String message) {
		super(message);
	}

	public DataException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
