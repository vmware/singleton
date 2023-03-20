/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */


package com.vmware.vip.messages.mt.intento;

/**
 * A model to match the request body's data structure
 */
public class FromModel {
    private ContextModel context;

    public ContextModel getContext() {
        return context;
    }

    public void setContext(ContextModel context) {
        this.context = context;
    }

    public ServiceModel getService() {
        return service;
    }

    public void setService(ServiceModel service) {
        this.service = service;
    }

    private ServiceModel service;

    FromModel(ContextModel context, ServiceModel service) {
        this.context = context;
        this.service = service;
    }
}
