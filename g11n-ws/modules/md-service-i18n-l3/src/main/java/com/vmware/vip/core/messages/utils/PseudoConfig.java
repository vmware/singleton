/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.core.messages.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.vmware.vip.common.utils.LocaleUtils;

/**
 * This object is used to add tag for translation resource, it is init according
 * to the configuration file 'application.proerties'
 */
@Component
@ConfigurationProperties(prefix = "pseudo")
public class PseudoConfig {
	private boolean enabled;
	private String locale;
	private String existSourceTag;
	private String notExistSourceTag;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = LocaleUtils.normalizeToLanguageTag(locale);;
	}

	public String getExistSourceTag() {
		return existSourceTag;
	}

	public void setExistSourceTag(String existSourceTag) {
		this.existSourceTag = existSourceTag;
	}

	public String getNotExistSourceTag() {
		return notExistSourceTag;
	}

	public void setNotExistSourceTag(String notExistSourceTag) {
		this.notExistSourceTag = notExistSourceTag;
	}
}
