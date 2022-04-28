/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.utils.timezone;

import java.io.Serializable;

public class TimeZoneDisplayName implements Serializable {
 
	private static final long serialVersionUID = -2262536734862076297L;
	private String standard;
	private String daylight;
	private String generic;

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public String getDaylight() {
		return daylight;
	}

	public void setDaylight(String daylight) {
		this.daylight = daylight;
	}

	public String getGeneric() {
		return generic;
	}

	public void setGeneric(String generic) {
		this.generic = generic;
	}
}
