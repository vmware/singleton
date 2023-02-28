/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.exceptions;

import com.vmware.vip.common.exceptions.VIPAPIException;

public class ValidationException  extends VIPAPIException {

	private static final long serialVersionUID = 4051918154967007725L;

	public ValidationException() {
		super();
	}

	public ValidationException(String message) {
		super(message);
	}

    public ValidationException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
