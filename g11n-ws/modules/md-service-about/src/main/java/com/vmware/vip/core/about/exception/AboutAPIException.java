/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.about.exception;

import com.vmware.vip.common.exceptions.VIPAPIException;

/**
 * Exception class for About API
 */
public class AboutAPIException extends VIPAPIException {

    public AboutAPIException() {
        super();
    }

    public AboutAPIException(String msg) {
        super(msg);
    }

    public AboutAPIException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
