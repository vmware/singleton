/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.model;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 *
 * @author shihu
 *
 */
public class ResultI18Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9124184172390126637L;

	private String product = "";
	private String version = "";
	private String component = "";
	private String locale = "";

	private Map<String, String> messages;

	public ResultI18Message(String product, String version, String component, String locale) {
		// TODO Auto-generated constructor stub
		this.product = product;
		this.version = version;
		this.component = component;
		this.locale = locale;

	}

	public ResultI18Message() {
	}

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

	public Map<String, String> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, String> messages) {
		this.messages = messages;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

}
