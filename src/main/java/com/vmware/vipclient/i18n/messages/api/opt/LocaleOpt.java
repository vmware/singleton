/*
 * Copyright 2019-2020 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt;

import java.util.Map;

public interface LocaleOpt {
	public Map<String, String> getSupportedLanguages(String locale);
	public Map<String, String> getRegions(String locale);
}
