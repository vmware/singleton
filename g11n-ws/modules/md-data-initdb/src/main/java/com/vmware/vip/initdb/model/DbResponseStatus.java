/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.initdb.model;

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
	private static final long serialVersionUID = -5562083882379185255L;
	private String status;
	private int code;
	private String desc;

	public DbResponseStatus(String result, int num) {
		this.status = result;
		this.code = num;
	}

	public DbResponseStatus(String result, int num, String description) {
		this.status = result;
		this.code = num;
		this.desc = description;
	}

	public DbResponseStatus() {

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
