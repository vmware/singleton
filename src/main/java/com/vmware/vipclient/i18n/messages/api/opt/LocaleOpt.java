/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt;

import com.vmware.vipclient.i18n.base.cache.LocaleCacheItem;

public interface LocaleOpt {
	public void getSupportedLanguages(String locale, LocaleCacheItem cacheItem);
	public void getRegions(String locale, LocaleCacheItem cacheItem);
}
