/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.exception;

import com.vmware.vip.common.exceptions.VIPAPIException;

/**
 * For L3 API exception
 */
public class L2APIException extends VIPAPIException {

	private static final long serialVersionUID = -4408979501427331605L;

	public L2APIException() {
		super();
	}

	public L2APIException(String message) {
		super(message);
	}

    public L2APIException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
