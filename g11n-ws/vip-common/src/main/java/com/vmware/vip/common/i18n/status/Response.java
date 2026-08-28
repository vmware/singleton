/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.status;

import java.io.Serializable;

import org.json.JSONObject;

import com.vmware.vip.common.constants.ConstantsKeys;

/**
 * Base status for API response
 *
 */
public class Response implements Serializable {

	private static final long serialVersionUID = -6345018856340614690L;

	/* The error code to identify the status */
	private Integer code;

	/* The error message for the details of the status */
	private String message = "";

	/* server time to track the response time */
	private String serverTime = "";

	public String getServerTime() {
		return serverTime;
	}

	public void setServerTime(String serverTime) {
		this.serverTime = "";
	}

	public Response() {
	}

	public Response(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		if (message == null) {
			this.message = "";
		} else {
			this.message = message;
		}
	}

	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject jo = new JSONObject();
		jo.put(ConstantsKeys.CODE, this.getCode());
		jo.put(ConstantsKeys.MESSAGE, this.getMessage());
		return jo.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((serverTime == null) ? 0 : serverTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (this.getClass() == obj.getClass()) {
			return this.code.intValue() == ((Response) obj).getCode().intValue();
		} else if (obj instanceof Response) {
			return this.code.intValue() == ((Response) obj).getCode().intValue();
		} else {
			return false;
		}
	}
}
