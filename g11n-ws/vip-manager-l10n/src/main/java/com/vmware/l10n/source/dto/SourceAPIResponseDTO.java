/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.dto;

import java.io.Serializable;

import com.vmware.vip.common.i18n.status.Response;

/**
 * This class defines the response content of collecting source strings API.
 */
public class SourceAPIResponseDTO implements Serializable {

    private static final long serialVersionUID = -6587672782896132155L;

    /** the base response content, includes response code and message **/
    private Response status;

    public Response getStatus() {
        return status;
    }

    public void setStatus(Response status) {
        this.status = status;
    }
}
