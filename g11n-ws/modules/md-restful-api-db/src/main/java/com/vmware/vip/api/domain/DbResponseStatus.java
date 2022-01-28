/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.domain;

import java.io.Serializable;

/**
 * 
 *
 * @author shihu
 *
 */
public class DbResponseStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3622821249645435399L;
	/**
	 * 
	 */

	private String status;
	private int code;
	private String desc = " ";
    private DbResponseStatus() {}
	private DbResponseStatus(String result, int num) {
		this.status = result;
		this.code = num;
	}

	private DbResponseStatus(String result, int num, String description) {
		this.status = result;
		this.code = num;
		this.desc = description;
	}

	public static DbResponseStatus respSuccess(int code) {
		return new DbResponseStatus("success", code, "this done successfully");
	}

	public static DbResponseStatus respSuccess(int code, String desc) {
		return new DbResponseStatus("success", code, desc);
	}

	public static DbResponseStatus respFailure(int code, String desc) {
		return new DbResponseStatus("failure", code, desc);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
