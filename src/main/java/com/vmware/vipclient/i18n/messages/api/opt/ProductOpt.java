/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt;

import java.util.List;

public interface ProductOpt {
	public List<String> getSupportedLocales();
	public List<String> getComponents();
}
