/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vip.messages.mt.intento;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;

/**
 * A class to match the 'response' node in the Intento response
 */
public class Response {
    private List<String> results;

    private ObjectNode meta;

    private ObjectNode service;

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public ObjectNode getMeta() {
        return meta;
    }

    public void setMeta(ObjectNode meta) {
        this.meta = meta;
    }

    public ObjectNode getService() {
        return service;
    }

    public void setService(ObjectNode service) {
        this.service = service;
    }
}