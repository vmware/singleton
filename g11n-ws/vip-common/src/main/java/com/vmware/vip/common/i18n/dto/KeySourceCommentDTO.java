/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import org.json.simple.JSONObject;

import com.vmware.vip.common.constants.ConstantsKeys;

/**
 * This class represents the DTO for key-source-comment.
 * 
 */
public class KeySourceCommentDTO {
	private String source = "";
	private String key = "";
	private String commentForSource = "";

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCommentForSource() {
		return commentForSource;
	}

	public void setCommentForSource(String commentForSource) {
		this.commentForSource = commentForSource;
	}

	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject jo = new JSONObject();
		jo.put(ConstantsKeys.SOURCE, this.getSource());
		jo.put(ConstantsKeys.KEY, this.getKey());
		jo.put(ConstantsKeys.COMMENT_FOR_SOURCE, this.getCommentForSource());
		return jo.toJSONString();
	}
}
