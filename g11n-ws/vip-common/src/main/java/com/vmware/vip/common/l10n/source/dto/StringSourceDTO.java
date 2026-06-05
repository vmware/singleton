/*
 * Copyright 2019-2025 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.l10n.source.dto;

import java.io.Serializable;

import org.json.JSONObject;

import com.vmware.vip.common.constants.ConstantsKeys;

/**
 * String-based Source DTO
 * 
 */
public class StringSourceDTO extends ComponentBaseDTO implements Serializable {

    private static final long serialVersionUID = 1L;


	private String key = "";
    /* English source string */
    private String source = "";

    /* The comments for source to help translation */
    private String comment = "";

    private String sourceFormat = "";

    public String getSourceFormat() {
		return sourceFormat;
	}

	public void setSourceFormat(String sourceFormat) {
		this.sourceFormat = sourceFormat;
	}

	public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @SuppressWarnings("unchecked")
    public String toJSONString() {
        JSONObject jo = new JSONObject();
        jo.put(ConstantsKeys.PRODUCTNAME, this.getProductName());
        jo.put(ConstantsKeys.COMPONENT, this.getComponent());
        jo.put(ConstantsKeys.VERSION, this.getVersion());
        jo.put(ConstantsKeys.KEY, this.getKey());
        jo.put(ConstantsKeys.SOURCE, this.getSource());
        jo.put(ConstantsKeys.lOCALE, this.getLocale());
        jo.put(ConstantsKeys.COMPONENT, this.getComponent());
        return jo.toString();
    }
}
