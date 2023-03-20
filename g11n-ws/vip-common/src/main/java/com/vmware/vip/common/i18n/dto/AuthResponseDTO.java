/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import java.io.Serializable;

import com.vmware.vip.common.i18n.status.Response;

/**
 * This class is used for wrapping the business authentication API's response result.
 * 
 */
public class AuthResponseDTO implements Serializable {

    private static final long serialVersionUID = 2034311854372477252L;

    // Response status
    private Response response;

    // session ID for identifying client
    private String sessionID = "";

    // token
    private String token = "";

    // Used for identifying the data come from vIP server
    private String signature = "";

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response status) {
        this.response = status;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}
