/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.dto;

import com.vmware.vipclient.i18n.VIPCfg;

public class LocaleDTO extends BaseDTO{

    public LocaleDTO() {
        super.setProductID(VIPCfg.getInstance().getProductName());
        super.setVersion(VIPCfg.getInstance().getVersion());
    }

    public LocaleDTO(VIPCfg cfg) {
        if (cfg != null) {
            this.setProductID(cfg.getProductName());
            this.setVersion(cfg.getVersion());
        } else {
            super.setProductID(VIPCfg.getInstance().getProductName());
            super.setVersion(VIPCfg.getInstance().getVersion());
        }
    }
}
