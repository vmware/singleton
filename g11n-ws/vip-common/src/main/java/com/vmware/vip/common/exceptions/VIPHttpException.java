/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.exceptions;

/**
 * API Exception for representing issues happen during API call.
 * 
 */
public class VIPHttpException extends Exception {

    private static final long serialVersionUID = 6254417482349012417L;

    public VIPHttpException() {
    }

    public VIPHttpException(String msg) {
        super(msg);
    }

    public VIPHttpException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
