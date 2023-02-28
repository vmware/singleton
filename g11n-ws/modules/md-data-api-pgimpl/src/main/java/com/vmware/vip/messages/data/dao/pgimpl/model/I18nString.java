/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.data.dao.pgimpl.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author shihu
 *
 */
public class I18nString implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3645665133081364932L;

	public I18nString(String product, String version, String component, String locale, String key) {
		// TODO Auto-generated constructor stub
		this.product = product;
		this.version = version;
		this.component = component;
		this.locale = locale;
		this.keys = new ArrayList<String>();
		this.keys.add(key);

	}

	public I18nString(String product, String version, String component, String locale) {
		// TODO Auto-generated constructor stub
		this.product = product;
		this.version = version;
		this.component = component;
		this.locale = locale;

	}

	public I18nString(String product, String version, String component, String locale, Map<String, String> msgs) {
		// TODO Auto-generated constructor stub
		this.product = product;
		this.version = version;
		this.component = component;
		this.locale = locale;
		this.messages = msgs;

	}

	public I18nString() {
	}

	private String product;
	private String version;
	private String component;
	private Map<String, String> messages;
	private String locale;
	private List<String> keys;

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

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

}
