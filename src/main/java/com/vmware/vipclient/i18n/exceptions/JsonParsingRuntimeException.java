/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.exceptions;

public class JsonParsingRuntimeException extends RuntimeException {
    // the JSON that we attempted to parse
    private final String json;

    public JsonParsingRuntimeException(Exception e, String json) {
        super(e);
        this.json = json;
    }

    public JsonParsingRuntimeException(String json) {
        super();
        this.json = json;
    }

    public String getJson() {
        return this.json;
    }
}
