/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.exceptions;

/**
 * Exception for representing issues happen during calling vIP server.
 * 
 */
public class VIPJavaClientTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 2715207175926260523L;

    public VIPJavaClientTimeoutException() {
        super();
    }

    public VIPJavaClientTimeoutException(String message) {
        super(message);
    }

    public VIPJavaClientTimeoutException(Throwable cause) {
        super(cause);
    }

    public VIPJavaClientTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public VIPJavaClientTimeoutException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
