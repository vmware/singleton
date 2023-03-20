/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.mt.azure;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ToTextModel {
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	ObjectNode transliteration;

	public ObjectNode getTransliteration() {
		return transliteration;
	}

	public void setTransliteration(ObjectNode transliteration) {
		this.transliteration = transliteration;
	}

	String text;
	String to;
}
