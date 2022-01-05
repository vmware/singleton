/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.l10n.source.dto;

import java.io.Serializable;

import org.json.simple.JSONObject;

import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.utils.LocaleUtils;

/**
 * Component-based Source DTO, represents as one component.
 * 
 */
public class ComponentBaseDTO extends SourceBaseDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String locale = "";
	private String component = "";

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = LocaleUtils.normalizeToLanguageTag(locale);
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	@SuppressWarnings("unchecked")
	public String toJSONString() {
		JSONObject jo = new JSONObject();
		jo.put(ConstantsKeys.PRODUCTNAME, this.getProductName());
		jo.put(ConstantsKeys.VERSION, this.getVersion());
		jo.put(ConstantsKeys.COMPONENT, this.getComponent());
		return jo.toJSONString();
	}
}
