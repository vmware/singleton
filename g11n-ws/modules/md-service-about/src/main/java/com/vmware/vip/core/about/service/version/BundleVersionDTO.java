package com.vmware.vip.core.about.service.version;

/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
import java.io.Serializable;

/**
 * DTO for storing version information of product's translation bundle
 */
public class BundleVersionDTO implements Serializable {
    //product's name
    private String productName;

    //product's version
    private String version;

    //job id of product's translation bundle
    private String changeId;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChangeId() {
        return changeId;
    }

    public void setChangeId(String changeId) {
        this.changeId = changeId;
    }
}
