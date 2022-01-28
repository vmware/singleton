/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.l2.service.locale;

import java.io.Serializable;

public class DisplayLanguageDTO implements Serializable {

	private static final long serialVersionUID = 2804097065189469885L;

	private String languageTag;

	private String displayName;

	private String displayName_sentenceBeginning;

	private String displayName_uiListOrMenu;

	private String displayName_standalone;

	public String getLanguageTag() {
		return languageTag;
	}

	public void setLanguageTag(String languageTag) {
		this.languageTag = languageTag;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName_sentenceBeginning() {
		return displayName_sentenceBeginning;
	}

	public void setDisplayName_sentenceBeginning(String displayName_sentenceBeginning) {
		this.displayName_sentenceBeginning = displayName_sentenceBeginning;
	}

	public String getDisplayName_uiListOrMenu() {
		return displayName_uiListOrMenu;
	}

	public void setDisplayName_uiListOrMenu(String displayName_uiListOrMenu) {
		this.displayName_uiListOrMenu = displayName_uiListOrMenu;
	}

	public String getDisplayName_standalone() {
		return displayName_standalone;
	}

	public void setDisplayName_standalone(String displayName_standalone) {
		this.displayName_standalone = displayName_standalone;
	}
}
