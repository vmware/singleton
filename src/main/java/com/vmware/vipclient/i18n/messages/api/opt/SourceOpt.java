/*
 * Copyright 2019 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt;

import java.util.Locale;

import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;

/**
 * The interface that defines source message access
 * e.g. ResourceBundleSrcOpt is the SourceOpt implementation that retrieves source messages from a .properties file. 
 * If source messages need to come from another location such as a DB, then have another implementation like DBSourceOpt.
 */
public interface SourceOpt {
	public void getComponentMessages(MessageCacheItem cacheItem);
	public String getMessage(String key);
	public Locale getLocale();
}
