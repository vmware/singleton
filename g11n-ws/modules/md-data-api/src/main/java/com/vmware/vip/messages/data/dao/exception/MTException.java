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
public class MTException extends Exception {

	private static final long serialVersionUID = -7277531804051536496L;

	public MTException() {
		super();
	}

	public MTException(String message) {
		super(message);
	}

	public MTException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
