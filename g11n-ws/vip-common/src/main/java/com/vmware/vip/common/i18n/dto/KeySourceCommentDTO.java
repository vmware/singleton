/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import org.json.JSONObject;

import com.vmware.vip.common.constants.ConstantsKeys;

/**
 * This class represents the DTO for key-source-comment.
 * 
 */
public class KeySourceCommentDTO {
	private String source = "";
	private String key = "";
	private String commentForSource = "";
	private String sourceFormat = "";

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

	public String getSourceFormat() {
		return sourceFormat;
	}

	public void setSourceFormat(String sourceFormat) {
		this.sourceFormat = sourceFormat;
	}
	
	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject jo = new JSONObject();
		jo.put(ConstantsKeys.SOURCE, this.getSource());
		jo.put(ConstantsKeys.KEY, this.getKey());
		jo.put(ConstantsKeys.COMMENT_FOR_SOURCE, this.getCommentForSource());
		jo.put(ConstantsKeys.SOURCE_FORMAT, this.getSourceFormat());
		return jo.toString();
	}

	
}
