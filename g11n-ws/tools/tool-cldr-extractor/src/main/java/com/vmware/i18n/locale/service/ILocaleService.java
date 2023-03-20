/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.i18n.locale.service;

public interface ILocaleService {

	public String getLocaleData(String language, String filePath);

	String getLocaleWithDefaultRegion(String locale);

	String getContextTransforms(String locale);

}
