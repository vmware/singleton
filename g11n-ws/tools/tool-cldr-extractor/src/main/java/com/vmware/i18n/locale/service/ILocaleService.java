/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.locale.service;

public interface ILocaleService {

	public String getRegion(String language);

	public String getLanguage(String displayLanguage);

	String getLocaleWithDefaultRegion(String locale);

}
