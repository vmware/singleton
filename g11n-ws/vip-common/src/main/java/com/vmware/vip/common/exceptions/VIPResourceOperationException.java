/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.exceptions;

/**
 * Resource operation exception for representing issues happen during resource operation.
 * 
 */
public class VIPResourceOperationException extends Exception {

    private static final long serialVersionUID = 6254417482349012417L;

    public VIPResourceOperationException() {
    }

    public VIPResourceOperationException(String msg) {
        super(msg);
    }

    public VIPResourceOperationException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
