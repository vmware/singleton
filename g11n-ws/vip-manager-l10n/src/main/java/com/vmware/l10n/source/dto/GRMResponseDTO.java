/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10n.source.dto;

import java.io.Serializable;

/**
 * This class defines the response format of GRM API.
 */
public class GRMResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** the response creation time **/
    long timestamp;

    /** the response code **/
    int status;

    /** the return message when the response is correct **/
    Object result;

    /** the return message when the response is error **/
    String errorMessage = "";

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
