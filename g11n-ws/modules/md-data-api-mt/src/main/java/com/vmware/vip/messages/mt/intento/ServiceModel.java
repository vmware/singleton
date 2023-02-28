/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */

package com.vmware.vip.messages.mt.intento;

import java.util.List;
import java.util.Map;

/**
 * A model to match the 'service' node in the request body
 */
public class ServiceModel {
    private boolean async;

    private List<String> provider;

    private Map<String, List<Map<String, String>>> auth;

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public List<String> getProvider() {
        return provider;
    }

    public void setProvider(List<String> provider) {
        this.provider = provider;
    }

    public Map<String, List<Map<String, String>>> getAuth() {
        return auth;
    }

    public void setAuth(Map<String, List<Map<String, String>>> auth) {
        this.auth = auth;
    }
}