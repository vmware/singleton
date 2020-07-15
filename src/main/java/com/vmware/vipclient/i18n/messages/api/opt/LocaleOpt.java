/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt;

import java.util.Map;

public interface LocaleOpt {
	public Map<String, String> getSupportedLanguages(String displayLanguage);
}
