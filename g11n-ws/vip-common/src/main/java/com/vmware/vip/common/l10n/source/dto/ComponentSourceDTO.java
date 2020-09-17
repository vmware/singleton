/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.l10n.source.dto;

import java.io.Serializable;

import org.json.simple.JSONObject;

import com.vmware.vip.common.constants.ConstantsKeys;

/**
 * Component-based Source DTO with messages and comments attribute, represents
 * as one component.
 * 
 */
public class ComponentSourceDTO extends ComponentBaseDTO implements
		Serializable {

	private static final long serialVersionUID = 1L;

	/* put translation key-value pairs to this messages */
	private JSONObject messages = new JSONObject();

	/* put comments key-value pairs to this messages */
	private JSONObject comments = new JSONObject();

	public JSONObject getMessages() {
		return messages;
	}

	@SuppressWarnings("unchecked")
	public void setMessages(String key, String message) {
		this.messages.put(key, message);
	}

	public JSONObject getComments() {
		return comments;
	}

	@SuppressWarnings("unchecked")
	public void setComments(String key, String comment) {
		this.comments.put(key, comment);
	}

	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject jo = new JSONObject();
		jo.put(ConstantsKeys.PRODUCTNAME, this.getProductName());
		jo.put(ConstantsKeys.VERSION, this.getVersion());
		jo.put(ConstantsKeys.MESSAGES, this.getMessages().toJSONString());
		jo.put(ConstantsKeys.COMPONENT, this.getComponent());
		return jo.toJSONString();
	}
}
