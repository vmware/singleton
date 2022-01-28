/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.mt.azure;

import com.fasterxml.jackson.databind.node.ArrayNode;

public class ToResult {
	public ToLangModel getDetectedLanguage() {
		return detectedLanguage;
	}

	public void setDetectedLanguage(ToLangModel detectedLanguage) {
		this.detectedLanguage = detectedLanguage;
	}

	public ArrayNode getTranslations() {
		return translations;
	}

	public void setTranslations(ArrayNode translations) {
		this.translations = translations;
	}

	ToLangModel detectedLanguage;
	ArrayNode translations;
}
