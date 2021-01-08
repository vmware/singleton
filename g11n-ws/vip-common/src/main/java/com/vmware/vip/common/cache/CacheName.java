/*
 * Copyright 2019-2021 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.common.cache;

/**
 * Enumerations of querying cache name.
 * 
 */
public enum CacheName {

	DEFAULT("DEFAULT"), ONECOMPONENT("ONECOMPONENT"), MULTCOMPONENT(
			"MULTCOMPONENT"), SOURCE("SOURCE"), SOURCEBACKUP("SOURCEBACKUP"), TOKEN(
			"TOKEN"), MT("MT"), MTSOURCE("MTSOURCE"), REGION("REGION"), LANGUAGE("LANGUAGE"), PATTERN("PATTERN");

	private String name;

	private CacheName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
