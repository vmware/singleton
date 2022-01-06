/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt;

import java.util.List;

import com.vmware.vipclient.i18n.base.cache.MessageCacheItem;

public interface ProductOpt {
	void getSupportedLocales(MessageCacheItem cacheItem);
	List<String> getComponents();
}
