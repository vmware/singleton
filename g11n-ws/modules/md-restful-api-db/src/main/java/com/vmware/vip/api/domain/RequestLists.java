/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vip.api.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 *
 * @author shihu
 *
 */
public class RequestLists implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1791451671181050033L;

	@JsonProperty("components")
	private List<String> components = new ArrayList<String>();

	@JsonProperty("locales")
	private List<String> locales = new ArrayList<String>();

	@JsonProperty("keys")
	private List<String> keys = new ArrayList<String>();

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public List<String> getComponents() {
		return components;
	}

	public void setComponents(List<String> components) {
		this.components = components;
	}

	public List<String> getLocales() {
		return locales;
	}

	public void setLocales(List<String> locales) {
		this.locales = locales;
	}

}
