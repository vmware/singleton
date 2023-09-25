/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.dto;

import com.vmware.vipclient.i18n.VIPCfg;

/**
 * DTO objects for cache data encapsulation
 *
 */
public class BaseDTO {
    private String productID;
    private String version;

    public BaseDTO() {
        this.productID = VIPCfg.getInstance().getProductName();
        this.version = VIPCfg.getInstance().getVersion();
    }

    public BaseDTO(String productID, String version) {
        this.productID = productID;
        this.version = version;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
