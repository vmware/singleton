/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.exceptions;

/**
 * API Exception for representing issues happen during API call.
 * 
 */
public class VIPAPIException extends Exception {

    private static final long serialVersionUID = 6254417482349012417L;

    public VIPAPIException() {
    }

    public VIPAPIException(String msg) {
        super(msg);
    }

    public VIPAPIException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
