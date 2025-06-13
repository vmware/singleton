/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.l10n.source.dto;

import java.io.Serializable;

import org.json.JSONObject;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
	
	/* put source format key-value pairs to this messages */
	private JSONObject sourceFormats = new JSONObject();

	@JsonSerialize(using = JSONObjectSerializer.class)
	@JsonDeserialize(using = JSONObjectDeserializer.class)
	public JSONObject getMessages() {
		return messages;
	}

	@SuppressWarnings("unchecked")
	public void setMessages(String key, String message) {
		this.messages.put(key, message);
	}

	@JsonSerialize(using = JSONObjectSerializer.class)
	@JsonDeserialize(using = JSONObjectDeserializer.class)
	public JSONObject getComments() {
		return comments;
	}

	@SuppressWarnings("unchecked")
	public void setComments(String key, String comment) {
		this.comments.put(key, comment);
	}
	
	@JsonSerialize(using = JSONObjectSerializer.class)
	@JsonDeserialize(using = JSONObjectDeserializer.class)
	public JSONObject getSourceFormats() {
		return sourceFormats;
	}

	@SuppressWarnings("unchecked")
	public void setSourceFormats(String key, String sourceFormat) {
		this.sourceFormats.put(key, sourceFormat);
	}

	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject jo = new JSONObject();
		jo.put(ConstantsKeys.PRODUCTNAME, this.getProductName());
		jo.put(ConstantsKeys.VERSION, this.getVersion());
		jo.put(ConstantsKeys.MESSAGES, this.getMessages().toString());
		jo.put(ConstantsKeys.COMPONENT, this.getComponent());
		return jo.toString();
	}

	
}
