/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.auth;

import com.vmware.vip.common.exceptions.VIPAPIException;

public class AuthenException  extends VIPAPIException {

	private static final long serialVersionUID = -5424841073537001856L;

	public AuthenException() {
		super();
	}

	public AuthenException(String message) {
		super(message);
	}

    public AuthenException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
