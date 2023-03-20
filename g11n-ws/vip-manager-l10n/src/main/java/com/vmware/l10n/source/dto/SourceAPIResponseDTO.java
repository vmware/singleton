/*
 * Copyright 2019-2022 VMware, Inc.
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
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response status) {
        this.response = status;
    }
}
