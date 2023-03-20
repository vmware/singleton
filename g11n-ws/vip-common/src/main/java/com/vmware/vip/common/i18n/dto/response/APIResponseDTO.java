/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto.response;

import com.vmware.vip.common.i18n.status.APIResponseStatus;
import com.vmware.vip.common.i18n.status.Response;

import java.io.Serializable;

/**
 * This class is used for wrapping the general API's response result.
 * 
 */
public class APIResponseDTO implements Serializable {

    private static final long serialVersionUID = -7840065330485133664L;

    // Response status
    private Response response;

    // Used for identifying the data come from vIP server
    private String signature = "";

    // API's business result data
    private Object data;

    public APIResponseDTO() {
        this.response = APIResponseStatus.OK;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response status) {
        this.response = status;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
    	if(signature == null) {
    		this.signature = "";
    	} else {
            this.signature = signature;    		
    	}
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
