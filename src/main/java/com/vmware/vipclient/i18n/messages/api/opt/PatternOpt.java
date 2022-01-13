/*
 * Copyright 2019-2022 VMware, Inc.
 * SPDX-License-Identifier: EPL-2.0
 */
package com.vmware.vipclient.i18n.messages.api.opt;

import com.vmware.vipclient.i18n.base.cache.PatternCacheItem;

public interface PatternOpt {
    public void getPatterns(String locale, PatternCacheItem cacheItem);
    public void getPatterns(String language, String region, PatternCacheItem cacheItem);
}
