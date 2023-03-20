/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.messages.mt.transportal;

import java.util.List;

public class FromModel {
	public String getFromLang() {
		return fromLang;
	}

	public void setFromLang(String fromLang) {
		this.fromLang = fromLang;
	}

	public String getToLang() {
		return toLang;
	}

	public void setToLang(String toLang) {
		this.toLang = toLang;
	}

	private String fromLang;
	private String toLang;
	private List<String> sourceList;

	public List<String> getSourceList() {
		return sourceList;
	}

	public void setSourceList(List<String> sourceList) {
		this.sourceList = sourceList;
	}
}
