/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.synch.model;

import java.io.Serializable;
import java.util.TreeMap;

public class SyncI18nMsg implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2886376274155882806L;
	private String product = "";
	private String version = "";
	private String component = "";
	private String locale = "";
	
	private TreeMap<String, String> messages = new TreeMap<String, String>();

	public String getProduct() {
		return product;
	}

	public String getVersion() {
		return version;
	}

	public String getComponent() {
		return component;
	}

	public String getLocale() {
		return locale;
	}

	public TreeMap<String, String> getMessages() {
		return messages;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setMessages(TreeMap<String, String> messages) {
		this.messages = messages;
	}
}
