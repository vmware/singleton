/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.initdb.model;

import java.io.File;
import java.io.Serializable;

/**
 * 
 *
 * @author shihu
 *
 */
public class TransCompDocFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7485184786407379678L;
	private String product;
	private String version;
	private String component;
	private String locale;
	private File docFile;

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

	public File getDocFile() {
		return docFile;
	}

	public void setDocFile(File docFile) {
		this.docFile = docFile;
	}

}
