/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.l10agent.model;

import java.io.Serializable;
import java.util.Map;
/**
 * 
 *
 * @author shihu
 *
 */
public class ComponentSourceModel implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3020200175972875633L;
	private String product;
	private String version;
	private String component;
	private String locale;
	private Map<String,Object> messages;
	
	
	
	
	
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
	public Map<String, Object> getMessages() {
		return messages;
	}
	public void setMessages(Map<String, Object> messages) {
		this.messages = messages;
	}
	
	public boolean isMessageNotNull() {
		if(this.messages !=null && !(this.messages.isEmpty())){
			return true;
		}else {
			return false;
		}
	}
}
