/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt;

import java.util.Locale;

import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;

public interface SourceOpt {
	public void getComponentMessages(MessageCacheItem cacheItem);
	public String getMessage(String key);
	public Locale getLocale();
}
