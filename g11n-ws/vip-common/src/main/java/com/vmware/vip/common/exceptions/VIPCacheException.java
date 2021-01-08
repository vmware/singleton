/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.exceptions;

/**
 * API Exception for representing issues happen during API call.
 * 
 */
public class VIPCacheException extends Exception {

    private static final long serialVersionUID = 6254417482349012417L;

    public VIPCacheException() {
    }

    public VIPCacheException(String msg) {
        super(msg);
    }

    public VIPCacheException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
