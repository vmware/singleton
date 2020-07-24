/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.dto;

import com.vmware.vipclient.i18n.VIPCfg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocaleDTO extends BaseDTO {
    Logger         logger = LoggerFactory.getLogger(LocaleDTO.class);

    private String displayLanguage;

    public LocaleDTO() {
        super.setProductID(VIPCfg.getInstance().getProductName());
        super.setVersion(VIPCfg.getInstance().getVersion());
    }
    public LocaleDTO(String productName, String version) {
        this.setProductID(productName);
        this.setVersion(version);
    }

    public LocaleDTO(String productName, String version, String displayLanguage) {
        new LocaleDTO(productName, version);
        this.displayLanguage = displayLanguage;
    }

    public String getDisplayLanguage() {
        return displayLanguage;
    }

    public void setDisplayLanguage(String displayLanguage) {
        this.displayLanguage = displayLanguage.replace("_", "-").toLowerCase();
    }
}
