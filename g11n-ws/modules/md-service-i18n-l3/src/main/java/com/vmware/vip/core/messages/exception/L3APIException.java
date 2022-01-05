/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.exception;

import com.vmware.vip.common.exceptions.VIPAPIException;

/**
 * For L3 API exception
 */
public class L3APIException extends VIPAPIException {

	private static final long serialVersionUID = -4408979501427331605L;

	public L3APIException() {
		super();
	}

	public L3APIException(String message) {
		super(message);
	}

    public L3APIException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
