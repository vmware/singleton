/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.exceptions;

public class JsonParsingException extends Exception {
    // the JSON that we attempted to parse
    private final String json;

    public JsonParsingException(Exception e, String json) {
        super(e);
        this.json = json;
    }

    public JsonParsingException(String json) {
        super();
        this.json = json;
    }

    public String getJson() {
        return this.json;
    }
}
