/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.l10n.source.dto;

import java.io.Serializable;

/**
 * Source-base DTO
 * 
 */
public class SourceBaseDTO implements Serializable{
    /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String productName = "";
    private String version = "";

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

}
