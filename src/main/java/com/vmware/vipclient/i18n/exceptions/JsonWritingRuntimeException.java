/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.exceptions;

public class JsonWritingRuntimeException extends RuntimeException {
    public JsonWritingRuntimeException(Exception e) {
        super(e);
    }
}
