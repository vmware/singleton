/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vip.messages.mt.intento;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * A class to match the response structure
 */
public class ToResult {
    private String id;

    private boolean done;

    private JsonNode error;

    private List<Response> response;

    private JsonNode meta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public JsonNode getError() {
        return error;
    }

    public void setError(JsonNode error) {
        this.error = error;
    }

    public List<Response> getResponse() {
        return response;
    }

    public void setResponse(List<Response> response) {
        this.response = response;
    }

    public JsonNode getMeta() {
        return meta;
    }

    public void setMeta(JsonNode meta) {
        this.meta = meta;
    }
}
