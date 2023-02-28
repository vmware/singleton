/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.i18n.dto;

import org.json.simple.JSONObject;

import com.vmware.vip.common.constants.ConstantsKeys;
import com.vmware.vip.common.utils.LocaleUtils;

/**
 * This class represents the DTO for string-based translation.
 * 
 */
public class StringBasedDTO extends BaseDTO {

    private static final long serialVersionUID = 2670911000421739213L;

	// The source for translation
    private String source = "";

    // The key's translation
    private String translation = "";

    // The translation's locale
    private String locale = "";

    // The key string
    private String key = "";

    // The component contains the key
    private String component = "";

    // Translation status
    private String status = "";

	public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = LocaleUtils.normalizeToLanguageTag(locale);
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
    	if(translation != null) {
    		this.translation = translation;
    	}
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
        jo.put(ConstantsKeys.lOCALE, this.getLocale());
        jo.put(ConstantsKeys.SOURCE, this.getSource());
        jo.put(ConstantsKeys.TRANSLATION, this.getTranslation());
        jo.put(ConstantsKeys.KEY, this.getKey());
        jo.put(ConstantsKeys.COMPONENT, this.getComponent());
        return jo.toJSONString();
    }
}
