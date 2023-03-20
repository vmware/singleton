/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.exception;

import com.vmware.vip.messages.data.dao.exception.DataException;

/**
 * 
 *
 *
 *
 * @author shihu
 *
 */
public class ProductUnregisteredException extends DataException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7117957365150535652L;

	public ProductUnregisteredException() {
		super();
	}

	public ProductUnregisteredException(String message) {
		super(message);
	}

	public ProductUnregisteredException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}
