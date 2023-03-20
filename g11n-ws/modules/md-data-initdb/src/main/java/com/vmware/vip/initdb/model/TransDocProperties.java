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
public class TransDocProperties implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -244836492387114052L;

	private String product;
	private String version;
	private String component;
	private String locale;
	private String absPath;

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getAbsPath() {
		return absPath;
	}

	public void setAbsPath(String absPath) {
		this.absPath = absPath;
	}

}
