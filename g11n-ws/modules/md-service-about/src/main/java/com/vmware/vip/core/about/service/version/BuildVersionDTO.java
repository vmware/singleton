/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.about.service.version;

import java.io.Serializable;

/**
 * DTO object for build's version information.
 */
public class BuildVersionDTO implements Serializable {

    //property for store service's version information
    private ServiceVersionDTO service;

    //property for store product's bundle's version information
    private BundleVersionDTO bundle;

    public ServiceVersionDTO getService() {
        return service;
    }

    public void setService(ServiceVersionDTO service) {
        this.service = service;
    }

    public BundleVersionDTO getBundle() {
        return bundle;
    }

    public void setBundle(BundleVersionDTO bundle) {
        this.bundle = bundle;
    }
}
