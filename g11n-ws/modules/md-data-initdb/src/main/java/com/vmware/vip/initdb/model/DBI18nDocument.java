/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.initdb.model;

/**
 * 
 *
 * @author shihu
 *
 */
import java.io.Serializable;
import java.util.Map;

public class DBI18nDocument implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8777822806792134840L;
	/**
	 * 
	 */

	private String product;
	private String version;
	private String component;
	private Map<String, String> messages;
	private String locale;

	public DBI18nDocument(String product, String version, String component, String locale) {
		// TODO Auto-generated constructor stub
		this.product = product;
		this.version = version;
		this.component = component;
		this.locale = locale;

	}

	public DBI18nDocument() {
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

	public String toString() {
		StringBuilder sbBuilder = new StringBuilder();
		sbBuilder.append(product);
		sbBuilder.append("---");
		sbBuilder.append(version);
		sbBuilder.append("---");
		sbBuilder.append(component);
		sbBuilder.append("---");
		sbBuilder.append(locale);

		/*
		 * if(messages != null) { for(Entry<String, String> entry: messages.entrySet())
		 * { sbBuilder.append(entry.getKey()+":"+entry.getValue()+"\n"); } }
		 */

		return sbBuilder.toString();

	}

}
