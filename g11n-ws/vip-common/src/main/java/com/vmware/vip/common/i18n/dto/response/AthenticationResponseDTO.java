/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto.response;

import java.io.Serializable;

import com.vmware.vip.common.i18n.status.Response;

/**
 * This class is used for wrapping the key authentication API's response result.
 * 
 */
public class AthenticationResponseDTO implements Serializable {

    private static final long serialVersionUID = 7864748057604065905L;

    // Response status
    private Response response;

    // API's result data
    private Object data;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response status) {
        this.response = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
    	if(data == null) {
    		this.data = "";
    	} else {
            this.data = data;
    	}
    }

}
